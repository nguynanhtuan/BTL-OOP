/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.OrderDAO;
import dao.PhoneDAO;
import dao.UserDAO;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import model.Order;
import model.Phone;
import model.User;
import util.ReadOnlyTableModel;

/**
 * Giao diện chính của người dùng: xem máy, đơn mua, đơn bán, quản lý tin đăng.
 */
public class Main extends javax.swing.JFrame {

    // ==================== HẰNG SỐ ====================
    private static final String ALL = "Tất cả";
    private static final String UNKNOWN = "Unknown";

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Main.class.getName());

    // ==================== THUỘC TÍNH ====================
    private final PhoneDAO phoneDAO = new PhoneDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private User currentUser;
    private final int currentUserId;

    // ==================== KHỞI TẠO ====================

    /** Dùng khi chạy thử trực tiếp (không có thông tin đăng nhập). */
    public Main() {
        this((User) null);
    }

    /** Constructor nhận thông tin User từ form Đăng nhập. */
    public Main(User user) {
        this.currentUser = user;
        this.currentUserId = (user != null) ? user.getId() : -1;
        initComponents();
        setLocationRelativeTo(null);
        if (user != null && user.getFullName() != null) {
            jLabel1.setText("Xin chào: " + user.getFullName());
        }
        refreshAll();
    }

    /** Nạp dữ liệu cho tất cả các tab. */
    private void refreshAll() {
        loadDanhSachMay();
        loadDonMua();
        loadDonBan();
        loadTinDaDang();
    }

    /** Tên hiển thị của người dùng: họ tên nếu có, không thì username. */
    private String displayName(User u) {
        if (u == null) {
            return UNKNOWN;
        }
        return u.getFullName() != null ? u.getFullName() : u.getUsername();
    }

    // ==================== TAB DANH SÁCH MÁY ====================

    private void loadDanhSachMay() {
        loadDanhSachMay("", ALL);
    }

    private void loadDanhSachMay(String searchName, String searchBrand) {
        ReadOnlyTableModel model =
                new ReadOnlyTableModel("ID", "Tên máy", "Hãng", "Giá", "Trạng thái");
        jTable1.setModel(model);
        anCotID(jTable1);

        for (Phone p : phoneDAO.findAllApproved()) {
            if (!matchesKeyword(p, searchName) || !matchesBrand(p, searchBrand)) {
                continue;
            }
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getBrand(),
                String.format("%,.0f", p.getPrice()),
                p.getStatus()
            });
        }
    }

    private boolean matchesKeyword(Phone p, String keyword) {
        if (keyword == null || keyword.isEmpty() || keyword.startsWith("[")) {
            return true;
        }
        return p.getName() != null
                && p.getName().toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchesBrand(Phone p, String brand) {
        if (brand == null || ALL.equals(brand)) {
            return true;
        }
        return p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand);
    }

    // ==================== TAB ĐƠN MUA ====================

    private void loadDonMua() {
        ReadOnlyTableModel model = new ReadOnlyTableModel(
                "ID đơn", "Tên máy", "Giá", "Người bán", "Trạng thái", "Ngày tạo");
        jTableDonMua.setModel(model);
        anCotID(jTableDonMua);

        for (Order o : orderDAO.findByBuyer(currentUserId)) {
            Phone p = phoneDAO.findById(o.getPhoneId());
            User seller = userDAO.findById(o.getSellerId());

            model.addRow(new Object[]{
                o.getId(),
                p != null ? p.getName() : UNKNOWN,
                String.format("%,.0f", o.getPrice()),
                displayName(seller),
                o.getStatus(),
                o.getCreatedAt() != null ? dateTimeFormat.format(o.getCreatedAt()) : ""
            });
        }
    }

    // ==================== TAB ĐƠN BÁN ====================

    private void loadDonBan() {
        ReadOnlyTableModel model = new ReadOnlyTableModel(
                "ID đơn", "Tên máy", "Giá", "Người mua", "Trạng thái", "Ngày tạo", "Ngày xác nhận");
        jTableDonBan.setModel(model);
        anCotID(jTableDonBan);

        for (Order o : orderDAO.findBySeller(currentUserId)) {
            Phone p = phoneDAO.findById(o.getPhoneId());
            User buyer = userDAO.findById(o.getBuyerId());

            model.addRow(new Object[]{
                o.getId(),
                p != null ? p.getName() : UNKNOWN,
                String.format("%,.0f", o.getPrice()),
                displayName(buyer),
                o.getStatus(),
                o.getCreatedAt() != null ? dateTimeFormat.format(o.getCreatedAt()) : "",
                o.getConfirmedAt() != null ? dateTimeFormat.format(o.getConfirmedAt()) : "Chưa xác nhận"
            });
        }
    }

    // ==================== TAB QUẢN LÝ TIN ĐĂNG ====================

    private void loadTinDaDang() {
        ReadOnlyTableModel model = new ReadOnlyTableModel(
                "Mã tin", "Tên máy", "Hãng", "Giá (VND)", "Trạng thái", "Ngày đăng");
        tblQuanLyTin.setModel(model);
        anCotID(tblQuanLyTin);

        if (currentUser == null) {
            return;
        }

        String filter = cbLocTrangThaiTin.getSelectedItem().toString();

        for (Phone p : phoneDAO.findByUser(currentUser.getId())) {
            if (!matchesPostFilter(p.getStatus(), filter)) {
                continue;
            }
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getBrand(),
                String.format("%,.0f", p.getPrice()),
                p.getStatus(),
                p.getCreatedAt() != null ? dateFormat.format(p.getCreatedAt()) : ""
            });
        }
    }

    /** Lọc tin theo combobox. "Tất cả" không gồm tin đã ngừng bán (CANCELED). */
    private boolean matchesPostFilter(String status, String filter) {
        switch (filter) {
            case "Chờ duyệt":
                return "PENDING".equals(status);
            case "Đang bán":
                return "APPROVED".equals(status);
            case "Đã bán":
                return "SOLD".equals(status);
            default:
                return !"CANCELED".equals(status);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel9 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnThanhToan = new javax.swing.JButton();
        btnXemChiTietDonMua = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableDonMua = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableDonBan = new javax.swing.JTable();
        btnXemChiTietDonBan = new javax.swing.JButton();
        btnXacNhanDonBan = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnXoaTin = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cbLocTrangThaiTin = new javax.swing.JComboBox<>();
        btnXemChiTietTin = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblQuanLyTin = new javax.swing.JTable();
        btnThongTinTaiKhoan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel9.setBackground(new java.awt.Color(236, 244, 251));

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setBackground(new java.awt.Color(236, 244, 251));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", ""},
                {"", "", "", "", ""},
                {"", "", "", "", ""},
                {"", "", "", "", ""}
            },
            new String [] {
                "", "", "", "", ""
            }
        ));
        jTable1.setShowHorizontalLines(true);
        jTable1.setShowVerticalLines(true);
        jScrollPane1.setViewportView(jTable1);

        jButton6.setBackground(new java.awt.Color(0, 255, 0));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("Xem chi tiết");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jButton3.setBackground(new java.awt.Color(0, 102, 102));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("Đăng bán máy mới");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("Tìm kiếm");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Hãng:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Apple", "Samsung", "Xiaomi", "OPPO", "Vivo", "Realme", "Asus", "Nokia", "Huawei", "Sony", "OnePlus", "Google", "Infinix", "Khác", " " }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Tìm theo tên: ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(142, 142, 142)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        jPanel2.add(jPanel7);

        jTabbedPane1.addTab("Danh sách máy", jPanel2);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(236, 244, 251));

        btnThanhToan.setBackground(new java.awt.Color(0, 102, 102));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThanhToan.setText("Thanh toán");
        btnThanhToan.addActionListener(this::btnThanhToanActionPerformed);

        btnXemChiTietDonMua.setBackground(new java.awt.Color(0, 255, 0));
        btnXemChiTietDonMua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemChiTietDonMua.setText("Xem chi tiết đơn");
        btnXemChiTietDonMua.addActionListener(this::btnXemChiTietDonMuaActionPerformed);

        jButton7.setBackground(new java.awt.Color(255, 102, 0));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton7.setText("Huỷ đơn hàng");
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.addActionListener(this::jButton7ActionPerformed);

        jTableDonMua.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", ""},
                {"", "", "", "", ""},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", ""
            }
        ));
        jTableDonMua.setShowGrid(false);
        jScrollPane2.setViewportView(jTableDonMua);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 816, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnXemChiTietDonMua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnThanhToan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXemChiTietDonMua, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.add(jPanel1);

        jTabbedPane1.addTab("Đơn mua", jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel11.setBackground(new java.awt.Color(236, 244, 251));

        jTableDonBan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", "", null},
                {"", "", "", "", "", null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", "", ""
            }
        ));
        jTableDonBan.setShowHorizontalLines(true);
        jTableDonBan.setShowVerticalLines(true);
        jScrollPane3.setViewportView(jTableDonBan);

        btnXemChiTietDonBan.setBackground(new java.awt.Color(0, 255, 0));
        btnXemChiTietDonBan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemChiTietDonBan.setText("Xem chi tiết đơn");
        btnXemChiTietDonBan.addActionListener(this::btnXemChiTietDonBanActionPerformed);

        btnXacNhanDonBan.setBackground(new java.awt.Color(0, 102, 102));
        btnXacNhanDonBan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXacNhanDonBan.setText("Xác nhận");
        btnXacNhanDonBan.addActionListener(this::btnXacNhanDonBanActionPerformed);

        jButton5.setBackground(new java.awt.Color(255, 102, 0));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setText("Huỷ");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnXacNhanDonBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXemChiTietDonBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXemChiTietDonBan, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXacNhanDonBan, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.add(jPanel11);

        jTabbedPane1.addTab("Đơn bán", jPanel4);

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jPanel10.setBackground(new java.awt.Color(236, 244, 251));

        btnXoaTin.setBackground(new java.awt.Color(255, 102, 0));
        btnXoaTin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaTin.setText("Xoá tin/ Ngừng bán");
        btnXoaTin.addActionListener(this::btnXoaTinActionPerformed);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Lọc trạng thái :");

        cbLocTrangThaiTin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chờ duyệt", "Đang bán", "Đã bán" }));
        cbLocTrangThaiTin.addActionListener(this::cbLocTrangThaiTinActionPerformed);

        btnXemChiTietTin.setBackground(new java.awt.Color(0, 255, 0));
        btnXemChiTietTin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemChiTietTin.setText("Xem chi tiết");
        btnXemChiTietTin.addActionListener(this::btnXemChiTietTinActionPerformed);

        tblQuanLyTin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblQuanLyTin);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(cbLocTrangThaiTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnXemChiTietTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbLocTrangThaiTin, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnXemChiTietTin, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(btnXoaTin, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jPanel8.add(jPanel10);

        jTabbedPane1.addTab("Quản lý tin đăng", jPanel8);

        btnThongTinTaiKhoan.setBackground(new java.awt.Color(204, 204, 255));
        btnThongTinTaiKhoan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThongTinTaiKhoan.setText("Thông tin tài khoản");
        btnThongTinTaiKhoan.addActionListener(this::btnThongTinTaiKhoanActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Xin chào: Nguyễn Văn An");

        jButton8.setBackground(new java.awt.Color(51, 51, 255));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton8.setText("Làm mới");
        jButton8.addActionListener(this::jButton8ActionPerformed);

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Đăng xuất");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(46, 46, 46)
                .addComponent(btnThongTinTaiKhoan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addGap(19, 19, 19))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnThongTinTaiKhoan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1))
        );

        getContentPane().add(jPanel9);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new GiaoDienDangNhap().setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int selectedRow = jTableDonMua.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = jTableDonMua.getValueAt(selectedRow, 4).toString();
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy đơn hàng đang chờ xác nhận (PENDING)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(jTableDonMua.getValueAt(selectedRow, 0).toString());

        
            if (orderDAO.updateStatus(orderId, "CANCELED")) { 
                JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDonMua(); 
            } else {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi hủy đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnXacNhanDonBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXacNhanDonBanActionPerformed
        int selectedRow = jTableDonBan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn bán để xác nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = jTableDonBan.convertRowIndexToModel(selectedRow);
        String status = jTableDonBan.getModel().getValueAt(modelRow, 4).toString();

        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể xác nhận đơn hàng đang ở trạng thái PENDING!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận đơn hàng này? Đơn sẽ chuyển sang trạng thái Chờ thanh toán (WAITING_PAYMENT).", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
    int orderId = Integer.parseInt(jTableDonBan.getModel().getValueAt(modelRow, 0).toString());

    if (orderDAO.sellerConfirmOrder(orderId)) {
        JOptionPane.showMessageDialog(this, "Đã xác nhận đơn! Đang chờ người mua thanh toán.",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadDonBan();
        loadDanhSachMay();
    } else {
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng!",
            "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
    }//GEN-LAST:event_btnXacNhanDonBanActionPerformed
    private void capNhatNgayXacNhan(int orderId) {
        String sql = "UPDATE orders SET confirmed_at = NOW() WHERE id = ?";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int selectedRow = jTableDonBan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn bán để từ chối/hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = jTableDonBan.getValueAt(selectedRow, 4).toString();
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy đơn hàng đang ở trạng thái PENDING!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn từ chối đơn hàng này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(jTableDonBan.getValueAt(selectedRow, 0).toString());
            
            if (orderDAO.updateStatus(orderId, "CANCELED")) {
                JOptionPane.showMessageDialog(this, "Đã từ chối đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDonBan();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // 1. (Tùy chọn) Xóa dữ liệu cũ đang nhập trong ô tìm kiếm để lấy toàn bộ danh sách
    jTextField1.setText("");
    jComboBox1.setSelectedIndex(0);

    // 2. Gọi lại 3 hàm lấy dữ liệu từ cơ sở dữ liệu để làm mới cả 3 bảng
    loadDanhSachMay();
    loadDonMua();
    loadDonBan();
    loadTinDaDang();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void cbLocTrangThaiTinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLocTrangThaiTinActionPerformed
        loadTinDaDang();
    }//GEN-LAST:event_cbLocTrangThaiTinActionPerformed

    private void btnXoaTinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaTinActionPerformed
        int selectedRow = tblQuanLyTin.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một tin trên bảng để xóa/ngừng bán!");
            return;
        }

        // Lấy trạng thái hiện tại
        String currentStatus = tblQuanLyTin.getValueAt(selectedRow, 4).toString();
        if (currentStatus.equals("SOLD")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Máy đã bán thành công, không thể xóa!");
            return;
        }

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn ngừng bán sản phẩm này?", "Xác nhận", javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
    int phoneId = Integer.parseInt(tblQuanLyTin.getValueAt(selectedRow, 0).toString());

    if (phoneDAO.updateStatus(phoneId, "CANCELED")) {
        JOptionPane.showMessageDialog(this, "Đã ngừng bán thành công!");
        loadTinDaDang();
        loadDanhSachMay();
    } else {
        JOptionPane.showMessageDialog(this, "Ngừng bán thất bại!");
    }
}
    }//GEN-LAST:event_btnXoaTinActionPerformed

    private void btnXemChiTietTinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemChiTietTinActionPerformed
        int selectedRow = tblQuanLyTin.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một tin trên bảng để xem chi tiết!");
            return;
        }

        int phoneId = Integer.parseInt(tblQuanLyTin.getValueAt(selectedRow, 0).toString());

