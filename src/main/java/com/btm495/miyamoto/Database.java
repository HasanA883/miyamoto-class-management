package com.btm495.miyamoto;

import com.btm495.miyamoto.objects.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {

//    private static final String URL = ("jdbc:sqlite:" + System.getProperty("user.dir") + "\\database.db").replaceAll("\\\\", "/");
    private static final String URL = "jdbc:sqlite:C:/Users/Hasan/IdeaProjects/Miyamoto/database.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    customerId INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    address TEXT,
                    phoneNumber TEXT,
                    password TEXT NOT NULL,
                    emailAddress TEXT UNIQUE NOT NULL,
                    loyaltyPoints INTEGER DEFAULT 0
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    employeeId TEXT PRIMARY KEY,
                    firstName TEXT NOT NULL,
                    lastName TEXT NOT NULL,
                    emailAddress TEXT UNIQUE NOT NULL,
                    phoneNumber TEXT,
                    employeeRole TEXT,
                    hireDate TEXT,
                    isActive INTEGER DEFAULT 1
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS availability (
                    availabilityId INTEGER PRIMARY KEY AUTOINCREMENT,
                    availableStartTime TEXT,
                    availableEndTime TEXT,
                    numberOfBookings INTEGER DEFAULT 0,
                    bookingType TEXT,
                    description TEXT,
                    basePrice REAL,
                    defaultChef TEXT,
                    materials TEXT,
                    isActive INTEGER DEFAULT 1
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bookings (
                    bookingId INTEGER PRIMARY KEY AUTOINCREMENT,
                    bookingType TEXT,
                    bookingDuration TEXT,
                    bookingDate TEXT,
                    bookingStart TEXT,
                    bookingEnd TEXT,
                    bookingLocation TEXT,
                    bookingCost REAL,
                    bookingStatus TEXT DEFAULT 'Pending',
                    employeeId TEXT,
                    customerId INTEGER,
                    totalEnrollment INTEGER DEFAULT 1,
                    totalPrice REAL,
                    bookingRating INTEGER DEFAULT 0,
                    bookingReview TEXT,
                    availabilityId INTEGER
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payments (
                    paymentId INTEGER PRIMARY KEY AUTOINCREMENT,
                    transactionNumber TEXT,
                    amount REAL,
                    paymentMethod TEXT,
                    paymentStatus TEXT,
                    bookingId INTEGER
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS invoices (
                    invoiceId INTEGER PRIMARY KEY AUTOINCREMENT,
                    paymentId INTEGER,
                    generationDate TEXT,
                    dueDate TEXT,
                    subTotal REAL,
                    taxAmount REAL,
                    totalAmount REAL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notifications (
                    notificationId INTEGER PRIMARY KEY AUTOINCREMENT,
                    bookingId INTEGER,
                    type TEXT,
                    recipientEmail TEXT,
                    messageContent TEXT,
                    sentDate TEXT,
                    notificationStatus TEXT,
                    subject TEXT
                )
            """);

            // Schema migrations — safe to run on every startup
            try { stmt.execute("ALTER TABLE employees ADD COLUMN password TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE availability ADD COLUMN duration TEXT"); } catch (SQLException ignored) {}

            // Seed default employee
            stmt.execute("""
                INSERT OR IGNORE INTO employees (employeeId, firstName, lastName, emailAddress, phoneNumber, employeeRole, hireDate, isActive, password)
                VALUES ('EMP001', 'Tanaka', 'San', 'admin@miyamoto.com', '5141234567', 'Manager', '2020-01-01', 1, 'admin123')
            """);
            // Ensure seed employee always has a password set
            stmt.execute("UPDATE employees SET password = 'admin123' WHERE employeeId = 'EMP001' AND (password IS NULL OR password = '')");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== CUSTOMERS =====

    public static boolean insertCustomer(String name, String address, String phoneNumber, String password, String emailAddress) {
        String sql = "INSERT INTO customers (name, address, phoneNumber, password, emailAddress, loyaltyPoints) VALUES (?,?,?,?,?,0)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, phoneNumber);
            ps.setString(4, password);
            ps.setString(5, emailAddress);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Customer getCustomerByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM customers WHERE emailAddress = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer c = new Customer(
                    rs.getString("name"),
                    rs.getString("address"),
                    parsePhone(rs.getString("phoneNumber")),
                    rs.getString("password"),
                    rs.getString("emailAddress"),
                    rs.getInt("loyaltyPoints")
                );
                c.setCustomerId(rs.getInt("customerId"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE emailAddress = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== EMPLOYEES =====

    public static Employee getEmployeeByEmailAndIdAndPassword(String email, String employeeId, String password) {
        String sql = "SELECT * FROM employees WHERE emailAddress = ? AND employeeId = ? AND password = ? AND isActive = 1";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, employeeId);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee e = new Employee(
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("emailAddress"),
                    parsePhone(rs.getString("phoneNumber")),
                    null,
                    rs.getString("employeeRole"),
                    null,
                    rs.getInt("isActive") == 1
                );
                e.setEmployeeId(rs.getString("employeeId"));
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== AVAILABILITY =====

    public static List<Availability> getAllAvailability() {
        List<Availability> list = new ArrayList<>();
        String sql = "SELECT * FROM availability ORDER BY availableStartTime";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapAvailability(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Availability> getAllActiveAvailability() {
        List<Availability> list = new ArrayList<>();
        String sql = "SELECT * FROM availability WHERE isActive = 1 ORDER BY availableStartTime";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapAvailability(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Availability> getAllActiveUpcomingAvailability() {
        List<Availability> list = new ArrayList<>();
        String sql = "SELECT * FROM availability WHERE isActive = 1 AND availableStartTime > ? ORDER BY availableStartTime";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAvailability(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Availability getAvailabilityById(int id) {
        String sql = "SELECT * FROM availability WHERE availabilityId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAvailability(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean insertAvailability(Availability a) {
        String sql = "INSERT INTO availability (availableStartTime, availableEndTime, numberOfBookings, bookingType, description, basePrice, defaultChef, materials, duration) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAvailableStartTime() != null ? a.getAvailableStartTime().toString() : null);
            ps.setString(2, a.getAvailableEndTime() != null ? a.getAvailableEndTime().toString() : null);
            ps.setInt(3, a.getNumberOfBookings());
            ps.setString(4, a.getBookingType());
            ps.setString(5, a.getDescription());
            ps.setDouble(6, a.getBasePrice() != null ? a.getBasePrice() : 0);
            ps.setString(7, a.getDefaultChef());
            ps.setString(8, a.getMaterials());
            ps.setString(9, a.getDurationText());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateAvailabilityRecord(Availability a) {
        String sql = "UPDATE availability SET availableStartTime=?, availableEndTime=?, numberOfBookings=?, bookingType=?, description=?, basePrice=?, defaultChef=?, materials=?, duration=? WHERE availabilityId=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAvailableStartTime() != null ? a.getAvailableStartTime().toString() : null);
            ps.setString(2, a.getAvailableEndTime() != null ? a.getAvailableEndTime().toString() : null);
            ps.setInt(3, a.getNumberOfBookings());
            ps.setString(4, a.getBookingType());
            ps.setString(5, a.getDescription());
            ps.setDouble(6, a.getBasePrice() != null ? a.getBasePrice() : 0);
            ps.setString(7, a.getDefaultChef());
            ps.setString(8, a.getMaterials());
            ps.setString(9, a.getDurationText());
            ps.setInt(10, a.getAvailabilityId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deactivateAvailability(int availabilityId) {
        String sql = "UPDATE availability SET isActive = 0 WHERE availabilityId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reactivateAvailability(int availabilityId) {
        String sql = "UPDATE availability SET isActive = 1 WHERE availabilityId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns true if any OTHER active availability overlaps with [start, end].
     * excludeId = 0 (or -1) means no exclusion (used for new records).
     */
    public static boolean hasActiveOverlap(LocalDateTime start, LocalDateTime end, int excludeId) {
        if (start == null || end == null) return false;
        String sql = """
            SELECT COUNT(*) FROM availability
            WHERE isActive = 1
            AND availabilityId != ?
            AND availableStartTime < ?
            AND availableEndTime > ?
        """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, excludeId);
            ps.setString(2, end.toString());
            ps.setString(3, start.toString());
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Auto-completes any Confirmed bookings whose end time has already passed,
     * and inserts a Review Request notification for each.
     * Uses bookingEnd directly — no dependency on availabilityId being set.
     */
    public static void sendEndOfClassNotifications() {
        String findSql = """
            SELECT b.bookingId, b.bookingType, c.emailAddress AS custEmail
            FROM bookings b
            LEFT JOIN customers c ON b.customerId = c.customerId
            WHERE b.bookingStatus = 'Confirmed'
            AND b.bookingEnd IS NOT NULL
            AND b.bookingEnd < ?
        """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setString(1, LocalDateTime.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("bookingId");
                String type = rs.getString("bookingType");
                String email = rs.getString("custEmail");
                updateBookingStatus(bookingId, "Completed");
                if (email != null) {
                    insertNotification(bookingId, "Review Request", email,
                        "How was your " + type + " class?",
                        "Thank you for attending! We'd love to hear your feedback. Please log in to rate and review your class.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== BOOKINGS =====

    public static int insertBooking(Booking b) {
        String sql = "INSERT INTO bookings (bookingType, bookingDuration, bookingDate, bookingStart, bookingEnd, bookingLocation, bookingCost, bookingStatus, customerId, totalEnrollment, totalPrice, availabilityId) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getBookingType());
            ps.setString(2, b.getBookingDuration());
            ps.setString(3, b.getBookingDate() != null ? b.getBookingDate().toString() : null);
            ps.setString(4, b.getBookingStart() != null ? b.getBookingStart().toString() : null);
            ps.setString(5, b.getBookingEnd() != null ? b.getBookingEnd().toString() : null);
            ps.setString(6, b.getBookingLocation());
            ps.setDouble(7, b.getBookingCost() != null ? b.getBookingCost() : 0);
            ps.setString(8, b.getBookingStatus());
            ps.setInt(9, b.getCustomerId() != null ? Integer.parseInt(b.getCustomerId()) : 0);
            ps.setInt(10, b.getTotalEnrollment());
            ps.setDouble(11, b.getTotalPrice() != null ? b.getTotalPrice() : 0);
            ps.setInt(12, b.getAvailabilityId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customerId = ? ORDER BY bookingDate DESC";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY bookingDate DESC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Booking> getAllConfirmedUpcomingBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = """
            SELECT b.* FROM bookings b
            JOIN availability a ON b.availabilityId = a.availabilityId
            WHERE b.bookingStatus = 'Confirmed'
            AND a.availableStartTime > ?
            ORDER BY a.availableStartTime
        """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Booking> getConfirmedUpcomingBookingsByCustomer(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = """
            SELECT b.* FROM bookings b
            JOIN availability a ON b.availabilityId = a.availabilityId
            WHERE b.customerId = ?
            AND b.bookingStatus = 'Confirmed'
            AND a.availableStartTime > ?
            ORDER BY a.availableStartTime
        """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, LocalDateTime.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Booking> getBookingsByAvailabilityId(int availabilityId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE availabilityId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET bookingStatus = ? WHERE bookingId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addBookingReview(int bookingId, int rating, String review) {
        String sql = "UPDATE bookings SET bookingRating = ?, bookingReview = ? WHERE bookingId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, review);
            ps.setInt(3, bookingId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reassigns a booking to a different availability slot and recalculates the total price.
     */
    public static boolean updateBookingFromAvailability(int bookingId, int newAvailabilityId, int participants) {
        Availability a = getAvailabilityById(newAvailabilityId);
        if (a == null) return false;
        double total = (a.getBasePrice() != null ? a.getBasePrice() : 0) * participants;
        String sql = "UPDATE bookings SET availabilityId=?, bookingType=?, bookingDuration=?, bookingStart=?, bookingEnd=?, totalEnrollment=?, totalPrice=? WHERE bookingId=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAvailabilityId);
            ps.setString(2, a.getBookingType());
            ps.setString(3, a.getDurationText());
            ps.setString(4, a.getAvailableStartTime() != null ? a.getAvailableStartTime().toString() : null);
            ps.setString(5, a.getAvailableEndTime() != null ? a.getAvailableEndTime().toString() : null);
            ps.setInt(6, participants);
            ps.setDouble(7, total);
            ps.setInt(8, bookingId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates participant count and recalculates total price from the linked availability's base price.
     */
    public static boolean updateBookingParticipants(int bookingId, int participants) {
        double basePrice = 0;
        String getSql = "SELECT a.basePrice FROM bookings b LEFT JOIN availability a ON b.availabilityId = a.availabilityId WHERE b.bookingId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(getSql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) basePrice = rs.getDouble("basePrice");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        double total = basePrice * participants;
        String updateSql = "UPDATE bookings SET totalEnrollment=?, totalPrice=? WHERE bookingId=?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, participants);
            ps.setDouble(2, total);
            ps.setInt(3, bookingId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== PAYMENTS =====

    public static int insertPayment(String transactionNumber, double amount, String paymentMethod, int bookingId) {
        String sql = "INSERT INTO payments (transactionNumber, amount, paymentMethod, paymentStatus, bookingId) VALUES (?,?,?,'Completed',?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transactionNumber);
            ps.setDouble(2, amount);
            ps.setString(3, paymentMethod);
            ps.setInt(4, bookingId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ===== INVOICES =====

    public static boolean insertInvoice(int paymentId, double subTotal, double taxAmount, double totalAmount) {
        String sql = "INSERT INTO invoices (paymentId, generationDate, dueDate, subTotal, taxAmount, totalAmount) VALUES (?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setString(2, LocalDateTime.now().toString());
            ps.setString(3, LocalDateTime.now().plusDays(30).toString());
            ps.setDouble(4, subTotal);
            ps.setDouble(5, taxAmount);
            ps.setDouble(6, totalAmount);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== NOTIFICATIONS =====

    public static List<Notification> getNotificationsByCustomerEmail(String email) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipientEmail = ? ORDER BY sentDate DESC";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification(
                    rs.getString("recipientEmail"),
                    rs.getString("type"),
                    rs.getString("messageContent"),
                    parseDateTime(rs.getString("sentDate")),
                    rs.getString("notificationStatus"),
                    rs.getString("subject")
                );
                n.setNotificationId(rs.getInt("notificationId"));
                n.setBookingId(rs.getInt("bookingId"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getUnreadNotificationCount(String email) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE recipientEmail = ? AND notificationStatus = 'Sent'";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean markNotificationRead(int notificationId) {
        String sql = "UPDATE notifications SET notificationStatus = 'Read' WHERE notificationId = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean markAllNotificationsRead(String email) {
        String sql = "UPDATE notifications SET notificationStatus = 'Read' WHERE recipientEmail = ? AND notificationStatus = 'Sent'";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Notifies all customers with Confirmed/Pending bookings for the given availability
     * that their class details have been updated by staff.
     */
    public static void notifyBookedCustomersOfClassUpdate(int availabilityId, String classType) {
        String sql = """
            SELECT b.bookingId, c.emailAddress
            FROM bookings b
            JOIN customers c ON b.customerId = c.customerId
            WHERE b.availabilityId = ?
            AND b.bookingStatus IN ('Confirmed', 'Pending')
        """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                insertNotification(rs.getInt("bookingId"), "Class Update", rs.getString("emailAddress"),
                    "Update: " + classType,
                    "The details for your upcoming class \"" + classType + "\" have been updated by staff. " +
                    "Please log in to review your booking for the latest date, time, and information.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertNotification(int bookingId, String type, String recipientEmail, String subject, String messageContent) {
        String sql = "INSERT INTO notifications (bookingId, type, recipientEmail, subject, messageContent, sentDate, notificationStatus) VALUES (?,?,?,?,?,?,'Sent')";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, type);
            ps.setString(3, recipientEmail);
            ps.setString(4, subject);
            ps.setString(5, messageContent);
            ps.setString(6, LocalDateTime.now().toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== PRIVATE HELPERS =====

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDateTime.parse(s); } catch (Exception e) { return null; }
    }

    private static int parsePhone(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            String digits = s.replaceAll("[^0-9]", "");
            if (digits.length() > 9) digits = digits.substring(0, 9);
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        } catch (Exception e) { return 0; }
    }

    private static Availability mapAvailability(ResultSet rs) throws SQLException {
        Availability a = new Availability(
            parseDateTime(rs.getString("availableStartTime")),
            parseDateTime(rs.getString("availableEndTime")),
            rs.getInt("numberOfBookings"),
            rs.getString("bookingType"),
            rs.getString("description"),
            rs.getDouble("basePrice"),
            rs.getString("defaultChef"),
            null,
            rs.getString("materials")
        );
        a.setAvailabilityId(rs.getInt("availabilityId"));
        a.setActive(rs.getInt("isActive") == 1);
        try { a.setDurationText(rs.getString("duration")); } catch (SQLException ignored) {}
        return a;
    }

    private static Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking(
            rs.getString("bookingType"),
            rs.getString("bookingDuration"),
            parseDateTime(rs.getString("bookingDate")),
            parseDateTime(rs.getString("bookingStart")),
            parseDateTime(rs.getString("bookingEnd")),
            rs.getString("bookingLocation"),
            rs.getDouble("bookingCost"),
            rs.getString("bookingStatus"),
            rs.getInt("totalEnrollment"),
            rs.getDouble("totalPrice"),
            rs.getInt("bookingRating"),
            rs.getString("bookingReview")
        );
        b.setBookingId(rs.getInt("bookingId"));
        b.setEmployeeId(rs.getString("employeeId"));
        b.setCustomerId(String.valueOf(rs.getInt("customerId")));
        b.setAvailabilityId(rs.getInt("availabilityId"));
        return b;
    }
}
