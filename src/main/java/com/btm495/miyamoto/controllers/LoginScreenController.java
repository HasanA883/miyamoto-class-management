package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Customer;
import com.btm495.miyamoto.objects.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginScreenController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField employeeIdField;
    @FXML private Button loginButton;

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String employeeId = employeeIdField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, loginButton.getScene().getWindow(),
                "Missing Fields", "Please enter your email and password.");
            return;
        }

        if (!employeeId.isEmpty()) {
            Employee emp = Database.getEmployeeByEmailAndIdAndPassword(email, employeeId, password);
            if (emp != null) {
                SessionManager.setCurrentEmployee(emp);
                try {
                    SceneManager.switchScene(loginButton.getScene().getWindow(),
                        "/employee-dashboard.fxml", "Miyamoto - Employee Dashboard");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(),
                    "Login Failed", "Invalid employee credentials. Check your email, Employee ID, and password.");
            }
        } else {
            Customer customer = Database.getCustomerByEmailAndPassword(email, password);
            if (customer != null) {
                SessionManager.setCurrentCustomer(customer);
                try {
                    SceneManager.switchScene(loginButton.getScene().getWindow(),
                        "/customer-dashboard.fxml", "Miyamoto - Customer Dashboard");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(),
                    "Login Failed", "Invalid email or password.");
            }
        }
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        try {
            SceneManager.switchScene(loginButton.getScene().getWindow(),
                "/registration-form.fxml", "Miyamoto - Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
