package com.btm495.miytamoto.objects;

public class Customer {

    private int customerId;
    private String name;
    private String address;
    private int phoneNumber;
    private String password;
    private String emailAddress;
    private int loyaltyProgram;

    public Customer(String name, String address, int phoneNumber, String password, String emailAddress, int loyaltyProgram) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.emailAddress = emailAddress;
        this.loyaltyProgram = loyaltyProgram;
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

    public int getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(int loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;

    }
}
