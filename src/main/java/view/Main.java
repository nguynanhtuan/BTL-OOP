/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.OrderDAO;
import dao.PhoneDAO;
import dao.UserDAO;
import model.Order;
import model.Phone;
import model.User;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;

public class Main extends javax.swing.JFrame {
    
    private User currentUser;
    private int currentUserId = 1;

    // Constructor nhận thông tin User từ form Đăng nhập
    public Main(User user) {
        this.currentUser = user;
        this.currentUserId = user.getId();
        initComponents();
        
        if (user.getFullName() != null) {
            jLabel1.setText("Xin chào: " + user.getFullName());
        }
        
        loadDanhSachMay();
        loadDonMua();
        loadDonBan();
        loadTinDaDang();
    }
    public Main() {
        initComponents();
        loadDanhSachMay();
        loadDonMua();
        loadDonBan();
        loadTinDaDang();
    }
    private void loadDanhSachMay() {
        loadDanhSachMay("", "Tất cả");
    }
    private void loadDanhSachMay(String searchName, String searchBrand) {
        String[] columnNames = {"ID", "Tên máy", "Hãng", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        jTable1.setModel(model);
        anCotID(jTable1);
        model.setRowCount(0);
        
        PhoneDAO phoneDAO = new PhoneDAO();
        UserDAO userDAO = new UserDAO();
        
        List<Phone> list = phoneDAO.findAllApproved(); 
        
        for (Phone p : list) {
            boolean matchName = true;
            if (searchName != null && !searchName.isEmpty() && !searchName.startsWith("[")) {
                matchName = p.getName() != null && p.getName().toLowerCase().contains(searchName.toLowerCase());
            }
            
            boolean matchBrand = true;
            if (searchBrand != null && !searchBrand.equals("Tất cả")) {
                matchBrand = p.getBrand() != null && p.getBrand().equalsIgnoreCase(searchBrand);
            }
            
            if (matchName && matchBrand) {
                User seller = userDAO.findById(p.getUserId());
                String sellerName = (seller != null) ? (seller.getFullName() != null ? seller.getFullName() : seller.getUsername()) : "Unknown";
                
                Object[] row = {
                    p.getId(),
                    p.getName(),
                    p.getBrand(),
                    String.format("%,.0f", p.getPrice()),
                    p.getStatus()
                };
                model.addRow(row);
            }
        }
    }
    private void loadDonMua() {
        String[] columnNames = {"ID đơn", "Tên máy", "Giá", "Người bán", "Trạng thái", "Ngày tạo"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        jTable2.setModel(model);
        anCotID(jTable2);
        model.setRowCount(0);
        
        OrderDAO orderDAO = new OrderDAO();
        PhoneDAO phoneDAO = new PhoneDAO();
        UserDAO userDAO = new UserDAO();

        List<Order> list = orderDAO.findByBuyer(currentUserId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Order o : list) {
            Phone p = phoneDAO.findById(o.getPhoneId()); 
            User seller = userDAO.findById(o.getSellerId()); 
            
            String phoneName = (p != null) ? p.getName() : "Unknown";
            String sellerName = (seller != null) ? (seller.getFullName() != null ? seller.getFullName() : seller.getUsername()) : "Unknown";
            String ngayDat = (o.getCreatedAt() != null) ? sdf.format(o.getCreatedAt()) : "";
            
            Object[] row = {
                o.getId(),
                phoneName,
                String.format("%,.0f", o.getPrice()),
                sellerName,
                o.getStatus(),
                ngayDat
            };
            model.addRow(row);
        }
    }
    private void loadDonBan() {
        String[] columnNames = {"ID đơn", "Tên máy", "Giá", "Người mua", "Trạng thái", "Ngày tạo"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        jTable3.setModel(model);
        anCotID(jTable3);
        model.setRowCount(0);
        
        OrderDAO orderDAO = new OrderDAO();
        PhoneDAO phoneDAO = new PhoneDAO();
        UserDAO userDAO = new UserDAO();

        List<Order> list = orderDAO.findBySeller(currentUserId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Order o : list) {
            Phone p = phoneDAO.findById(o.getPhoneId()); 
            User buyer = userDAO.findById(o.getBuyerId()); 
            
            String phoneName = (p != null) ? p.getName() : "Unknown";
            String buyerName = (buyer != null) ? (buyer.getFullName() != null ? buyer.getFullName() : buyer.getUsername()) : "Unknown";
            String ngayDat = (o.getCreatedAt() != null) ? sdf.format(o.getCreatedAt()) : "";
            
            Object[] row = {
                o.getId(),
                phoneName,
                String.format("%,.0f", o.getPrice()),
                buyerName,
                o.getStatus(),
                ngayDat
            };
            model.addRow(row);
        }
    }
    
     // --- HÀM PHỤ TRỢ: TRANSACTION CẬP NHẬT ĐƠN HÀNG VÀ CHUYỂN MÁY THÀNH SOLD ---
    private boolean confirmOrderAndSellPhone(int orderId) {
        String getPhoneIdQuery = "SELECT phone_id FROM orders WHERE id = ?";
        String updateOrderQuery = "UPDATE orders SET status = 'CONFIRMED' WHERE id = ?";
        String updatePhoneQuery = "UPDATE phones SET status = 'SOLD' WHERE id = ?";
        
        try (java.sql.Connection conn = util.DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bật transaction đảm bảo toàn vẹn dữ liệu
            
            int phoneId = -1;
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(getPhoneIdQuery)) {
                pstmt.setInt(1, orderId);
                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        phoneId = rs.getInt("phone_id");
                    }
                }
            }
            
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(updateOrderQuery)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
            }
            
            if (phoneId != -1) {
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(updatePhoneQuery)) {
                    pstmt.setInt(1, phoneId);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblQuanLyTin = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        cbLocTrangThaiTin = new javax.swing.JComboBox<>();
        btnXoaTin = new javax.swing.JButton();
        btnXemChiTietTin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Xin chào: Nguyễn Văn An");

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("[ Đăng xuất ]");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton8.setText("[ Làm mới ]");
        jButton8.addActionListener(this::jButton8ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 309, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addGap(35, 35, 35)
                .addComponent(jButton1)
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jButton8))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Tìm theo tên: ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Hãng:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Apple", "Samsung", "Xiaomi", "OPPO", "Vivo", "Realme", "Asus", "Nokia", "Huawei", "Sony", "OnePlus", "Google", "Infinix", "Khác", " " }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("[ Tìm kiếm ]");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(49, 49, 49)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(59, 59, 59))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel5, java.awt.BorderLayout.NORTH);

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

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("[ Đăng bán máy mới ]");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("[ Xem chi tiết ]");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 303, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(68, 68, 68))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton6))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Danh sách máy", jPanel2);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable2.setRowSelectionAllowed(false);
        jTable2.setShowGrid(false);
        jScrollPane2.setViewportView(jTable2);

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton7.setText("[ Huỷ đơn hàng ]");
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.addActionListener(this::jButton7ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(255, 255, 255)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Đơn mua", jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable3.setShowHorizontalLines(true);
        jTable3.setShowVerticalLines(true);
        jScrollPane3.setViewportView(jTable3);

        jPanel4.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("[ Xác nhận ]");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setText("[ Huỷ ]");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(118, 118, 118))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel7, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Đơn bán", jPanel4);

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

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Lọc trạng thái :");

        cbLocTrangThaiTin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chờ duyệt", "Đang bán", "Đã bán" }));
        cbLocTrangThaiTin.addActionListener(this::cbLocTrangThaiTinActionPerformed);

        btnXoaTin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaTin.setText("[ Xoá tin/ Ngừng bán ]");
        btnXoaTin.addActionListener(this::btnXoaTinActionPerformed);

        btnXemChiTietTin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemChiTietTin.setText("[ Xem chi tiết ]");
        btnXemChiTietTin.addActionListener(this::btnXemChiTietTinActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbLocTrangThaiTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnXemChiTietTin)
                .addGap(18, 18, 18)
                .addComponent(btnXoaTin)
                .addGap(55, 55, 55))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbLocTrangThaiTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoaTin)
                    .addComponent(btnXemChiTietTin))
                .addContainerGap(197, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quản lý tin đăng", jPanel8);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new GiaoDienDangNhap().setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = jTable2.getValueAt(selectedRow, 4).toString();
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy đơn hàng đang chờ xác nhận (PENDING)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(jTable2.getValueAt(selectedRow, 0).toString());

            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.updateStatus(orderId, "CANCELED")) { 
                JOptionPane.showMessageDialog(this, "Hủy đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDonMua(); 
            } else {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi hủy đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một điện thoại trong danh sách để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        try {
            int phoneId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
            PhoneDAO phoneDAO = new PhoneDAO();
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

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn bán để xác nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = jTable3.getValueAt(selectedRow, 4).toString();
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể xác nhận đơn hàng đang ở trạng thái PENDING!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn xác nhận đơn hàng này? Sản phẩm sẽ chuyển thành trạng thái Đã bán (SOLD).", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(jTable3.getValueAt(selectedRow, 0).toString());
            
            if (confirmOrderAndSellPhone(orderId)) {
                JOptionPane.showMessageDialog(this, "Xác nhận đơn hàng và cập nhật sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDonBan();
                loadDanhSachMay();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn bán để từ chối/hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = jTable3.getValueAt(selectedRow, 4).toString();
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy đơn hàng đang ở trạng thái PENDING!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn từ chối đơn hàng này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(jTable3.getValueAt(selectedRow, 0).toString());
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.updateStatus(orderId, "CANCELED")) {
                JOptionPane.showMessageDialog(this, "Đã từ chối đơn hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDonBan();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

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

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            int phoneId = Integer.parseInt(tblQuanLyTin.getValueAt(selectedRow, 0).toString());

            try (java.sql.Connection conn = util.DBConnection.getConnection();
                 java.sql.PreparedStatement pst = conn.prepareStatement("UPDATE phones SET status = 'CANCELED' WHERE id = ?")) {

                pst.setInt(1, phoneId);
                pst.executeUpdate();

                javax.swing.JOptionPane.showMessageDialog(this, "Đã ngừng bán thành công!");
                loadTinDaDang(); 
                loadDanhSachMay(); 

            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xóa tin: " + e.getMessage());
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

        try {
            // Truy vấn lấy thông tin chi tiết của phone theo ID từ database
            model.Phone selectedPhone = null;
            try (java.sql.Connection conn = util.DBConnection.getConnection();
                 java.sql.PreparedStatement pst = conn.prepareStatement("SELECT * FROM phones WHERE id = ?")) {
                pst.setInt(1, phoneId);
                java.sql.ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    selectedPhone = new model.Phone();
                    selectedPhone.setId(rs.getInt("id"));
                    selectedPhone.setName(rs.getString("name"));
                    selectedPhone.setBrand(rs.getString("brand"));
                    selectedPhone.setPrice(rs.getDouble("price"));
                    selectedPhone.setDescription(rs.getString("description"));
                    selectedPhone.setStatus(rs.getString("status"));
                    selectedPhone.setUserId(rs.getInt("user_id")); 
                    selectedPhone.setImagePath(rs.getString("image_path"));
                }
            }

            if (selectedPhone != null) {
                // Khởi tạo cửa sổ ChiTietSP
                view.ChiTietSP chiTiet = new view.ChiTietSP(this, true, selectedPhone, this.currentUser);

                // Gọi hàm ẩn nút mua
                chiTiet.hideBuyButton(); 

                // Đặt vị trí ra giữa màn hình và hiển thị
                chiTiet.setLocationRelativeTo(this);
                chiTiet.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi mở chi tiết: " + e.getMessage());
        }
    }//GEN-LAST:event_btnXemChiTietTinActionPerformed
                       
                                                                    

   
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
    
    private void loadTinDaDang() {
        String[] columnNames = {"Mã tin", "Tên máy", "Hãng", "Giá (VND)", "Trạng thái", "Ngày đăng"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(null, columnNames);
        tblQuanLyTin.setModel(model);
        anCotID(tblQuanLyTin);

        if (this.currentUser == null) return; 

        String trangThaiLoc = cbLocTrangThaiTin.getSelectedItem().toString();

        String sql = "SELECT id, name, brand, price, status, created_at FROM phones WHERE user_id = ?";

        // Cập nhật điều kiện lọc
        if (trangThaiLoc.equals("Tất cả")) {
            sql += " AND status != 'CANCELED'"; // <-- LOẠI BỎ TIN ĐÃ HỦY/XÓA KHỎI DANH SÁCH TẤT CẢ
        } else if (trangThaiLoc.equals("Chờ duyệt")) {
            sql += " AND status = 'PENDING'";
        } else if (trangThaiLoc.equals("Đang bán")) {
            sql += " AND status = 'APPROVED'";
        } else if (trangThaiLoc.equals("Đã bán")) {
            sql += " AND status = 'SOLD'";
        }

        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, currentUser.getId());
            java.sql.ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    String.format("%,.0f", rs.getDouble("price")),
                    rs.getString("status"),
                    rs.getDate("created_at")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi tải danh sách tin đăng: " + e.getMessage());
        }
    }
    private void anCotID(javax.swing.JTable table) {
    // Ép chiều rộng của cột đầu tiên (cột 0 - chứa ID) về 0 pixel để ẩn đi
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnXemChiTietTin;
    private javax.swing.JButton btnXoaTin;
    private javax.swing.JComboBox<String> cbLocTrangThaiTin;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tblQuanLyTin;
    // End of variables declaration//GEN-END:variables
}
