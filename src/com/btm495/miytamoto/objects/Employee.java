package com.btm495.miytamoto.objects;

import java.time.LocalDateTime;

public class Employee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private int phoneNumber;
    private LocalDateTime schedule;
    private String employeeRole;
    private LocalDateTime hireDate;
    private Boolean iaActive;

    public Employee(String firstName, String lastName, String emailAddress, int phoneNumber, LocalDateTime schedule, String employeeRole, LocalDateTime hireDate, Boolean iaActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.schedule = schedule;
        this.employeeRole = employeeRole;
        this.hireDate = hireDate;
        this.iaActive = iaActive;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getSchedule() {
        return schedule;
    }

    public void setSchedule(LocalDateTime schedule) {
        this.schedule = schedule;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public LocalDateTime getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public Boolean getIaActive() {
        return iaActive;
    }

    public void setIaActive(Boolean iaActive) {
        this.iaActive = iaActive;
    }
}
