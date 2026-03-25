package com.btm495.miyamoto.objects;

public class Customer {

    private int customerId;
    private String name;
    private String address;
    private int phoneNumber;
    private String password;
    private String emailAddress;
    private int loyaltyPoints;

    public Customer(String name, String address, int phoneNumber, String password, String emailAddress, int loyaltyPoints) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.emailAddress = emailAddress;
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public boolean registerAccount() {
        return false;
    }

    public boolean loginAccount() {
        return false;
    }

    public boolean createAccount() {
        return false;
    }

    public boolean activateAccount() {
        return false;
    }

    public boolean updateProfile() {
        return false;
    }

    public boolean viewBooking() {
        return false;
    }

    public boolean addReview() {
        return false;
    }

    public boolean makePayment() {
        return false;
    }

    public boolean chooseTimeslot() {
        return false;
    }

    public boolean addLoyaltyPoints() {
        return false;
    }

    public boolean enterInfo() {
        return false;
    }

    public boolean confirmTermsAndConditions() {
        return false;
    }

    public boolean confirmVerification() {
        return false;
    }

    public boolean returnInfo() {
        return false;
    }
}
