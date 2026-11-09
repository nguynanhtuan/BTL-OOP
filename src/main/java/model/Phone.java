package model;

public class Phone {
    private int id;
    private String name;
    private String brand;
    private double price;
    private String description;
    private String imagePath;
    private String videoPath; // Đã đưa lên đây cho gọn
    private String status;
    private int userId;
    private java.util.Date createdAt; 

    public Phone() {
    }

    // Đã thêm videoPath và createdAt vào Constructor đầy đủ
    public Phone(int id, String name, String brand, double price,
                 String description, String imagePath, String videoPath, String status, int userId, java.util.Date createdAt) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
        this.videoPath = videoPath;
        this.status = status;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public java.util.Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name;
    }
}