package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String status;
    private String bankAccount; // Số tài khoản
    private String bankName;    // Tên ngân hàng

    public User() {
    }

    // Cập nhật Constructor đầy đủ (thêm bankAccount và bankName)
    public User(int id, String username, String password, String fullName, String phoneNumber, String role, String bankAccount, String bankName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
    }

    // ==================== GETTER & SETTER ====================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
}