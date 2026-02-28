package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML
    private void openProducts() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Products.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Products Management");
        stage.show();
    }

    @FXML
    private void openOrders() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Orders.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Orders / POS");
        stage.show();
    }

    @FXML
    private void openReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reports.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sales Report");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customers.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Customer Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openEmployees() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/employees.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Employees Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void openSuppliers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/suppliers.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Suppliers Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
