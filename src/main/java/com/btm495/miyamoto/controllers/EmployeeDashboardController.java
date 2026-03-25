package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EmployeeDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        Employee emp = SessionManager.getCurrentEmployee();
        if (emp != null) {
            welcomeLabel.setText("Welcome, " + emp.getFirstName() + " " + emp.getLastName()
                + "  |  Role: " + emp.getEmployeeRole() + "  |  ID: " + emp.getEmployeeId());
        }
    }

    @FXML
    public void manageAvailability(ActionEvent event) {
        try {
            SceneManager.switchScene(welcomeLabel.getScene().getWindow(),
                "/manage-availability.fxml", "Miyamoto - Manage Availability");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void viewAllBookings(ActionEvent event) {
        SessionManager.setViewAllBookings(true);
        try {
            SceneManager.switchScene(welcomeLabel.getScene().getWindow(),
                "/my-bookings.fxml", "Miyamoto - All Bookings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(ActionEvent event) {
        SessionManager.clearSession();
        try {
            SceneManager.switchScene(welcomeLabel.getScene().getWindow(),
                "/login-screen.fxml", "Miyamoto - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
