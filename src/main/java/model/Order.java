package model;

public class Order {
    private int id;
    private int phoneId;
    private int buyerId;
    private int sellerId;
    private double price;
    private String status;

    public Order() {
    }

    public Order(int id, int phoneId, int buyerId, int sellerId,
                 double price, String status) {
        this.id = id;
        this.phoneId = phoneId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPhoneId() { return phoneId; }
    public void setPhoneId(int phoneId) { this.phoneId = phoneId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Order #" + id;
    }
}