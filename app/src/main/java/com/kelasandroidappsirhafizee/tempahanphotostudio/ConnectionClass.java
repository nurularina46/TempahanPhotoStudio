package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionClass {

    // Ganti ikut server/database anda
    String ip = "10.0.2.2";
    String db = "TempahanPhotoStudio";
    String username = "sa";
    String password = "12345";
    String port = "1433";

    // Buat connection ke SQL Server
    public Connection CONN() {
        Connection conn = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            System.out.println("Loading JDBC driver...");
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            
            String connUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port +
                    ";databaseName=" + db + ";user=" + username + ";password=" + password + ";";
            System.out.println("Connection URL: " + connUrl);
            System.out.println("Attempting to connect to database...");
            
            conn = DriverManager.getConnection(connUrl);
            System.out.println("Database connection established successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            Log.e("SQL Connection", e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    // Test database connection and table structure
    public String testSubPackageTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("=== TESTING DATABASE CONNECTION ===");
            conn = CONN();
            if (conn == null) {
                return "❌ Database connection failed!";
            }
            System.out.println("✅ Database connection successful!");
            
            // Test if videography_sub_packages table exists
            String checkTableQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'videography_sub_packages'";
            stmt = conn.prepareStatement(checkTableQuery);
            rs = stmt.executeQuery();
            rs.next();
            int tableExists = rs.getInt(1);
            rs.close();
            stmt.close();
            
            if (tableExists == 0) {
                return "❌ videography_sub_packages table does not exist! Run fix_database_quick.sql first.";
            }
            System.out.println("✅ videography_sub_packages table exists!");
            
            // Check if packages table exists and has data
            String checkPackagesQuery = "SELECT COUNT(*) FROM packages WHERE category = 'Videography'";
            stmt = conn.prepareStatement(checkPackagesQuery);
            rs = stmt.executeQuery();
            rs.next();
            int videographyPackages = rs.getInt(1);
            rs.close();
            stmt.close();
            
            if (videographyPackages == 0) {
                return "❌ No videography packages found! Run fix_database_quick.sql first.";
            }
            System.out.println("✅ Found " + videographyPackages + " videography packages!");
            
            // Get first videography package ID
            String getFirstPackageQuery = "SELECT TOP 1 id, package_name FROM packages WHERE category = 'Videography' ORDER BY id";
            stmt = conn.prepareStatement(getFirstPackageQuery);
            rs = stmt.executeQuery();
            rs.next();
            int firstPackageId = rs.getInt("id");
            String firstPackageName = rs.getString("package_name");
            rs.close();
            stmt.close();
            
            System.out.println("✅ First package: ID=" + firstPackageId + ", Name=" + firstPackageName);
            
            // Test table structure
            String checkColumnsQuery = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'videography_sub_packages' ORDER BY ORDINAL_POSITION";
            stmt = conn.prepareStatement(checkColumnsQuery);
            rs = stmt.executeQuery();
            
            StringBuilder columns = new StringBuilder();
            while (rs.next()) {
                columns.append(rs.getString("COLUMN_NAME")).append(", ");
            }
            rs.close();
            stmt.close();
            
            System.out.println("✅ Table columns: " + columns.toString());
            
            // Test insert with valid package_id
            String testInsertQuery = "INSERT INTO videography_sub_packages (package_id, package_class, details, price, description, is_active) VALUES (?, 'Test', 'Test details', 100.0, 'Test description', 1)";
            stmt = conn.prepareStatement(testInsertQuery);
            stmt.setInt(1, firstPackageId);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Test insert successful!");
                
                // Delete test record
                String deleteTestQuery = "DELETE FROM videography_sub_packages WHERE package_class = 'Test'";
                stmt = conn.prepareStatement(deleteTestQuery);
                stmt.executeUpdate();
                stmt.close();
                
                System.out.println("✅ Test record deleted!");
                
                return "🎉 SUCCESS!\n" +
                       "📊 Videography packages: " + videographyPackages + "\n" +
                       "📦 First package: " + firstPackageName + " (ID: " + firstPackageId + ")\n" +
                       "🗄️ Table columns: " + columns.toString() + "\n" +
                       "✅ Database is ready for use!";
            } else {
                return "❌ FAILED: Could not insert test record";
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return "❌ ERROR: " + e.getMessage();
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    // Get available videography package IDs
    public ArrayList<Integer> getVideographyPackageIds() {
        ArrayList<Integer> packageIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = CONN();
            if (conn == null) return packageIds;
            
            String query = "SELECT id FROM packages WHERE category = 'Videography' ORDER BY id";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                packageIds.add(rs.getInt("id"));
            }
            
            System.out.println("Found videography package IDs: " + packageIds.toString());
        } catch (Exception e) {
            System.out.println("Error getting videography package IDs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return packageIds;
    }

    // ===================== PACKAGE =====================

    // Add Package (alias for insertPackage)
    public boolean addPackage(PackageModel packageModel) {
        return insertPackage(packageModel);
    }

    // Insert Package
    public boolean insertPackage(PackageModel packageModel) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "INSERT INTO packages (package_name, event, duration, category, description, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, packageModel.getPackageName());
            stmt.setString(2, packageModel.getEvent());
            stmt.setString(3, packageModel.getDuration());
            stmt.setString(4, packageModel.getCategory());
            stmt.setString(5, packageModel.getDescription());
            stmt.setString(6, packageModel.getImageUrl());

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }
        return isSuccess;
    }

    // Update Package
    public boolean updatePackage(PackageModel packageModel) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "UPDATE packages SET package_name=?, event=?, duration=?, category=?, description=?, image_url=? " +
                    "WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, packageModel.getPackageName());
            stmt.setString(2, packageModel.getEvent());
            stmt.setString(3, packageModel.getDuration());
            stmt.setString(4, packageModel.getCategory());
            stmt.setString(5, packageModel.getDescription());
            stmt.setString(6, packageModel.getImageUrl());
            stmt.setInt(7, packageModel.getId());

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }
        return isSuccess;
    }

    // Delete Package
    public boolean deletePackage(int packageId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "DELETE FROM packages WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, packageId);

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }
        return isSuccess;
    }

    // Get Package by ID
    public PackageModel getPackageById(int packageId) {
        PackageModel pkg = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return null;

            String query = "SELECT * FROM packages WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, packageId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                pkg = new PackageModel(
                        rs.getInt("id"),
                        rs.getString("package_name"),
                        rs.getString("event"),
                        rs.getString("duration"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_url")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return pkg;
    }

    // Get Packages by Category
    public ArrayList<PackageModel> getPackagesByCategory(String category) {
        ArrayList<PackageModel> packages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return packages;

            String query = "SELECT * FROM packages WHERE category = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, category);
            rs = stmt.executeQuery();

            while (rs.next()) {
                PackageModel packageModel = new PackageModel(
                        rs.getInt("id"),
                        rs.getString("package_name"),
                        rs.getString("event"),
                        rs.getString("duration"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_url")
                );
                packages.add(packageModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return packages;
    }

    // ===================== SUB PACKAGE =====================

    // Insert SubPackage
    public boolean insertSubPackage(SubPackageModel subPackage) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;

        try {
            System.out.println("=== INSERTING VIDEOGRAPHY SUB-PACKAGE ===");
            conn = CONN();
            if (conn == null) {
                System.out.println("❌ Database connection failed!");
                return false;
            }
            System.out.println("✅ Database connection successful!");

            // First, verify package exists
            String verifyQuery = "SELECT COUNT(*) FROM packages WHERE id = ? AND category = 'Videography'";
            PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
            verifyStmt.setInt(1, subPackage.getPackageId());
            ResultSet verifyRs = verifyStmt.executeQuery();
            verifyRs.next();
            int packageExists = verifyRs.getInt(1);
            verifyRs.close();
            verifyStmt.close();

            if (packageExists == 0) {
                System.out.println("❌ Package ID " + subPackage.getPackageId() + " does not exist in videography packages!");
                return false;
            }
            System.out.println("✅ Package ID " + subPackage.getPackageId() + " verified!");

            // Insert with all required columns based on database structure
            String query = "INSERT INTO videography_sub_packages (package_id, package_class, details, price, description, is_active) VALUES (?, ?, ?, ?, ?, ?)";
            System.out.println("📝 Query: " + query);
            System.out.println("📦 Package ID: " + subPackage.getPackageId());
            System.out.println("🏷️ Package Class: " + subPackage.getPackageClass());
            System.out.println("💰 Price: " + subPackage.getPrice());
            System.out.println("📋 Details: " + subPackage.getDetails());
            
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, subPackage.getPackageId());
            stmt.setString(2, subPackage.getPackageClass());
            stmt.setString(3, subPackage.getDetails());
            stmt.setDouble(4, subPackage.getPrice());
            stmt.setString(5, subPackage.getDescription() != null ? subPackage.getDescription() : "");
            stmt.setBoolean(6, true); // is_active = true by default

            int rowsAffected = stmt.executeUpdate();
            System.out.println("📊 Rows affected: " + rowsAffected);
            isSuccess = rowsAffected > 0;
            
            if (isSuccess) {
                System.out.println("🎉 Videography sub-package inserted successfully!");
            } else {
                System.out.println("❌ No rows were affected during insert!");
            }
        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
            System.out.println("🔍 Error Code: " + e.getErrorCode());
            System.out.println("📝 SQL State: " + e.getSQLState());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ General Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }

        return isSuccess;
    }

    // Update SubPackage
    public boolean updateSubPackage(SubPackageModel subPackage) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;

        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "UPDATE videography_sub_packages SET package_id=?, package_class=?, price=?, details=? WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, subPackage.getPackageId());
            stmt.setString(2, subPackage.getPackageClass());
            stmt.setDouble(3, subPackage.getPrice());
            stmt.setString(4, subPackage.getDetails());
            stmt.setInt(5, subPackage.getId());

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }

        return isSuccess;
    }

    // Delete SubPackage
    public boolean deleteSubPackage(int subPackageId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;

        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "DELETE FROM videography_sub_packages WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, subPackageId);

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }

        return isSuccess;
    }

    // Get SubPackages by PackageId
    public ArrayList<SubPackageModel> getSubPackagesByPackageId(int packageId) {
        ArrayList<SubPackageModel> subPackages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return subPackages;

            String query = "SELECT * FROM videography_sub_packages WHERE package_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, packageId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                SubPackageModel subPackage = new SubPackageModel(
                        rs.getInt("id"),
                        rs.getInt("package_id"),
                        rs.getString("package_class"),
                        rs.getDouble("price"),
                        rs.getString("details")
                );
                subPackages.add(subPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return subPackages;
    }

    // Get SubPackage by ID
    public SubPackageModel getSubPackageById(int subPackageId) {
        SubPackageModel subPackage = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return null;

            String query = "SELECT * FROM videography_sub_packages WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, subPackageId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                subPackage = new SubPackageModel(
                        rs.getInt("id"),
                        rs.getInt("package_id"),
                        rs.getString("package_class"),
                        rs.getDouble("price"),
                        rs.getString("details")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return subPackage;
    }

    // ===================== BOOKING =====================

    // Add Booking
    public String addBooking(BookingModel booking) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        
        try {
            conn = CONN();
            if (conn == null) return "Database connection failed";

            String query = "INSERT INTO bookings (user_id, package_id, sub_package_id, booking_date, event_date, status, total_amount, payment_method, payment_status, notes, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getPackageId());
            stmt.setInt(3, booking.getSubPackageId());
            stmt.setString(4, booking.getBookingDate());
            stmt.setString(5, booking.getEventDate());
            stmt.setString(6, booking.getStatus());
            stmt.setDouble(7, booking.getTotalAmount());
            stmt.setString(8, booking.getPaymentMethod());
            stmt.setString(9, booking.getPaymentStatus());
            stmt.setString(10, booking.getNotes());
            stmt.setString(11, booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : new java.util.Date().toString());

            isSuccess = stmt.executeUpdate() > 0;
            
            if (isSuccess) {
                return "Booking added successfully";
            } else {
                return "Failed to add booking";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    // Get Bookings by User ID
    public ArrayList<BookingModel> getBookingsByUserId(int userId) {
        ArrayList<BookingModel> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return bookings;

            String query = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                BookingModel booking = new BookingModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("package_id"),
                        rs.getInt("sub_package_id"),
                        rs.getString("booking_date"),
                        rs.getString("event_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getString("notes"),
                        rs.getDate("created_at")
                );
                bookings.add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return bookings;
    }

    // Get Booking by ID
    public BookingModel getBookingById(int bookingId) {
        BookingModel booking = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return null;

            String query = "SELECT * FROM bookings WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookingId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                booking = new BookingModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("package_id"),
                        rs.getInt("sub_package_id"),
                        rs.getString("booking_date"),
                        rs.getString("event_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getString("notes"),
                        rs.getDate("created_at")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return booking;
    }

    // Update Booking Status
    public boolean updateBookingStatus(int bookingId, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;

        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "UPDATE bookings SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, stmt, conn);
        }

        return isSuccess;
    }

    // ===================== USER AUTHENTICATION =====================

    // Validate User Login
    public String validateUser(String email, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return "Database connection failed";

            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            boolean isValid = rs.next();
            
            System.out.println("validateUser - Email: " + email + ", Password: " + password + ", Valid: " + isValid);
            
            if (isValid) {
                return "success";
            } else {
                return "Invalid email or password";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("validateUser - Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    // Get User by Email
    public UserModel getUserByEmail(String email) {
        UserModel user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return null;

            String query = "SELECT * FROM users WHERE email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new UserModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("name"), // fullName
                        "" // phoneNumber
                );
            }
            
            System.out.println("getUserByEmail - Email: " + email + ", User found: " + (user != null));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getUserByEmail - Error: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }

        return user;
    }

    // Register User
    public String registerUser(String name, String email, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;

        try {
            conn = CONN();
            if (conn == null) return "Database connection failed";

            // Check if user already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                return "User with this email already exists";
            }

            // Insert new user
            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);

            isSuccess = stmt.executeUpdate() > 0;
            
            if (isSuccess) {
                return "User registered successfully";
            } else {
                return "Failed to register user";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    // Test Database Connection
    public String testConnection() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = CONN();
            if (conn == null) return "Database connection failed";

            String query = "SELECT COUNT(*) FROM users";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int userCount = rs.getInt(1);
                return "Connection successful! Users table exists with " + userCount + " records.";
            } else {
                return "Connection successful but users table is empty.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection failed: " + e.getMessage();
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    // Check if user exists
    public boolean userExists(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }

        return exists;
    }

    // ===================== UTIL =====================

    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