Phone selectedPhone = phoneDAO.findById(phoneId);
if (selectedPhone == null) {
    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sản phẩm!");
    return;
}

ChiTietSP chiTiet = new ChiTietSP(this, true, selectedPhone, currentUser);
chiTiet.hideBuyButton();
chiTiet.setLocationRelativeTo(this);
chiTiet.setVisible(true);
    }//GEN-LAST:event_btnXemChiTietTinActionPerformed

    private void btnXemChiTietDonMuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemChiTietDonMuaActionPerformed
        int selectedRow = jTableDonMua.getSelectedRow(); 
    
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng trên bảng để xem chi tiết!");
            return;
        }

        try {
            
            int modelRow = jTableDonMua.convertRowIndexToModel(selectedRow);
            int orderId = Integer.parseInt(jTableDonMua.getModel().getValueAt(modelRow, 0).toString());

            // Mở Dialog chi tiết đơn hàng
            view.ChiTietDonHang dialog = new view.ChiTietDonHang(this, true, orderId);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi định dạng ID đơn hàng.");
        }
    }//GEN-LAST:event_btnXemChiTietDonMuaActionPerformed

    private void btnXemChiTietDonBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemChiTietDonBanActionPerformed
        int selectedRow = jTableDonBan.getSelectedRow(); 
    
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng trên bảng để xem chi tiết!");
            return;
        }

        try {
            // Lấy ID đơn hàng ở cột số 0
            int modelRow = jTableDonBan.convertRowIndexToModel(selectedRow);
            int orderId = Integer.parseInt(jTableDonBan.getModel().getValueAt(modelRow, 0).toString());

            // Mở Dialog chi tiết đơn hàng
            view.ChiTietDonHang dialog = new view.ChiTietDonHang(this, true, orderId);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi định dạng ID đơn hàng.");
        }
    }//GEN-LAST:event_btnXemChiTietDonBanActionPerformed

    private void btnThongTinTaiKhoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongTinTaiKhoanActionPerformed
        User fresh = new dao.UserDAO().getUserById(currentUser.getId());
