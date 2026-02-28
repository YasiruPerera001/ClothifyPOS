package controller;

import db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;

import java.sql.*;

public class ProductController {

    @FXML
    private TextField nameField, categoryField, priceField, quantityField;

    @FXML
    private Button addBtn, updateBtn, deleteBtn;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadProducts();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                categoryField.setText(newSelection.getCategory());
                priceField.setText(String.valueOf(newSelection.getPrice()));
                quantityField.setText(String.valueOf(newSelection.getQuantity()));
            }
        });
    }

    private void loadProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM products");

            while(rs.next()){
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }

            productTable.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addProduct() {
        String name = nameField.getText();
        String category = categoryField.getText();
        String priceText = priceField.getText();
        String quantityText = quantityField.getText();

        if(name.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all fields");
            alert.show();
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO products(name, category, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, category);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Product added successfully!");
            alert.show();

            // Refresh table
            loadProducts();

            // Clear fields
            nameField.clear();
            categoryField.clear();
            priceField.clear();
            quantityField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Price and Quantity must be numbers!");
            alert.show();
        }
    }

    @FXML
    private void updateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select a product to update");
            alert.show();
            return;
        }

        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE products SET name=?, category=?, price=?, quantity=? WHERE product_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, category);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.setInt(5, selected.getProductId());
            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Product updated successfully!");
            alert.show();

            loadProducts();
            nameField.clear();
            categoryField.clear();
            priceField.clear();
            quantityField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Price and Quantity must be numbers!");
            alert.show();
        }
    }

    @FXML
    private void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select a product to delete");
            alert.show();
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM products WHERE product_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, selected.getProductId());
            pst.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Product deleted successfully!");
            alert.show();

            loadProducts();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}