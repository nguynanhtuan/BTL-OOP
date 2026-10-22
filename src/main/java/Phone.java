package model;

public class Phone {
    private int id;
    private String name;
    private String brand;
    private double price;
    private String description;
    private String imagePath;
    private String status;
    private int userId;

    public Phone() {
    }

    public Phone(int id, String name, String brand, double price,
                 String description, String imagePath, String status, int userId) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
        this.userId = userId;
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return name;
    }
}