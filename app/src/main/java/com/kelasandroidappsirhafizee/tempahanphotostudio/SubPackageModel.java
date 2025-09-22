package com.kelasandroidappsirhafizee.tempahanphotostudio;

public class SubPackageModel {
    private int id;
    private int packageId;
    private String packageClass;
    private double price;
    private String details;

    // Constructor
    public SubPackageModel(int id, int packageId, String packageClass, double price, String details) {
        this.id = id;
        this.packageId = packageId;
        this.packageClass = packageClass;
        this.price = price;
        this.details = details;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageClass() {
        return packageClass;
    }

    public void setPackageClass(String packageClass) {
        this.packageClass = packageClass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
