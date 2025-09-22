package com.kelasandroidappsirhafizee.tempahanphotostudio;

public class PackageModel {
    private int id;
    private String packageName;
    private String event;
    private String duration;
    private String category;
    private String description;
    private String imageUrl;

    // Empty constructor
    public PackageModel() {}

    // Full constructor
    public PackageModel(int id, String packageName, String event, String duration,
                        String category, String description, String imageUrl) {
        this.id = id;
        this.packageName = packageName;
        this.event = event;
        this.duration = duration;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getPackageName() { return packageName; }
    public String getEvent() { return event; }
    public String getDuration() { return duration; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setEvent(String event) { this.event = event; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
