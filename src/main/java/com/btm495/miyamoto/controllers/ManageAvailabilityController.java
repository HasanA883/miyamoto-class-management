package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.objects.Availability;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ManageAvailabilityController {

    @FXML private TableView<Availability> availabilityTable;
    @FXML private TableColumn<Availability, String> typeTableColumn;
    @FXML private TableColumn<Availability, String> startTimeTableColumn;
    @FXML private TableColumn<Availability, String> endTimeTableColumn;
    @FXML private TableColumn<Availability, String> priceTableColumn;
    @FXML private TableColumn<Availability, String> spotsTableColumn;
    @FXML private TableColumn<Availability, String> activeTableColumn;

    @FXML private Label formTitleLabel;
    @FXML private TextField classTypeField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField durationField;
    @FXML private TextField basePriceField;
    @FXML private TextField maxSpotsField;
    @FXML private TextField defaultChefField;
    @FXML private TextField materialsField;
    @FXML private Button saveButton;
    @FXML private Button toggleStatusButton;
    @FXML private Button filterToggleButton;

    private static final DateTimeFormatter START_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private int editingId = -1;
    private boolean showActiveOnly = false;

    @FXML
    public void initialize() {
        // Auto-complete bookings for any classes that have already ended
        Database.sendEndOfClassNotifications();

        typeTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBookingType()));
        startTimeTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getAvailableStartTime() != null ? cd.getValue().getAvailableStartTime().format(START_FMT) : ""));
        endTimeTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getAvailableEndTime() != null ? cd.getValue().getAvailableEndTime().format(TIME_FMT) : ""));
        priceTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            String.format("$%.2f", cd.getValue().getBasePrice() != null ? cd.getValue().getBasePrice() : 0)));
        spotsTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            String.valueOf(cd.getValue().getNumberOfBookings())));
        activeTableColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().isActive() ? "Active" : "Deactivated"));

        // Update toggleStatusButton label/colour whenever a row is selected
        availabilityTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                if (selected.isActive()) {
                    toggleStatusButton.setText("Deactivate");
                    toggleStatusButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-cursor: hand;");
                } else {
                    toggleStatusButton.setText("Activate");
                    toggleStatusButton.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-cursor: hand;");
                }
            }
        });

        loadTable();
    }

    private void loadTable() {
        List<Availability> list = showActiveOnly
            ? Database.getAllActiveUpcomingAvailability()
            : Database.getAllAvailability();
        availabilityTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    public void toggleFilter(ActionEvent event) {
        showActiveOnly = !showActiveOnly;
        filterToggleButton.setText(showActiveOnly ? "Show All Classes" : "Active/Upcoming Only");
        loadTable();
    }

    @FXML
    public void saveAvailability(ActionEvent event) {
        String type = classTypeField.getText().trim();
        if (type.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "Missing Field", "Class Type is required.");
            return;
        }

        String duration = durationField.getText().trim();
        if (duration.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "Missing Field", "Duration is required (e.g. \"2 hours\").");
            return;
        }

        if (startDatePicker.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "Missing Field", "Date is required.");
            return;
        }

        LocalDate date = startDatePicker.getValue();
        LocalDateTime start = buildDateTime(date, startTimeField.getText().trim());
        LocalDateTime end = buildDateTime(date, endTimeField.getText().trim());

        if (start == null || end == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "Invalid Time", "Please enter valid Start Time and End Time (HH:MM).");
            return;
        }

        if (!end.isAfter(start)) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "Invalid Time", "End Time must be after Start Time.");
            return;
        }

        // Enforce one active class at a time — check for overlap with other active classes
        int excludeForOverlap = editingId > 0 ? editingId : 0;
        if (Database.hasActiveOverlap(start, end, excludeForOverlap)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, saveButton.getScene().getWindow(),
                "Time Conflict",
                "This class overlaps with another active class. Only one active class at a time is allowed. Please choose a different date/time or deactivate the conflicting class first.");
            return;
        }

        double price = 0;
        try { price = Double.parseDouble(basePriceField.getText().trim()); } catch (NumberFormatException ignored) {}

        int spots = 0;
        try { spots = Integer.parseInt(maxSpotsField.getText().trim()); } catch (NumberFormatException ignored) {}

        Availability a = new Availability(start, end, spots, type,
            descriptionField.getText().trim(), price,
            defaultChefField.getText().trim(), null,
            materialsField.getText().trim());
        a.setDurationText(duration);

        boolean success;
        if (editingId > 0) {
            a.setAvailabilityId(editingId);
            success = Database.updateAvailabilityRecord(a);
        } else {
            success = Database.insertAvailability(a);
        }

        if (success) {
            if (editingId > 0) {
                // Notify any customers who have bookings for this class that details changed
                Database.notifyBookedCustomersOfClassUpdate(editingId, type);
            }
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(),
                "Saved", editingId > 0 ? "Class updated successfully." : "Class added successfully.");
            clearForm(null);
            loadTable();
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, saveButton.getScene().getWindow(),
                "Error", "Failed to save class. Please try again.");
        }
    }

    @FXML
    public void loadSelected(ActionEvent event) {
        Availability selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "No Selection", "Please select a class to edit.");
            return;
        }
        editingId = selected.getAvailabilityId();
        formTitleLabel.setText("Edit Class  (ID: " + editingId + ")");
        classTypeField.setText(nvl(selected.getBookingType()));
        descriptionField.setText(nvl(selected.getDescription()));
        defaultChefField.setText(nvl(selected.getDefaultChef()));
        materialsField.setText(nvl(selected.getMaterials()));
        durationField.setText(nvl(selected.getDurationText()));
        basePriceField.setText(selected.getBasePrice() != null ? String.valueOf(selected.getBasePrice()) : "");
        maxSpotsField.setText(String.valueOf(selected.getNumberOfBookings()));

        if (selected.getAvailableStartTime() != null) {
            startDatePicker.setValue(selected.getAvailableStartTime().toLocalDate());
            startTimeField.setText(selected.getAvailableStartTime().toLocalTime().toString().substring(0, 5));
        }
        if (selected.getAvailableEndTime() != null) {
            endTimeField.setText(selected.getAvailableEndTime().toLocalTime().toString().substring(0, 5));
        }
        saveButton.setText("Update Class");
    }

    @FXML
    public void clearForm(ActionEvent event) {
        editingId = -1;
        formTitleLabel.setText("Add New Class");
        saveButton.setText("Save Class");
        classTypeField.clear();
        descriptionField.clear();
        startDatePicker.setValue(null);
        startTimeField.clear();
        endTimeField.clear();
        durationField.clear();
        basePriceField.clear();
        maxSpotsField.clear();
        defaultChefField.clear();
        materialsField.clear();
    }

    @FXML
    public void toggleStatus(ActionEvent event) {
        Availability selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, saveButton.getScene().getWindow(),
                "No Selection", "Please select a class first.");
            return;
        }

        if (selected.isActive()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Deactivate \"" + selected.getBookingType() + "\"? It will no longer be visible to customers.",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Deactivation");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if (Database.deactivateAvailability(selected.getAvailabilityId())) {
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(),
                        "Deactivated", "Class has been deactivated and hidden from customers.");
                    loadTable();
                }
            }
        } else {
            // Check for overlap before reactivating
            if (Database.hasActiveOverlap(selected.getAvailableStartTime(), selected.getAvailableEndTime(), selected.getAvailabilityId())) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, saveButton.getScene().getWindow(),
                    "Time Conflict",
                    "Cannot activate: this class overlaps with another active class.");
                return;
            }
            if (Database.reactivateAvailability(selected.getAvailabilityId())) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(),
                    "Activated", "Class is now active and visible to customers.");
                loadTable();
            }
        }
    }

    @FXML
    public void refreshTable(ActionEvent event) {
        Database.sendEndOfClassNotifications();
        loadTable();
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            SceneManager.switchScene(saveButton.getScene().getWindow(),
                "/employee-dashboard.fxml", "Miyamoto - Employee Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime buildDateTime(LocalDate date, String time) {
        if (date == null) return null;
        try {
            String t = (time == null || time.isBlank()) ? "00:00" : time;
            return LocalDateTime.of(date, LocalTime.parse(t));
        } catch (Exception e) {
            return null;
        }
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
