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
import javafx.scene.layout.VBox;

public class LoginScreenController {

    // Customer form fields
    @FXML private VBox customerForm;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    // Admin form fields
    @FXML private VBox adminForm;
    @FXML private TextField adminEmailField;
    @FXML private PasswordField adminPasswordField;

    // Tab buttons
    @FXML private Button customerTabBtn;
    @FXML private Button adminTabBtn;

    private boolean isAdminMode = false;

    @FXML
    public void showCustomerLogin(ActionEvent event) {
        isAdminMode = false;
        customerForm.setVisible(true);
        customerForm.setManaged(true);
        adminForm.setVisible(false);
        adminForm.setManaged(false);
        customerTabBtn.getStyleClass().setAll("button", "btn-accent");
        adminTabBtn.getStyleClass().setAll("button", "btn-outline");
    }

    @FXML
    public void showAdminLogin(ActionEvent event) {
        isAdminMode = true;
        customerForm.setVisible(false);
        customerForm.setManaged(false);
        adminForm.setVisible(true);
        adminForm.setManaged(true);
        adminTabBtn.getStyleClass().setAll("button", "btn-accent");
        customerTabBtn.getStyleClass().setAll("button", "btn-outline");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        if (isAdminMode) {
            handleAdminLogin();
        } else {
            handleCustomerLogin();
        }
    }

    private void handleCustomerLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, loginButton.getScene().getWindow(),
                "Missing Fields", "Please enter your email and password.");
            return;
        }

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

    private void handleAdminLogin() {
        String email = adminEmailField.getText().trim();
        String password = adminPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, adminEmailField.getScene().getWindow(),
                "Missing Fields", "Please enter your email and password.");
            return;
        }

        Employee emp = Database.getEmployeeByEmailAndPassword(email, password);
        if (emp != null) {
            SessionManager.setCurrentEmployee(emp);
            try {
                SceneManager.switchScene(adminEmailField.getScene().getWindow(),
                    "/employee-dashboard.fxml", "Miyamoto - Employee Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, adminEmailField.getScene().getWindow(),
                "Login Failed", "Invalid employee credentials. Check your email, Employee ID, and password.");
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
