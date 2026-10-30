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
    }

    // Constructor mặc định (dùng để test độc lập)
    public Main() {
        initComponents();
        loadDanhSachMay();
        loadDonMua();
        loadDonBan();
    }

    // --- CÁC HÀM LOAD DỮ LIỆU LÊN BẢNG ---

    private void loadDanhSachMay() {
        loadDanhSachMay("", "Tất cả");
    }

    // Hàm load danh sách máy có hỗ trợ bộ lọc Tìm kiếm (Tên và Hãng)
    private void loadDanhSachMay(String searchName, String searchBrand) {
        String[] columnNames = {"ID", "Tên máy", "Hãng", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        jTable1.setModel(model);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
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
        jPanel8 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Xin chào: Nguyễn Văn An");

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(51, 204, 0));
        jButton1.setText("[ Đăng xuất ]");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Tìm theo tên: ");

        jTextField1.setText("[_____________________________]");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Hãng:");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Samsung", "Iphone", "Xiaomi" }));

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
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(199, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên máy", "Hãng", "Giá", "Trạng thái"
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
                .addGap(70, 70, 70)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(54, 54, 54))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton6))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Danh sách máy", jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID đơn", "Tên máy", "Giá", "Người bán", "Trạng thái", "Ngày tạo"
            }
        ));
        jTable2.setShowHorizontalLines(true);
        jTable2.setShowVerticalLines(true);
        jScrollPane2.setViewportView(jTable2);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 0, 0));
        jButton7.setText("[ Huỷ đơn hàng ]");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(180, 180, 180)
                .addComponent(jButton7)
                .addContainerGap(195, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton7)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel8, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Đơn mua", jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID đơn", "Tên máy", "Giá", "Người mua", "Trạng thái", "Ngày tạo"
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
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

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    // --- CÁC SỰ KIỆN NÚT BẤM (ACTION LISTENERS) ---

    // 1. Nút Đăng xuất
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new GiaoDienDangNhap().setVisible(true);
        }
    }                                        

    // 2. Nút Tìm kiếm
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String keyword = jTextField1.getText().trim();
        if (keyword.startsWith("[") && keyword.endsWith("]")) {
            keyword = "";
        }
        String selectedBrand = jComboBox1.getSelectedItem().toString();
        loadDanhSachMay(keyword, selectedBrand);
    }                                        

    // 3. Nút Đăng bán máy mới (Truyền chuẩn currentUser sang DangBanDT)
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        DangBanDT dangBanForm = new DangBanDT(this, true, currentUser);
        dangBanForm.setLocationRelativeTo(this); 
        dangBanForm.setVisible(true);            
        loadDanhSachMay();
    }                                        

    // 4. Nút Xem chi tiết (Truyền đủ Phone và User)
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
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
    }                                        

    // 5. Nút Huỷ đơn hàng (Tab Đơn mua)
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {                                         
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
    }                                        

    // 6. Nút Xác nhận đơn hàng (Tab Đơn bán - Chuyển đơn thành CONFIRMED và máy thành SOLD)
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
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
    }                                        

    // 7. Nút Huỷ đơn hàng (Tab Đơn bán - Người bán từ chối đơn)
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
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
    }                                        

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

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration                   
}
