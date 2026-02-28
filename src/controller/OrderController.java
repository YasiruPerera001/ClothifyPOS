package controller;

import db.DBConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderController {

    @FXML
    private ComboBox<Product> productCombo;

    @FXML
    private TextField qtyField;

    @FXML
    private TableView<Product> cartTable;

    @FXML
    private TableColumn<Product, String> colProdName;
    @FXML
    private TableColumn<Product, Integer> colQty;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Double> colTotal;

    @FXML
    private Label totalLabel;

    private ObservableList<Product> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadProducts();

        productCombo.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product == null) return "";
                return product.getName(); // show product name
            }

            @Override
            public Product fromString(String string) {
                return null; // not needed
            }
        });

        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(cellData ->
                javafx.beans.property.SimpleDoubleProperty
                        .doubleProperty(new SimpleDoubleProperty(cellData.getValue().getPrice() * cellData.getValue().getQuantity()).asObject()).asObject()
        );
        cartTable.setItems(cartList);
    }

    private void loadProducts() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM products");

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                productCombo.getItems().add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToCart() {
        Product selected = productCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText());
            if(qty <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Enter a valid quantity!");
            alert.show();
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            // Get latest stock from DB
            PreparedStatement pst = conn.prepareStatement("SELECT quantity, price FROM products WHERE product_id=?");
            pst.setInt(1, selected.getProductId());
            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                int availableStock = rs.getInt("quantity");
                double latestPrice = rs.getDouble("price");

                // Check if product is already in cart
                Product cartItem = null;
                for(Product p : cartList){
                    if(p.getProductId() == selected.getProductId()){
                        cartItem = p;
                        break;
                    }
                }

                int totalQty = qty;
                if(cartItem != null){
                    totalQty += cartItem.getQuantity(); // total quantity including existing in cart
                }

                if(totalQty > availableStock){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Not enough stock! Available: " + availableStock);
                    alert.show();
                    return;
                }

                if(cartItem != null){
                    // update quantity in existing cart item
                    cartItem.setQuantity(cartItem.getQuantity() + qty);
                    cartTable.refresh();
                } else {
                    // add new item to cart
                    Product newItem = new Product(selected.getProductId(), selected.getName(),
                            selected.getCategory(), latestPrice, qty);
                    cartList.add(newItem);
                }

                updateTotal();
                qtyField.clear();
            }

        } catch(SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Database error while adding to cart!");
            alert.show();
        }
    }

    @FXML
    private void checkout() {
        if(cartList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Cart is empty!");
            alert.show();
            return;
        }

        double total = 0;

        try {
            Connection conn = DBConnection.getConnection();

            // 1️⃣ Check stock for each cart item using latest DB values
            for(Product p : cartList){
                PreparedStatement pst = conn.prepareStatement("SELECT quantity FROM products WHERE product_id=?");
                pst.setInt(1, p.getProductId());
                ResultSet rs = pst.executeQuery();
                if(rs.next()){
                    int available = rs.getInt("quantity");
                    if(p.getQuantity() > available){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText(null);
                        alert.setContentText("Not enough stock for " + p.getName() + ". Available: " + available);
                        alert.show();
                        return;
                    }
                }
                total += p.getPrice() * p.getQuantity();
            }

            // 2️⃣ Insert into orders table
            String orderSql = "INSERT INTO orders(date, total) VALUES(?, ?)";
            PreparedStatement orderPst = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPst.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            orderPst.setDouble(2, total);
            orderPst.executeUpdate();

            ResultSet rsOrder = orderPst.getGeneratedKeys();
            int orderId = 0;
            if(rsOrder.next()){
                orderId = rsOrder.getInt(1);
            }

            // 3️⃣ Insert each cart item into order_items table
            String itemSql = "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES(?, ?, ?, ?)";
            PreparedStatement itemPst = conn.prepareStatement(itemSql);

            // 4️⃣ Update products stock safely
            String updateSql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
            PreparedStatement updatePst = conn.prepareStatement(updateSql);

            for(Product p : cartList){
                itemPst.setInt(1, orderId);
                itemPst.setInt(2, p.getProductId());
                itemPst.setInt(3, p.getQuantity());
                itemPst.setDouble(4, p.getPrice());
                itemPst.addBatch();

                updatePst.setInt(1, p.getQuantity());
                updatePst.setInt(2, p.getProductId());
                updatePst.addBatch();
            }

            itemPst.executeBatch();
            updatePst.executeBatch();

            // Success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Order placed successfully!\nTotal: " + total);
            alert.show();

            cartList.clear();
            totalLabel.setText("0.0");

        } catch (SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Database error during checkout!");
            alert.show();
        }
    }

    private int getProductStock(int productId){
        int stock = 0;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT quantity FROM products WHERE product_id=?");
            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                stock = rs.getInt("quantity");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return stock;
    }

    private void updateTotal() {
        double total = 0;
        for (Product p : cartList) {
            total += p.getPrice() * p.getQuantity();
        }
        totalLabel.setText(String.valueOf(total));
    }

}