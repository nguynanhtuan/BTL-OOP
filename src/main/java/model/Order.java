package model;

public class Order {
    private int id;
    private int phoneId;
    private int buyerId;
    private int sellerId;
    private double price;
    private String status;
    private java.util.Date createdAt;
    private java.util.Date confirmedAt; // Ngày xác nhận đơn

    public Order() {
    }

    // Đã thêm confirmedAt vào Constructor đầy đủ
    public Order(int id, int phoneId, int buyerId, int sellerId,
                 double price, String status, java.util.Date createdAt, java.util.Date confirmedAt) {
        this.id = id;
        this.phoneId = phoneId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
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

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    // --- THÊM 2 HÀM NÀY ĐỂ HẾT LỖI ĐỎ BÊN DAO ---
    public java.util.Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(java.util.Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    // ---------------------------------------------

    @Override
    public String toString() {
        return "Order #" + id;
    }
}