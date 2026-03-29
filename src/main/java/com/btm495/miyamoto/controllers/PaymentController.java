package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Booking;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PaymentController {

    @FXML private Label bookingSummaryLabel;
    @FXML private Label totalAmountLabel;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField cardholderField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private VBox cardDetailsPane;
    @FXML private Button payButton;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");
    private Booking pending;

    @FXML
    public void initialize() {
        pending = SessionManager.getPendingBooking();

        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Debit Card"));
        paymentMethodCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            boolean isCard = "Credit Card".equals(val) || "Debit Card".equals(val);
            cardDetailsPane.setVisible(isCard);
            cardDetailsPane.setManaged(isCard);
        });

        if (pending != null) {
            String startStr = pending.getBookingStart() != null ? pending.getBookingStart().format(FMT) : "TBD";
            bookingSummaryLabel.setText(
                "Class: " + pending.getBookingType() + "\n" +
                "Date & Time: " + startStr + "\n" +
                "Participants: " + pending.getTotalEnrollment() + "\n" +
                "Duration: " + pending.getBookingDuration()
            );
            double subtotal = pending.getTotalPrice() != null ? pending.getTotalPrice() : 0;
            double tax = subtotal * 0.15;
            totalAmountLabel.setText(String.format("Subtotal: $%.2f  |  Tax (15%%): $%.2f  |  Total: $%.2f",
                subtotal, tax, subtotal + tax));
        }
    }

    @FXML
    public void processPayment(ActionEvent event) {
        String method = paymentMethodCombo.getValue();
        if (method == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, payButton.getScene().getWindow(),
                "Missing Info", "Please select a payment method.");
            return;
        }

        boolean isCard = "Credit Card".equals(method) || "Debit Card".equals(method);
        if (isCard) {
            if (cardholderField.getText().isBlank() || cardNumberField.getText().isBlank()
                    || expiryField.getText().isBlank() || cvvField.getText().isBlank()) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, payButton.getScene().getWindow(),
                    "Missing Card Details", "Please fill in all card details.");
                return;
            }
            String digits = cardNumberField.getText().replaceAll("\\s", "");
            if (digits.length() < 16) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, payButton.getScene().getWindow(),
                    "Invalid Card Number", "Please enter a valid 16-digit card number.");
                return;
            }
        }

        double subtotal = pending != null && pending.getTotalPrice() != null ? pending.getTotalPrice() : 0;
        double tax = subtotal * 0.15;
        double grandTotal = subtotal + tax;

        Database.updateBookingStatus(pending.getBookingId(), "Confirmed");

        String txn = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int paymentId = Database.insertPayment(txn, grandTotal, method, pending.getBookingId());
        if (paymentId > 0) {
            Database.insertInvoice(paymentId, subtotal, tax, grandTotal);
        }

        if (SessionManager.getCurrentCustomer() != null) {
            Database.insertNotification(
                pending.getBookingId(),
                "Booking Confirmation",
                SessionManager.getCurrentCustomer().getEmailAddress(),
                "Booking Confirmed - " + pending.getBookingType(),
                "Your booking for \"" + pending.getBookingType() + "\" has been confirmed.\n" +
                "Transaction #: " + txn + "\nTotal Charged: $" + String.format("%.2f", grandTotal)
            );
        }

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, payButton.getScene().getWindow(),
            "Payment Successful",
            String.format("Payment confirmed!\n\nTransaction #: %s\nSubtotal: $%.2f\nTax (15%%): $%.2f\nTotal: $%.2f\n\n" +
                "A confirmation notification has been logged to your account.", txn, subtotal, tax, grandTotal));

        try {
            SceneManager.switchScene(payButton.getScene().getWindow(),
                "/customer-dashboard.fxml", "Miyamoto - Customer Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        if (pending != null) {
            Database.updateBookingStatus(pending.getBookingId(), "Cancelled");
        }
        try {
            SceneManager.switchScene(payButton.getScene().getWindow(),
                "/browse-classes.fxml", "Miyamoto - Browse Classes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
