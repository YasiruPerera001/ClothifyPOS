package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import db.DBConnection;

import java.sql.*;

public class CustomerController {

    @FXML
    private TextField txtName, txtPhone, txtEmail;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableColumn<Customer, String> colEmail;

    private ObservableList<Customer> customerList;

    @FXML
    public void initialize() {
        loadCustomers();

        // Select row in table to edit
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null){
                txtName.setText(newSelection.getName());
                txtPhone.setText(newSelection.getPhone());
                txtEmail.setText(newSelection.getEmail());
            }
        });
    }

    public void loadCustomers() {
        customerList = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            while(rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        customerTable.setItems(customerList);
    }

    @FXML
    public void addCustomer() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        // Validation: Name and Phone are required
        if(name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Name is required!");
            return;
        }
        if(phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Phone number is required!");
            return;
        }

        // Optional: validate phone is numeric
        if(!phone.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Phone number must contain only digits!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO customers(name, phone, email) VALUES(?, ?, ?)"
            );
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setString(3, email.isEmpty() ? null : email); // email can be null
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Customer added successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    @FXML
    public void updateCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            showAlert(Alert.AlertType.ERROR, "Select a customer to update");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE customers SET name=?, phone=?, email=? WHERE customer_id=?"
            );
            pst.setString(1, txtName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, txtEmail.getText());
            pst.setInt(4, selected.getCustomerId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Customer updated successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if(selected == null) {
            showAlert(Alert.AlertType.ERROR, "Select a customer to delete");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM customers WHERE customer_id=?"
            );
            pst.setInt(1, selected.getCustomerId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Customer deleted successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        customerTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}