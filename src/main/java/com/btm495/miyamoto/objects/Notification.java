package com.btm495.miyamoto.objects;

import java.time.LocalDateTime;

public class Notification {

    private int notificationId;
    private int bookingId;
    private String type;
    private String recipientEmail;
    private String messageContent;
    private LocalDateTime sentDate;
    private String notificationStatus;
    private String subject;

    public Notification(String recipientEmail, String type, String messageContent, LocalDateTime sentDate, String notificationStatus, String subject) {
        this.recipientEmail = recipientEmail;
        this.type = type;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
        this.notificationStatus = notificationStatus;
        this.subject = subject;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean send() {
        return false;
    }

    public boolean queue() {
        return false;
    }

    public boolean resend() {
        return false;
    }

    public boolean detectTrigger() {
        return false;
    }

    public boolean retrieveInfo() {
        return false;
    }

    public boolean logStatus() {
        return false;
    }
}
