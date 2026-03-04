package com.btm495.miytamoto.objects;

import java.util.Date;

public class Payment {
    private int transactionNumber;
    private Double amount;
    private String paymentMethod;
    private int CSVNumber;
    private Date cardExpirationDate;
    private String paymentStatus;

    public Payment(String paymentMethod, int transactionNumber, Double amount, int CSVNumber, Date cardExpirationDate, String paymentStatus) {
        this.paymentMethod = paymentMethod;
        this.transactionNumber = transactionNumber;
        this.amount = amount;
        this.CSVNumber = CSVNumber;
        this.cardExpirationDate = cardExpirationDate;
        this.paymentStatus = paymentStatus;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(int transactionNumber) {
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

    public int getCSVNumber() {
        return CSVNumber;
    }

    public void setCSVNumber(int CSVNumber) {
        this.CSVNumber = CSVNumber;
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
}
