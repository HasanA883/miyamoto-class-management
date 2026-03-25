package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationFormController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button createAccountButton;

    @FXML
    public void createAccount(ActionEvent event) {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, createAccountButton.getScene().getWindow(),
                "Missing Fields", "Please fill in all required fields.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, createAccountButton.getScene().getWindow(),
                "Invalid Email", "Please enter a valid email address.");
            return;
        }
        if (!password.equals(confirm)) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, createAccountButton.getScene().getWindow(),
                "Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }
        if (password.length() < 6) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, createAccountButton.getScene().getWindow(),
                "Weak Password", "Password must be at least 6 characters.");
            return;
        }
        if (Database.emailExists(email)) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, createAccountButton.getScene().getWindow(),
                "Email Already Registered", "An account with this email already exists. Please log in.");
            return;
        }

        if (Database.insertCustomer(name, address, phone, password, email)) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, createAccountButton.getScene().getWindow(),
                "Account Created", "Your account has been created successfully! Please log in.");
            try {
                SceneManager.switchScene(createAccountButton.getScene().getWindow(),
                    "/login-screen.fxml", "Miyamoto - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, createAccountButton.getScene().getWindow(),
                "Error", "Failed to create account. Please try again.");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            SceneManager.switchScene(createAccountButton.getScene().getWindow(),
                "/login-screen.fxml", "Miyamoto - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
