package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Availability;
import com.btm495.miyamoto.objects.Booking;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingFormController {

    @FXML private Label classTypeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label materialsLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label chefLabel;
    @FXML private Label priceLabel;
    @FXML private Spinner<Integer> participantsSpinner;
    @FXML private Label totalCostLabel;
    @FXML private Button confirmButton;

    private Availability selected;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

    @FXML
    public void initialize() {
        selected = SessionManager.getSelectedAvailability();
        if (selected == null) return;

        classTypeLabel.setText(selected.getBookingType() != null ? selected.getBookingType() : "N/A");
        descriptionLabel.setText(selected.getDescription() != null ? selected.getDescription() : "N/A");
        materialsLabel.setText(selected.getMaterials() != null ? selected.getMaterials() : "Provided by studio");
        dateTimeLabel.setText(selected.getAvailableStartTime() != null ? selected.getAvailableStartTime().format(FMT) : "TBD");
        endTimeLabel.setText(selected.getAvailableEndTime() != null ? selected.getAvailableEndTime().format(FMT) : "TBD");
        chefLabel.setText(selected.getDefaultChef() != null ? selected.getDefaultChef() : "TBD");
        priceLabel.setText(String.format("$%.2f", selected.getBasePrice() != null ? selected.getBasePrice() : 0));

        updateTotal();
        participantsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());
    }

    private void updateTotal() {
        if (selected == null || participantsSpinner == null) return;
        double total = (selected.getBasePrice() != null ? selected.getBasePrice() : 0) * participantsSpinner.getValue();
        totalCostLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    public void confirmBooking(ActionEvent event) {
        if (selected == null || SessionManager.getCurrentCustomer() == null) return;

        int participants = participantsSpinner.getValue();
        double basePrice = selected.getBasePrice() != null ? selected.getBasePrice() : 0;
        double total = basePrice * participants;

        String duration = "N/A";
        if (selected.getAvailableStartTime() != null && selected.getAvailableEndTime() != null) {
            long mins = java.time.Duration.between(
                selected.getAvailableStartTime(), selected.getAvailableEndTime()).toMinutes();
            duration = mins + " minutes";
        }

        Booking booking = new Booking(
            selected.getBookingType(),
            duration,
            LocalDateTime.now(),
            selected.getAvailableStartTime(),
            selected.getAvailableEndTime(),
            "Miyamoto Sushi Academy",
            basePrice,
            "Pending",
            participants,
            total,
            0,
            null
        );
        booking.setCustomerId(String.valueOf(SessionManager.getCurrentCustomer().getCustomerId()));
        booking.setAvailabilityId(selected.getAvailabilityId());

        int bookingId = Database.insertBooking(booking);
        if (bookingId > 0) {
            booking.setBookingId(bookingId);
            SessionManager.setPendingBooking(booking);
            try {
                SceneManager.switchScene(confirmButton.getScene().getWindow(),
                    "/payment.fxml", "Miyamoto - Payment");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, confirmButton.getScene().getWindow(),
                "Error", "Could not create booking. Please try again.");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            SceneManager.switchScene(confirmButton.getScene().getWindow(),
                "/browse-classes.fxml", "Miyamoto - Browse Classes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
