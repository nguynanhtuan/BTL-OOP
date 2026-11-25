package model;

public class OrderDetail {
    private String phoneName;
    private double price;
    private String imagePath;
    private String status;
    private String buyerName;
    private String sellerName;
    private String bankName;
    private String bankAccount;

    public OrderDetail() {
    }

    public String getPhoneName() { return phoneName; }
    public void setPhoneName(String phoneName) { this.phoneName = phoneName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    
    private int phoneId;

    public int getPhoneId() { return phoneId; }
    public void setPhoneId(int phoneId) { this.phoneId = phoneId; } 
}