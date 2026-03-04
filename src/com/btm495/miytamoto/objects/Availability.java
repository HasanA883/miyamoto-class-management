package com.btm495.miytamoto.objects;

import java.time.LocalDateTime;

public class Availability {

    private LocalDateTime availableStartTime;
    private LocalDateTime availableEndTime;
    private int numberOfBookings;
    private String bookingType;
    private String description;
    private Double basePrice;
    private String defaultChef;
    private LocalDateTime defaultDuration;
    private String materials;

    public Availability(LocalDateTime availableStartTime, LocalDateTime availableEndTime, int numberOfBookings, String bookingType, String description, Double basePrice, String defaultChef, LocalDateTime defaultDuration, String materials) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.numberOfBookings = numberOfBookings;
        this.bookingType = bookingType;
        this.description = description;
        this.basePrice = basePrice;
        this.defaultChef = defaultChef;
        this.defaultDuration = defaultDuration;
        this.materials = materials;
    }

    public LocalDateTime getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(LocalDateTime availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public LocalDateTime getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(LocalDateTime availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    public int getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(int numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getDefaultChef() {
        return defaultChef;
    }

    public void setDefaultChef(String defaultChef) {
        this.defaultChef = defaultChef;
    }

    public LocalDateTime getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(LocalDateTime defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }
}
