package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Supplier;
import db.DBConnection;

import java.sql.*;

public class SupplierController {

    @FXML
    private TextField txtName, txtPhone, txtAddress;

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colPhone;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    private ObservableList<Supplier> supplierList;

    @FXML
    public void initialize() {
        loadSuppliers();

        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if(newSel != null) {
                txtName.setText(newSel.getName());
                txtPhone.setText(newSel.getPhone());
                txtAddress.setText(newSel.getAddress());
            }
        });
    }

    public void loadSuppliers() {
        supplierList = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM suppliers");

            while(rs.next()) {
                supplierList.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }

        } catch (SQLException e) { e.printStackTrace(); }

        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        supplierTable.setItems(supplierList);
    }

    @FXML
    public void addSupplier() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if(name.isEmpty()) { showAlert(Alert.AlertType.ERROR,"Name is required"); return; }
        if(phone.isEmpty()) { showAlert(Alert.AlertType.ERROR,"Phone is required"); return; }
        if(!phone.matches("\\d+")) { showAlert(Alert.AlertType.ERROR,"Phone must be digits only"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO suppliers(name, phone, address) VALUES(?, ?, ?)"
            );
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setString(3, address.isEmpty()?null:address);
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Supplier added successfully!");
            clearFields();
            loadSuppliers();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    public void updateSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if(selected==null){ showAlert(Alert.AlertType.ERROR,"Select a supplier to update"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE suppliers SET name=?, phone=?, address=? WHERE supplier_id=?"
            );
            pst.setString(1, txtName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, txtAddress.getText());
            pst.setInt(4, selected.getSupplierId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Supplier updated successfully!");
            clearFields();
            loadSuppliers();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    public void deleteSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if(selected==null){ showAlert(Alert.AlertType.ERROR,"Select a supplier to delete"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM suppliers WHERE supplier_id=?"
            );
            pst.setInt(1, selected.getSupplierId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Supplier deleted successfully!");
            clearFields();
            loadSuppliers();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtAddress.clear();
        supplierTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type,message,ButtonType.OK);
        alert.showAndWait();
    }
}