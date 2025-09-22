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
    String ip = "YOUR_SERVER_IP";
    String db = "YOUR_DATABASE_NAME";
    String username = "YOUR_USERNAME";
    String password = "YOUR_PASSWORD";
    String port = "1433";

    // Buat connection ke SQL Server
    public Connection CONN() {
        Connection conn = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + db + ";user=" + username + ";password=" + password + ";";
            conn = DriverManager.getConnection(connUrl);
        } catch (Exception e) {
            Log.e("SQL Connection", e.getMessage());
        }
        return conn;
    }

    // ===================== PACKAGE =====================

    // Insert Package
    public boolean insertPackage(PackageModel packageModel) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isSuccess = false;
        try {
            conn = CONN();
            if (conn == null) return false;

            String query = "INSERT INTO packages (package_name, event, duration, category, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";
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

            String query = "UPDATE packages SET package_name=?, event=?, duration=?, category=?, description=?, image_url=? WHERE id=?";
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
            conn = CONN();
            if (conn == null) return false;

            String query = "INSERT INTO sub_packages (package_id, package_class, price, details) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, subPackage.getPackageId());
            stmt.setString(2, subPackage.getPackageClass());
            stmt.setDouble(3, subPackage.getPrice());
            stmt.setString(4, subPackage.getDetails());

            isSuccess = stmt.executeUpdate() > 0;
        } catch (Exception e) {
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

            String query = "UPDATE sub_packages SET package_id=?, package_class=?, price=?, details=? WHERE id=?";
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

            String query = "DELETE FROM sub_packages WHERE id=?";
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

            String query = "SELECT * FROM sub_packages WHERE package_id = ?";
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

            String query = "SELECT * FROM sub_packages WHERE id = ?";
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
