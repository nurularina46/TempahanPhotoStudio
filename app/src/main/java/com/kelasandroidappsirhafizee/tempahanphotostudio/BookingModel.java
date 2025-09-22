package com.kelasandroidappsirhafizee.tempahanphotostudio;

import java.util.Date;

public class BookingModel {
    private int id;
    private int userId;
    private int packageId;
    private int subPackageId;
    private String bookingDate;
    private String eventDate;
    private String status; // Pending, Confirmed, Completed, Cancelled
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus; // Pending, Paid, Refunded
    private String notes;
    private Date createdAt;

    public BookingModel() {}

    public BookingModel(int id, int userId, int packageId, int subPackageId, String bookingDate, 
                       String eventDate, String status, double totalAmount, String paymentMethod, 
                       String paymentStatus, String notes, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.subPackageId = subPackageId;
        this.bookingDate = bookingDate;
        this.eventDate = eventDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public int getSubPackageId() { return subPackageId; }
    public void setSubPackageId(int subPackageId) { this.subPackageId = subPackageId; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
