package com.btm495.miyamoto.controllers;

import com.btm495.miyamoto.AlertHelper;
import com.btm495.miyamoto.Database;
import com.btm495.miyamoto.SceneManager;
import com.btm495.miyamoto.SessionManager;
import com.btm495.miyamoto.objects.Availability;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BrowseClassesController {

    @FXML private TableView<Availability> classesTable;
    @FXML private TableColumn<Availability, String> typeColumn;
    @FXML private TableColumn<Availability, String> descriptionColumn;
    @FXML private TableColumn<Availability, String> startTimeColumn;
    @FXML private TableColumn<Availability, String> endTimeColumn;
    @FXML private TableColumn<Availability, String> priceColumn;
    @FXML private TableColumn<Availability, String> chefColumn;
    @FXML private TableColumn<Availability, String> spotsColumn;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBookingType()));
        descriptionColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDescription()));
        startTimeColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getAvailableStartTime() != null ? cd.getValue().getAvailableStartTime().format(FMT) : "TBD"));
        endTimeColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getAvailableEndTime() != null ? cd.getValue().getAvailableEndTime().format(FMT) : "TBD"));
        priceColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            String.format("$%.2f", cd.getValue().getBasePrice() != null ? cd.getValue().getBasePrice() : 0)));
        chefColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getDefaultChef() != null ? cd.getValue().getDefaultChef() : "TBD"));
        spotsColumn.setCellValueFactory(cd -> new SimpleStringProperty(
            String.valueOf(cd.getValue().getNumberOfBookings())));

        loadClasses();
    }

    private void loadClasses() {
        List<Availability> classes = Database.getAllActiveAvailability();
        classesTable.setItems(FXCollections.observableArrayList(classes));
    }

    @FXML
    public void bookClass(ActionEvent event) {
        Availability selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, classesTable.getScene().getWindow(),
                "No Selection", "Please select a class to book.");
            return;
        }
        SessionManager.setSelectedAvailability(selected);
        try {
            SceneManager.switchScene(classesTable.getScene().getWindow(),
                "/booking-form.fxml", "Miyamoto - Book a Class");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            SceneManager.switchScene(classesTable.getScene().getWindow(),
                "/customer-dashboard.fxml", "Miyamoto - Customer Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