if (fresh != null) {
    currentUser = fresh;
}
new HoSo(this, true, currentUser).setVisible(true);
    }//GEN-LAST:event_btnThongTinTaiKhoanActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        int selectedRow = jTableDonMua.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng cần thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = jTableDonMua.convertRowIndexToModel(selectedRow);
        String status = jTableDonMua.getModel().getValueAt(modelRow, 4).toString();

        // Chỉ cho phép thanh toán khi người bán đã xác nhận (WAITING_PAYMENT)
        if (!status.equals("WAITING_PAYMENT")) {
            JOptionPane.showMessageDialog(this, 
                    "Chỉ có thể thanh toán cho đơn hàng ở trạng thái WAITING_PAYMENT (Chờ thanh toán)!", 
                    "Không thể thanh toán", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int orderId = Integer.parseInt(jTableDonMua.getModel().getValueAt(modelRow, 0).toString());

Order order = orderDAO.findById(orderId);
if (order == null) {
    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    return;
}

Phone phone = new Phone();
phone.setPrice(order.getPrice());

ThanhToan dialog = new ThanhToan(this, true, phone, true);
dialog.setVisible(true);

if (dialog.isCanceled()) {
    if (orderDAO.cancelWaitingPaymentOrder(orderId)) {
        JOptionPane.showMessageDialog(this, "Đã huỷ đơn hàng thành công!");
        loadDonMua();
        loadDanhSachMay();
    } else {
        JOptionPane.showMessageDialog(this, "Huỷ đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
} else {
    JOptionPane.showMessageDialog(this,
        "Nếu bạn đã chuyển khoản thành công, vui lòng chờ Admin kiểm tra và duyệt đơn hàng!",
        "Chờ xác nhận giao dịch", JOptionPane.INFORMATION_MESSAGE);
}

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xử lý thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String keyword = jTextField1.getText().trim();
        if (keyword.startsWith("[") && keyword.endsWith("]")) {
            keyword = "";
        }
        String selectedBrand = jComboBox1.getSelectedItem().toString();
        loadDanhSachMay(keyword, selectedBrand);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DangBanDT dangBanForm = new DangBanDT(this, true, currentUser);
        dangBanForm.setLocationRelativeTo(this);
        dangBanForm.setVisible(true);
        loadDanhSachMay();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một điện thoại trong danh sách để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int phoneId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

            Phone selectedPhone = phoneDAO.findById(phoneId);

            if (selectedPhone != null) {
                ChiTietSP chiTiet = new ChiTietSP(this, true, selectedPhone, currentUser);
                chiTiet.setLocationRelativeTo(this);
                chiTiet.setVisible(true);
                loadDanhSachMay();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton6ActionPerformed
                       
                                                                    

   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            User dummyUser = new User();
            dummyUser.setId(1);
            dummyUser.setFullName("Quản trị viên");
            new Main(dummyUser).setVisible(true);
        });
    }
    

    private void anCotID(javax.swing.JTable table) {
    // Ép chiều rộng của cột đầu tiên (cột 0 - chứa ID) về 0 pixel để ẩn đi
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThongTinTaiKhoan;
    private javax.swing.JButton btnXacNhanDonBan;
    private javax.swing.JButton btnXemChiTietDonBan;
    private javax.swing.JButton btnXemChiTietDonMua;
    private javax.swing.JButton btnXemChiTietTin;
    private javax.swing.JButton btnXoaTin;
    private javax.swing.JComboBox<String> cbLocTrangThaiTin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableDonBan;
    private javax.swing.JTable jTableDonMua;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tblQuanLyTin;
    // End of variables declaration//GEN-END:variables
}
