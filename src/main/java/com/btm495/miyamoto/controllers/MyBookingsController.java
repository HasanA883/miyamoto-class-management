package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Availability;
import com.btm495.miyamoto.objects.Booking;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyBookingsController {

    @FXML private Label titleLabel;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> idColumn;
    @FXML private TableColumn<Booking, String> typeColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> durationColumn;
    @FXML private TableColumn<Booking, String> participantsColumn;
    @FXML private TableColumn<Booking, String> totalColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, String> ratingColumn;
    @FXML private Button addReviewButton;
    @FXML private Button markCompletedButton;
    @FXML private Button filterToggleButton;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");
    private boolean showUpcomingOnly = false;
    private List<Availability> upcomingSlots;

    @FXML
    public void initialize() {
        boolean isEmployee = SessionManager.isViewAllBookings();
        titleLabel.setText(isEmployee ? "All Bookings" : "My Bookings");

        idColumn.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getBookingId())));
        typeColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBookingType()));
        dateColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getBookingStart() != null ? cd.getValue().getBookingStart().format(FMT) : "TBD"));
        durationColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBookingDuration()));
        participantsColumn.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getTotalEnrollment())));
        totalColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            String.format("$%.2f", cd.getValue().getTotalPrice() != null ? cd.getValue().getTotalPrice() : 0)));
        statusColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBookingStatus()));
        ratingColumn.setCellValueFactory(cd -> {
            int r = cd.getValue().getBookingRating();
            return new SimpleStringProperty(r > 0 ? r + "/5" : "-");
        });

        if (isEmployee) {
            // Hide customer-only buttons
            addReviewButton.setVisible(false);
            addReviewButton.setManaged(false);
            markCompletedButton.setVisible(false);
            markCompletedButton.setManaged(false);

            // Enable inline editing for date (availability slot) and participants
            bookingsTable.setEditable(true);

            upcomingSlots = Database.getAllActiveUpcomingAvailability();
            ObservableList<String> slotLabels = FXCollections.observableArrayList(
                upcomingSlots.stream()
                    .map(a -> a.getAvailableStartTime().format(FMT) + "  [" + a.getBookingType() + "]")
                    .collect(Collectors.toList())
            );

            dateColumn.setCellFactory(ComboBoxTableCell.forTableColumn(slotLabels));
            dateColumn.setOnEditCommit(e -> {
                Booking b = e.getRowValue();
                String label = e.getNewValue();
                Availability matched = upcomingSlots.stream()
                    .filter(a -> (a.getAvailableStartTime().format(FMT) + "  [" + a.getBookingType() + "]").equals(label))
                    .findFirst().orElse(null);
                if (matched != null) {
                    Database.updateBookingFromAvailability(
                        b.getBookingId(), matched.getAvailabilityId(), b.getTotalEnrollment());
                    loadBookings();
                }
            });

            participantsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            participantsColumn.setOnEditCommit(e -> {
                Booking b = e.getRowValue();
                try {
                    int p = Integer.parseInt(e.getNewValue().trim());
                    if (p > 0) {
                        Database.updateBookingParticipants(b.getBookingId(), p);
                        loadBookings();
                    }
                } catch (NumberFormatException ignored) {
                    loadBookings(); // refresh to revert invalid edit
                }
            });
        }

        loadBookings();
    }

    private void loadBookings() {
        boolean isEmployee = SessionManager.isViewAllBookings();
        List<Booking> bookings;
        if (showUpcomingOnly) {
            if (isEmployee) {
                bookings = Database.getAllConfirmedUpcomingBookings();
            } else {
                bookings = Database.getConfirmedUpcomingBookingsByCustomer(
                    SessionManager.getCurrentCustomer().getCustomerId());
            }
        } else {
            if (isEmployee) {
                bookings = Database.getAllBookings();
            } else {
                bookings = Database.getBookingsByCustomerId(
                    SessionManager.getCurrentCustomer().getCustomerId());
            }
        }
        bookingsTable.setItems(FXCollections.observableArrayList(bookings));
    }

    @FXML
    public void toggleFilter(ActionEvent event) {
        showUpcomingOnly = !showUpcomingOnly;
        filterToggleButton.setText(showUpcomingOnly ? "Show All Bookings" : "Show Upcoming Only");
        loadBookings();
    }

    @FXML
    public void cancelBooking(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "No Selection", "Please select a booking to cancel.");
            return;
        }
        String status = selected.getBookingStatus();
        if ("Cancelled".equals(status) || "Completed".equals(status)) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "Cannot Cancel", "This booking cannot be cancelled (status: " + status + ").");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Cancel booking #" + selected.getBookingId() + " - " + selected.getBookingType() + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Cancellation");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (Database.updateBookingStatus(selected.getBookingId(), "Cancelled")) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, bookingsTable.getScene().getWindow(),
                    "Cancelled", "Booking has been cancelled successfully.");
                loadBookings();
            }
        }
    }

    @FXML
    public void addReview(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "No Selection", "Please select a completed booking to review.");
            return;
        }
        if (!"Completed".equals(selected.getBookingStatus())) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "Not Completed", "You can only review bookings that have been completed.");
            return;
        }
        if (selected.getBookingRating() > 0) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, bookingsTable.getScene().getWindow(),
                "Already Reviewed", "You have already submitted a review for this booking.");
            return;
        }

        TextInputDialog ratingDialog = new TextInputDialog("5");
        ratingDialog.setTitle("Rate Your Experience");
        ratingDialog.setHeaderText("Rate your class (1 = Poor, 5 = Excellent):");
        ratingDialog.setContentText("Rating (1-5):");
        Optional<String> ratingResult = ratingDialog.showAndWait();
        if (ratingResult.isEmpty()) return;

        int rating;
        try {
            rating = Integer.parseInt(ratingResult.get().trim());
            if (rating < 1 || rating > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "Invalid Rating", "Please enter a number between 1 and 5.");
            return;
        }

        TextInputDialog reviewDialog = new TextInputDialog();
        reviewDialog.setTitle("Write a Review");
        reviewDialog.setHeaderText("Share your experience with \"" + selected.getBookingType() + "\":");
        reviewDialog.setContentText("Review:");
        Optional<String> reviewResult = reviewDialog.showAndWait();
        if (reviewResult.isEmpty()) return;

        if (Database.addBookingReview(selected.getBookingId(), rating, reviewResult.get())) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, bookingsTable.getScene().getWindow(),
                "Thank You!", "Your review has been submitted. We appreciate your feedback!");
            loadBookings();
        }
    }

    @FXML
    public void markCompleted(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "No Selection", "Please select a booking to mark as completed.");
            return;
        }
        if (!"Confirmed".equals(selected.getBookingStatus())) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, bookingsTable.getScene().getWindow(),
                "Cannot Complete", "Only confirmed bookings can be marked as completed.");
            return;
        }
        if (Database.updateBookingStatus(selected.getBookingId(), "Completed")) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, bookingsTable.getScene().getWindow(),
                "Completed", "Booking marked as completed.");
            loadBookings();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            if (SessionManager.isViewAllBookings()) {
                SceneManager.switchScene(bookingsTable.getScene().getWindow(),
                    "/employee-dashboard.fxml", "Miyamoto - Employee Dashboard");
            } else {
                SceneManager.switchScene(bookingsTable.getScene().getWindow(),
                    "/customer-dashboard.fxml", "Miyamoto - Customer Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
