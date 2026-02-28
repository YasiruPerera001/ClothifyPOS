package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.SaleReport;
import db.DBConnection;

import java.sql.*;

public class ReportsController {

    @FXML
    private TableView<SaleReport> salesTable;

    @FXML
    private TableColumn<SaleReport, Integer> colOrderId;

    @FXML
    private TableColumn<SaleReport, Date> colDate;

    @FXML
    private TableColumn<SaleReport, String> colProduct;

    @FXML
    private TableColumn<SaleReport, Integer> colQty;

    @FXML
    private TableColumn<SaleReport, Double> colPrice;

    @FXML
    private TableColumn<SaleReport, Double> colTotal;

    @FXML
    public void initialize() {
        loadSalesReport();
    }

    public void loadSalesReport() {
        ObservableList<SaleReport> list = FXCollections.observableArrayList();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT o.order_id, o.date, p.name, oi.quantity, oi.price, (oi.quantity*oi.price) AS total " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "ORDER BY o.order_id";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()) {
                list.add(new SaleReport(
                        rs.getInt("order_id"),
                        rs.getDate("date"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        salesTable.setItems(list);
    }
}