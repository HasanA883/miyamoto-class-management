package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Notification;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label unreadBadge;
    @FXML private ListView<Notification> notificationsListView;
    @FXML private Label avatarLabel;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

    @FXML
    public void initialize() {
        if (SessionManager.getCurrentCustomer() != null) {
            String name = SessionManager.getCurrentCustomer().getName();
            String email = SessionManager.getCurrentCustomer().getEmailAddress();
            welcomeLabel.setText("Welcome back, " + name + "!");
            nameLabel.setText(name);
            emailLabel.setText(email);
            // Set avatar initials (up to 2 characters)
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)
                : name.substring(0, Math.min(2, name.length()));
            avatarLabel.setText(initials.toUpperCase());
        }

        notificationsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("");
                    return;
                }
                boolean unread = "Sent".equals(item.getNotificationStatus());

                Label subject = new Label(item.getSubject() != null ? item.getSubject() : "(No subject)");
                subject.setStyle("-fx-font-size: 12px; -fx-font-weight: " + (unread ? "bold" : "normal") + ";");

                String dateStr = item.getSentDate() != null ? item.getSentDate().format(FMT) : "";
                Label meta = new Label(item.getType() + "  \u2022  " + dateStr
                    + (unread ? "  \u2022  Unread" : "  \u2022  Read"));
                meta.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px;");

                VBox box = new VBox(3, subject, meta);
                box.setStyle("-fx-padding: 5 6 5 6;");
                setGraphic(box);

                // Highlight unread rows with the design-system accent tint
                setStyle(unread
                    ? "-fx-background-color: #EFF3F4; -fx-border-left-color: #7A8C8E; -fx-border-width: 0 0 0 3; -fx-padding: 0 0 0 2;"
                    : "-fx-background-color: white;");
            }
        });

        loadNotifications();
    }

    private void loadNotifications() {
        if (SessionManager.getCurrentCustomer() == null) return;
        // Auto-complete any ended bookings and generate review requests before fetching
        Database.sendEndOfClassNotifications();
        String email = SessionManager.getCurrentCustomer().getEmailAddress();
        List<Notification> notifications = Database.getNotificationsByCustomerEmail(email);
        notificationsListView.setItems(FXCollections.observableArrayList(notifications));

        int unread = Database.getUnreadNotificationCount(email);
        unreadBadge.setText(unread > 0 ? unread + " unread" : "");
        unreadBadge.setVisible(unread > 0);
        unreadBadge.setManaged(unread > 0);
    }

    @FXML
    public void refreshNotifications(ActionEvent event) {
        loadNotifications();
    }

    @FXML
    public void viewSelectedNotification(ActionEvent event) {
        Notification selected = notificationsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, welcomeLabel.getScene().getWindow(),
                "No Selection", "Please select a notification to view.");
            return;
        }
        // Mark as read and refresh before showing dialog
        if ("Sent".equals(selected.getNotificationStatus())) {
            Database.markNotificationRead(selected.getNotificationId());
        }
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(welcomeLabel.getScene().getWindow());
        dialog.setTitle(selected.getSubject());
        dialog.setHeaderText(selected.getSubject());
        dialog.setContentText(selected.getMessageContent() != null ? selected.getMessageContent() : "(No message content)");
        dialog.showAndWait();
        loadNotifications();
    }

    @FXML
    public void markSelectedRead(ActionEvent event) {
        Notification selected = notificationsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, welcomeLabel.getScene().getWindow(),
                "No Selection", "Please select a notification to mark as read.");
            return;
        }
        if ("Read".equals(selected.getNotificationStatus())) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, welcomeLabel.getScene().getWindow(),
                "Already Read", "This notification is already marked as read.");
            return;
        }
        Database.markNotificationRead(selected.getNotificationId());
        loadNotifications();
    }

    @FXML
    public void markAllRead(ActionEvent event) {
        if (SessionManager.getCurrentCustomer() == null) return;
        Database.markAllNotificationsRead(SessionManager.getCurrentCustomer().getEmailAddress());
        loadNotifications();
    }

    @FXML
    public void browseClasses(ActionEvent event) {
        try {
            SceneManager.switchScene(welcomeLabel.getScene().getWindow(),
                "/browse-classes.fxml", "Miyamoto - Browse Classes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void myBookings(ActionEvent event) {
        SessionManager.setViewAllBookings(false);
        try {
            SceneManager.switchScene(welcomeLabel.getScene().getWindow(),
                "/my-bookings.fxml", "Miyamoto - My Bookings");
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
