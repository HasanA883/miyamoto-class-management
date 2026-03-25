package com.btm495.miyamoto;

import com.btm495.miyamoto.objects.Availability;
import com.btm495.miyamoto.objects.Booking;
import com.btm495.miyamoto.objects.Customer;
import com.btm495.miyamoto.objects.Employee;

public class SessionManager {

    private static Customer currentCustomer;
    private static Employee currentEmployee;
    private static Availability selectedAvailability;
    private static Booking pendingBooking;
    private static boolean viewAllBookings = false;

    public static Customer getCurrentCustomer() { return currentCustomer; }
    public static void setCurrentCustomer(Customer c) { currentCustomer = c; currentEmployee = null; }

    public static Employee getCurrentEmployee() { return currentEmployee; }
    public static void setCurrentEmployee(Employee e) { currentEmployee = e; currentCustomer = null; }

    public static void clearSession() {
        currentCustomer = null;
        currentEmployee = null;
        selectedAvailability = null;
        pendingBooking = null;
        viewAllBookings = false;
    }

    public static boolean isCustomerLoggedIn() { return currentCustomer != null; }
    public static boolean isEmployeeLoggedIn() { return currentEmployee != null; }

    public static Availability getSelectedAvailability() { return selectedAvailability; }
    public static void setSelectedAvailability(Availability a) { selectedAvailability = a; }

    public static Booking getPendingBooking() { return pendingBooking; }
    public static void setPendingBooking(Booking b) { pendingBooking = b; }

    public static boolean isViewAllBookings() { return viewAllBookings; }
    public static void setViewAllBookings(boolean b) { viewAllBookings = b; }
}
