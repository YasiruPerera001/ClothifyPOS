package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Employee;
import db.DBConnection;

import java.sql.*;

public class EmployeeController {

    @FXML
    private TextField txtName, txtPhone, txtPosition;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, Integer> colId;

    @FXML
    private TableColumn<Employee, String> colName;

    @FXML
    private TableColumn<Employee, String> colPhone;

    @FXML
    private TableColumn<Employee, String> colPosition;

    private ObservableList<Employee> employeeList;

    @FXML
    public void initialize() {
        loadEmployees();

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if(newSel != null) {
                txtName.setText(newSel.getName());
                txtPhone.setText(newSel.getPhone());
                txtPosition.setText(newSel.getPosition());
            }
        });
    }

    public void loadEmployees() {
        employeeList = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            while(rs.next()) {
                employeeList.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("position")
                ));
            }

        } catch (SQLException e) { e.printStackTrace(); }

        colId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));

        employeeTable.setItems(employeeList);
    }

    @FXML
    public void addEmployee() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String position = txtPosition.getText().trim();

        if(name.isEmpty()) { showAlert(Alert.AlertType.ERROR,"Name is required"); return; }
        if(phone.isEmpty()) { showAlert(Alert.AlertType.ERROR,"Phone is required"); return; }
        if(!phone.matches("\\d+")) { showAlert(Alert.AlertType.ERROR,"Phone must be digits only"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO employees(name, phone, position) VALUES(?, ?, ?)"
            );
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.setString(3, position.isEmpty()?null:position);
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Employee added successfully!");
            clearFields();
            loadEmployees();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    public void updateEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if(selected==null){ showAlert(Alert.AlertType.ERROR,"Select an employee to update"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE employees SET name=?, phone=?, position=? WHERE employee_id=?"
            );
            pst.setString(1, txtName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, txtPosition.getText());
            pst.setInt(4, selected.getEmployeeId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Employee updated successfully!");
            clearFields();
            loadEmployees();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    public void deleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if(selected==null){ showAlert(Alert.AlertType.ERROR,"Select an employee to delete"); return; }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM employees WHERE employee_id=?"
            );
            pst.setInt(1, selected.getEmployeeId());
            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION,"Employee deleted successfully!");
            clearFields();
            loadEmployees();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtPosition.clear();
        employeeTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type,message,ButtonType.OK);
        alert.showAndWait();
    }
}