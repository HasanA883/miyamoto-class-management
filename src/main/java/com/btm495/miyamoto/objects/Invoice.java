package com.btm495.miyamoto.objects;

import java.io.File;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int paymentId;
    private LocalDateTime generationDate;
    private LocalDateTime dueDate;
    private double subTotal;
    private double taxAmount;
    private double totalAmount;

    public Invoice(LocalDateTime generationDate, LocalDateTime dueDate, double subTotal, double taxAmount, double totalAmount) {
        this.generationDate = generationDate;
        this.dueDate = dueDate;
        this.subTotal = subTotal;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(LocalDateTime generationDate) {
        this.generationDate = generationDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean generateInvoice() {
        return false;
    }

    public boolean sendInvoice() {
        return false;
    }

    public File printInvoice() {
        return null;
    }
}
