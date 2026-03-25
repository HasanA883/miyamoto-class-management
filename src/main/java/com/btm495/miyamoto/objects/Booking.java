package com.btm495.miyamoto.objects;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private String bookingType;
    private String bookingDuration;
    private LocalDateTime bookingDate;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private String bookingLocation;
    private Double bookingCost;
    private String bookingStatus;
    private String employeeId;
    private String customerId;
    private int totalEnrollment;
    private Double totalPrice;
    private int bookingRating;
    private String bookingReview;
    private int availabilityId;

    public Booking(String bookingType, String bookingDuration, LocalDateTime bookingDate, LocalDateTime bookingStart,
                   LocalDateTime bookingEnd, String bookingLocation, Double bookingCost, String bookingStatus,
                   int totalEnrollment, Double totalPrice, int bookingRating, String bookingReview) {
        this.bookingType = bookingType;
        this.bookingDuration = bookingDuration;
        this.bookingDate = bookingDate;
        this.bookingStart = bookingStart;
        this.bookingEnd = bookingEnd;
        this.bookingLocation = bookingLocation;
        this.bookingCost = bookingCost;
        this.bookingStatus = bookingStatus;
        this.totalEnrollment = totalEnrollment;
        this.totalPrice = totalPrice;
        this.bookingRating = bookingRating;
        this.bookingReview = bookingReview;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public String getBookingDuration() { return bookingDuration; }
    public void setBookingDuration(String bookingDuration) { this.bookingDuration = bookingDuration; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public LocalDateTime getBookingStart() { return bookingStart; }
    public void setBookingStart(LocalDateTime bookingStart) { this.bookingStart = bookingStart; }

    public LocalDateTime getBookingEnd() { return bookingEnd; }
    public void setBookingEnd(LocalDateTime bookingEnd) { this.bookingEnd = bookingEnd; }

    public String getBookingLocation() { return bookingLocation; }
    public void setBookingLocation(String bookingLocation) { this.bookingLocation = bookingLocation; }

    public Double getBookingCost() { return bookingCost; }
    public void setBookingCost(Double bookingCost) { this.bookingCost = bookingCost; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getTotalEnrollment() { return totalEnrollment; }
    public void setTotalEnrollment(int totalEnrollment) { this.totalEnrollment = totalEnrollment; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public int getBookingRating() { return bookingRating; }
    public void setBookingRating(int bookingRating) { this.bookingRating = bookingRating; }

    public String getBookingReview() { return bookingReview; }
    public void setBookingReview(String bookingReview) { this.bookingReview = bookingReview; }

    public int getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(int availabilityId) { this.availabilityId = availabilityId; }

    public boolean createBooking() { return false; }
    public boolean cancelBooking() { return false; }
    public boolean modifyBooking() { return false; }
    public boolean updateBooking() { return false; }
    public boolean submitBooking() { return false; }
    public boolean sendNotification() { return false; }
    public boolean updatePaymentStatus() { return false; }
    public boolean updateSchedule() { return false; }
    public boolean addReview() { return false; }
    public float calculateTotal() { return 0f; }
    public String promptInfo() { return null; }
    public boolean checkCustomersEnrolled() { return false; }
    public LocalDateTime reserveTimeslot() { return null; }
    public boolean displayReservationForm() { return false; }
    public Object displaySchedule() { return null; }
    public boolean validateDetails() { return false; }
    public boolean sendVerificationEmail() { return false; }
}
