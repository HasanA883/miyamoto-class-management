package com.btm495.miyamoto.objects;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;

public class Payment {
    private String transactionNumber;
    private Double amount;
    private String paymentMethod;
    private int CVVNumber;
    private Date cardExpirationDate;
    private String paymentStatus;

    public Payment(String transactionNumber, Double amount, String paymentMethod, int CVVNumber, Date cardExpirationDate, String paymentStatus) {
        this.transactionNumber = transactionNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.CVVNumber = CVVNumber;
        this.cardExpirationDate = cardExpirationDate;
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getCVVNumber() {
        return CVVNumber;
    }

    public void setCVVNumber(int CVVNumber) {
        this.CVVNumber = CVVNumber;
    }

    public Date getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(Date cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean processPayment() {
        return false;
    }

    public boolean displayCheckout() {
        return false;
    }

    public boolean displayPaymentOptions() {
        return false;
    }

    public boolean selectPaymentType() {
        return false;
    }

    public boolean validatePayment() {
        return false;
    }

    public boolean issueRefund() {
        return false;
    }

    public File generateInvoice() {
        return null;
    }

    public LocalDateTime releaseTimeslot() {
        return null;
    }

    public boolean promptInfo() {
        return false;
    }

    public boolean enterInfo() {
        return false;
    }

    public boolean logPaymentStatus() {
        return false;
    }
}
