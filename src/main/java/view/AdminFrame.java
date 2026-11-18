/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.OrderDAO;
import dao.PhoneDAO;
import dao.UserDAO;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Order;
import model.Phone;
import model.User;


public class AdminFrame extends javax.swing.JFrame {

    // ==================== HẰNG SỐ ====================
    private static final String ALL = "Tất cả";
    private static final String UNKNOWN = "Không xác định";
    private static final java.awt.Font HEADER_FONT =
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13);

    // ==================== THUỘC TÍNH ====================
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(AdminFrame.class.getName());

    private final PhoneDAO phoneDAO = new PhoneDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    // Người dùng (admin) đang đăng nhập
    private final User currentUser;

    // ==================== KHỞI TẠO ====================

   
    public AdminFrame() {
        this((User) null);
    }

    public AdminFrame(User user) {
        this.currentUser = user;
        initComponents();
        setupTables();
        refreshAll();
        // this.setTitle("Trang Quản Trị - Xin chào: " + user.getFullName());
    }

   
    private void setupTables() {
        khoaSuaBang();
        for (JTable table : new JTable[]{jTable1, jTable2, jTable3}) {
            table.getTableHeader().setFont(HEADER_FONT);
        }
    }

   
    private void refreshAll() {
        loadDonChoThanhToan();
        loadDataToTable1(ALL);
        loadDataToTable2();
        loadDataToTable3(ALL);
    }

    // ==================== TABLE MODEL DÙNG CHUNG ====================

    
    private static class ReadOnlyTableModel extends DefaultTableModel {

        ReadOnlyTableModel(Object[] columnNames) {
            super(columnNames, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    
    private static String[] columnNamesOf(JTable table) {
        TableModel model = table.getModel();
        String[] names = new String[model.getColumnCount()];
        for (int i = 0; i < names.length; i++) {
            names[i] = model.getColumnName(i);
        }
        return names;
    }

   
    private void khoaSuaBang() {
        jTable1.setModel(new ReadOnlyTableModel(columnNamesOf(jTable1)));
        jTable2.setModel(new ReadOnlyTableModel(columnNamesOf(jTable2)));
        jTable3.setModel(new ReadOnlyTableModel(columnNamesOf(jTable3)));
    }

    // ==================== TAB QUẢN LÝ TIN (jTable1) ====================

    private void loadDataToTable1(String statusFilter) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        List<Phone> list = ALL.equals(statusFilter)
                ? phoneDAO.findAll()
                : phoneDAO.findByStatus(statusFilter);

        for (Phone p : list) {
            User poster = userDAO.findById(p.getUserId());

            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                poster != null ? poster.getUsername() : UNKNOWN,
                p.getStatus(),
                p.getCreatedAt() != null ? dateFormat.format(p.getCreatedAt()) : ""
            });
        }
    }

    // ==================== TAB QUẢN LÝ USER (jTable2) ====================

    private void loadDataToTable2() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        for (User u : userDAO.findAll()) {
            model.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getPhoneNumber(),
                u.getRole(),
                u.getStatus()
            });
        }
    }

    // ==================== TAB QUẢN LÝ ĐƠN HÀNG (jTable3) ====================

    private void loadDataToTable3(String statusFilter) {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);

        for (Order order : orderDAO.findAll()) {
            String status = order.getStatus();

            // Đơn PENDING chưa vào quy trình của admin
            if ("PENDING".equalsIgnoreCase(status)) {
                continue;
            }
            if (!matchesOrderFilter(status, statusFilter)) {
                continue;
            }

            Phone phone = phoneDAO.findById(order.getPhoneId());
            User seller = userDAO.findById(order.getSellerId());
            User buyer = userDAO.findById(order.getBuyerId());

            model.addRow(new Object[]{
                order.getId(),
                phone != null ? phone.getName() : UNKNOWN,
                seller != null ? seller.getUsername() : UNKNOWN,
                buyer != null ? buyer.getUsername() : UNKNOWN,
                moneyFormat.format(order.getPrice()) + " VNĐ",
                status,
                order.getConfirmedAt() != null
                        ? dateTimeFormat.format(order.getConfirmedAt())
                        : "Chưa xác nhận"
            });
        }
    }

    /** Kiểm tra trạng thái đơn có khớp bộ lọc (combobox dùng "CONFIRM" cho "CONFIRMED"). */
    private boolean matchesOrderFilter(String status, String filter) {
        if (ALL.equals(filter)) {
            return true;
        }
        if ("CONFIRM".equalsIgnoreCase(filter)) {
            return "CONFIRMED".equalsIgnoreCase(status);
        }
        return filter.equalsIgnoreCase(status);
    }

    // ==================== HÀM PHỤ TRỢ ====================

    
    private int getSelectedId(int row, JTable table) {
        return Integer.parseInt(table.getValueAt(row, 0).toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        LamMoi2 = new javax.swing.JButton();
        MoKhoa = new javax.swing.JButton();
        KhoaTK = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        Tatcadonhang = new javax.swing.JComboBox<>();
        Lammoi3 = new javax.swing.JButton();
        Xemchitiet2 = new javax.swing.JButton();
        Kiemtra = new javax.swing.JButton();
        BanHang = new javax.swing.JButton();
        Thatbai = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnHuyDonQuaHan = new javax.swing.JButton();
        DuyetThanhToan = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableDuyetThanhToan = new javax.swing.JTable();
        Lammoi4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        ThanhTim1 = new javax.swing.JComboBox<>();
        LamMoi = new javax.swing.JButton();
        Xemchitiet = new javax.swing.JButton();
        Duyet = new javax.swing.JButton();
        Tuchoi = new javax.swing.JButton();
        Xóa = new javax.swing.JButton();
        DangXuat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setBackground(new java.awt.Color(236, 244, 251));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("QUẢN TRỊ VIÊN");
        jLabel1.setMaximumSize(new java.awt.Dimension(121, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(121, 20));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel8.setBackground(new java.awt.Color(236, 244, 251));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Username", "Họ tên", "SDT", "Vai trò", "Trạng thái"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        LamMoi2.setBackground(new java.awt.Color(51, 51, 255));
        LamMoi2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LamMoi2.setForeground(new java.awt.Color(255, 255, 255));
        LamMoi2.setText("Làm mới");
        LamMoi2.addActionListener(this::LamMoi2ActionPerformed);

        MoKhoa.setBackground(new java.awt.Color(0, 255, 0));
        MoKhoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MoKhoa.setForeground(new java.awt.Color(255, 255, 255));
        MoKhoa.setText("Mở khóa");
        MoKhoa.addActionListener(this::MoKhoaActionPerformed);

        KhoaTK.setBackground(new java.awt.Color(255, 0, 0));
        KhoaTK.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        KhoaTK.setForeground(new java.awt.Color(255, 255, 255));
        KhoaTK.setText("Khóa tài khoản");
        KhoaTK.addActionListener(this::KhoaTKActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(623, Short.MAX_VALUE)
                .addComponent(LamMoi2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
            .addComponent(MoKhoa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(KhoaTK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(LamMoi2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(MoKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(KhoaTK, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(jPanel8, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Quản lý user", jPanel2);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel9.setBackground(new java.awt.Color(236, 244, 251));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID đơn", "Sản phẩm", "Người bán", "Người mua", "Giá", "Trạng thái", "Ngày xác nhận"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Lọc trạng thái:");

        Tatcadonhang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Tatcadonhang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "CONFIRM", "CHECKING", "CHECKED", "SOLD", "CANCELED", "FAILED" }));
        Tatcadonhang.addActionListener(this::TatcadonhangActionPerformed);

        Lammoi3.setBackground(new java.awt.Color(51, 51, 255));
        Lammoi3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Lammoi3.setForeground(new java.awt.Color(255, 255, 255));
        Lammoi3.setText("Làm mới");
        Lammoi3.addActionListener(this::Lammoi3ActionPerformed);

        Xemchitiet2.setBackground(new java.awt.Color(51, 51, 255));
        Xemchitiet2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xemchitiet2.setForeground(new java.awt.Color(255, 255, 255));
        Xemchitiet2.setText("Chi tiết");
        Xemchitiet2.addActionListener(this::Xemchitiet2ActionPerformed);

        Kiemtra.setBackground(new java.awt.Color(51, 51, 255));
        Kiemtra.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Kiemtra.setForeground(new java.awt.Color(255, 255, 255));
        Kiemtra.setText("Bước tiếp theo");
        Kiemtra.addActionListener(this::KiemtraActionPerformed);

        BanHang.setBackground(new java.awt.Color(0, 255, 0));
        BanHang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        BanHang.setForeground(new java.awt.Color(255, 255, 255));
        BanHang.setText("Đã bán ");
        BanHang.addActionListener(this::BanHangActionPerformed);

        Thatbai.setBackground(new java.awt.Color(255, 0, 0));
        Thatbai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Thatbai.setForeground(new java.awt.Color(255, 255, 255));
        Thatbai.setText("Thất bại");
        Thatbai.addActionListener(this::ThatbaiActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel4)
                        .addGap(32, 32, 32)
                        .addComponent(Tatcadonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lammoi3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(Xemchitiet2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)
                        .addComponent(Kiemtra, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71)
                        .addComponent(BanHang, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addComponent(Thatbai, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Tatcadonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lammoi3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BanHang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Xemchitiet2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Kiemtra, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Thatbai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        jPanel4.add(jPanel9);

        jTabbedPane1.addTab("Quản lý đơn hàng", jPanel4);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBackground(new java.awt.Color(236, 244, 251));

        btnHuyDonQuaHan.setBackground(new java.awt.Color(255, 102, 0));
        btnHuyDonQuaHan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHuyDonQuaHan.setText("Hủy đơn quá hạn");
        btnHuyDonQuaHan.addActionListener(this::btnHuyDonQuaHanActionPerformed);

        DuyetThanhToan.setBackground(new java.awt.Color(0, 102, 102));
        DuyetThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        DuyetThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        DuyetThanhToan.setText("Duyệt thanh toán");
        DuyetThanhToan.addActionListener(this::DuyetThanhToanActionPerformed);

        jTableDuyetThanhToan.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTableDuyetThanhToan);

        Lammoi4.setBackground(new java.awt.Color(51, 51, 255));
        Lammoi4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Lammoi4.setForeground(new java.awt.Color(255, 255, 255));
        Lammoi4.setText("Làm mới");
        Lammoi4.addActionListener(this::Lammoi4ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lammoi4, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(DuyetThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHuyDonQuaHan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(Lammoi4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DuyetThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnHuyDonQuaHan, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel5.add(jPanel3);

        jTabbedPane1.addTab("Duyệt thanh toán", jPanel5);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setBackground(new java.awt.Color(236, 244, 251));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên máy", "Người dùng", "Trạng thái", "Ngày"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Lọc trạng thái:");

        ThanhTim1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThanhTim1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "PENDING", "APPROVED", "REJECTED", "SOLD" }));
        ThanhTim1.setToolTipText("");
        ThanhTim1.addActionListener(this::ThanhTim1ActionPerformed);

        LamMoi.setBackground(new java.awt.Color(0, 0, 255));
        LamMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LamMoi.setForeground(new java.awt.Color(255, 255, 255));
        LamMoi.setText("Làm mới");
        LamMoi.addActionListener(this::LamMoiActionPerformed);

        Xemchitiet.setBackground(new java.awt.Color(204, 255, 255));
        Xemchitiet.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xemchitiet.setText("Chi tiết");
        Xemchitiet.addActionListener(this::XemchitietActionPerformed);

        Duyet.setBackground(new java.awt.Color(0, 102, 102));
        Duyet.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Duyet.setForeground(new java.awt.Color(255, 255, 255));
        Duyet.setText("Duyệt");
        Duyet.addActionListener(this::DuyetActionPerformed);

        Tuchoi.setBackground(new java.awt.Color(255, 153, 0));
        Tuchoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Tuchoi.setForeground(new java.awt.Color(255, 255, 255));
        Tuchoi.setText("Từ chối");
        Tuchoi.addActionListener(this::TuchoiActionPerformed);

        Xóa.setBackground(new java.awt.Color(255, 0, 51));
        Xóa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xóa.setForeground(new java.awt.Color(255, 255, 255));
        Xóa.setText("Xóa");
        Xóa.addActionListener(this::XóaActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Xemchitiet, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(Duyet, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(Tuchoi, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(Xóa, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ThanhTim1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(LamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))
                    .addComponent(ThanhTim1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(Xemchitiet, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(Tuchoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Xóa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Duyet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel7);

        jTabbedPane1.addTab("Quản lý tin", jPanel1);

        DangXuat.setBackground(new java.awt.Color(0, 0, 0));
        DangXuat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        DangXuat.setForeground(new java.awt.Color(255, 255, 255));
        DangXuat.setText("Đăng xuất");
        DangXuat.addActionListener(this::DangXuatActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(331, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(203, 203, 203)
                .addComponent(DangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DangXuat)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel6);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DangXuatActionPerformed
        // TODO add your handling code here:
        // Xác nhận đăng xuất
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );

        // Nếu chọn Không thì không làm gì
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Mở giao diện đăng nhập
        GiaoDienDangNhap login = new GiaoDienDangNhap();
        login.setLocationRelativeTo(null);
        login.setVisible(true);

        // Đóng giao diện Admin
        this.dispose();
    }//GEN-LAST:event_DangXuatActionPerformed

    private void XemchitietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XemchitietActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
            return;
        }

        int id = getSelectedId(row, jTable1);
        Phone phone = phoneDAO.findById(id);

        if (phone == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            return;
        }

        ChiTietSP dialog = new ChiTietSP(this, true, phone, currentUser);
        dialog.hideBuyButton();
        dialog.setVisible(true);
    }//GEN-LAST:event_XemchitietActionPerformed

    private void XóaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XóaActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn dòng cần xóa!");
            return;
        }

        int id = getSelectedId(row, jTable1);

        String status = jTable1.getValueAt(row, 3).toString();

        if ("SOLD".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                this,
                "Điện thoại đã bán, không thể thực hiện thao tác này!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa tin ID: " + id + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (phoneDAO.delete(id)) {
                loadDataToTable1(
                    (String) ThanhTim1.getSelectedItem());

                JOptionPane.showMessageDialog(
                    this, "Đã xóa tin thành công!");
            } else {
                JOptionPane.showMessageDialog(
                    this, "Xóa tin thất bại!");
            }
        }
    }//GEN-LAST:event_XóaActionPerformed

    private void TuchoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TuchoiActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn dòng cần từ chối!");
            return;
        }

        int id = getSelectedId(row, jTable1);

        String status = jTable1.getValueAt(row, 3).toString();

        if ("SOLD".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                this,
                "Điện thoại đã bán, không thể thực hiện thao tác này!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (phoneDAO.updateStatus(id, "REJECTED")) {
            loadDataToTable1(
                (String) ThanhTim1.getSelectedItem());

            JOptionPane.showMessageDialog(
                this, "Đã từ chối tin ID: " + id);
        } else {
            JOptionPane.showMessageDialog(
                this, "Từ chối tin thất bại!");
        }
    }//GEN-LAST:event_TuchoiActionPerformed

    private void DuyetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DuyetActionPerformed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn dòng cần duyệt!");
            return;
        }

        int id = getSelectedId(row, jTable1);
        String status = jTable1.getValueAt(row, 3).toString();

        if ("SOLD".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                this,
                "Điện thoại đã bán, không thể thực hiện thao tác này!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (phoneDAO.updateStatus(id, "APPROVED")) {
            loadDataToTable1(
                (String) ThanhTim1.getSelectedItem());

            JOptionPane.showMessageDialog(
                this, "Đã duyệt tin ID: " + id);
        } else {
            JOptionPane.showMessageDialog(
                this, "Duyệt tin thất bại!");
        }
    }//GEN-LAST:event_DuyetActionPerformed

    private void LamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamMoiActionPerformed
        // TODO add your handling code here:
        ThanhTim1.setSelectedIndex(0);
        loadDataToTable1("Tất cả");
        JOptionPane.showMessageDialog(this, "Đã làm mới danh sách quản lý tin!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_LamMoiActionPerformed

    private void ThanhTim1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThanhTim1ActionPerformed
        // TODO add your handling code here:
        String selectedStatus = (String) ThanhTim1.getSelectedItem();
        loadDataToTable1(selectedStatus);
    }//GEN-LAST:event_ThanhTim1ActionPerformed

    private void Lammoi4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lammoi4ActionPerformed
        // TODO add your handling code here:
        loadDonChoThanhToan();

        JOptionPane.showMessageDialog(
            this,
            "Đã làm mới danh sách thanh toán!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_Lammoi4ActionPerformed

    private void DuyetThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DuyetThanhToanActionPerformed
        int selectedRow = jTableDuyetThanhToan.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng cần duyệt thanh toán!");
            return;
        }

        int modelRow = jTableDuyetThanhToan.convertRowIndexToModel(selectedRow);
        int orderId = Integer.parseInt(jTableDuyetThanhToan.getModel().getValueAt(modelRow, 0).toString());
        String soTien = jTableDuyetThanhToan.getModel().getValueAt(modelRow, 5).toString();

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Xác nhận đã nhận đủ " + soTien + " cho đơn hàng này?",
            "Duyệt thanh toán", javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            
            if (orderDAO.updateStatus(orderId, "CONFIRMED")) {
                javax.swing.JOptionPane.showMessageDialog(this, "Đã duyệt thanh toán! Đơn hàng chuyển sang CONFIRMED.");
                loadDonChoThanhToan(); // Làm mới bảng Admin
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái đơn hàng!");
            }
        }
    }//GEN-LAST:event_DuyetThanhToanActionPerformed

    private void btnHuyDonQuaHanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyDonQuaHanActionPerformed
        int selectedRow = jTableDuyetThanhToan.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this,
            "Vui lòng chọn một đơn hàng quá hạn cần huỷ!",
            "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int modelRow = jTableDuyetThanhToan.convertRowIndexToModel(selectedRow);
    int orderId = Integer.parseInt(jTableDuyetThanhToan.getModel().getValueAt(modelRow, 0).toString());
    String tenMay = jTableDuyetThanhToan.getModel().getValueAt(modelRow, 1).toString();
    String nguoiMua = jTableDuyetThanhToan.getModel().getValueAt(modelRow, 2).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Xác nhận huỷ đơn hàng [" + tenMay + "] của người mua [" + nguoiMua + "] do quá hạn thanh toán?\n"
        + "Sản phẩm sẽ được mở bán lại trên hệ thống.",
        "Huỷ đơn quá hạn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    if (orderDAO.cancelOverdueAndRestorePhone(orderId)) {
        JOptionPane.showMessageDialog(this,
            "Đã huỷ đơn quá hạn và mở bán lại [" + tenMay + "] thành công!",
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDonChoThanhToan();
    } else {
        JOptionPane.showMessageDialog(this,
            "Huỷ đơn thất bại! Đơn có thể không còn ở trạng thái chờ thanh toán.",
            "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnHuyDonQuaHanActionPerformed

    private void KiemtraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KiemtraActionPerformed
        // TODO add your handling code here:
        int row = jTable3.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!");
            return;
        }

        int id = getSelectedId(row, jTable3);
        String status = jTable3.getValueAt(row, 5).toString();

        String newStatus;

        if (status.equalsIgnoreCase("CONFIRMED")) {
            newStatus = "CHECKING";
        } else if (status.equalsIgnoreCase("CHECKING")) {
            newStatus = "CHECKED";
        } else {
            JOptionPane.showMessageDialog(this,
                "Đơn hàng không thể chuyển sang bước tiếp theo!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn chuyển trạng thái sang " + newStatus + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (orderDAO.updateStatus(id, newStatus)) {
            loadDataToTable3((String) Tatcadonhang.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }//GEN-LAST:event_KiemtraActionPerformed

    private void BanHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BanHangActionPerformed
        // TODO add your handling code here:
        int row = jTable3.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!");
            return;
        }

        int id = getSelectedId(row, jTable3);
        String status = jTable3.getValueAt(row, 5).toString();

        if (status.equalsIgnoreCase("SOLD")
            || status.equalsIgnoreCase("CANCELED")
            || status.equalsIgnoreCase("FAILED")) {

            JOptionPane.showMessageDialog(this,
                "Đơn hàng không thể chuyển sang trạng thái đã bán!");
            return;
        }

        if (!status.equalsIgnoreCase("CHECKED")) {
            JOptionPane.showMessageDialog(this,
                "Chỉ có thể xác nhận bán khi đơn hàng đã kiểm tra xong!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn chuyển đơn hàng thành ĐÃ BÁN?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (orderDAO.updateStatus(id, "SOLD")) {
            loadDataToTable3((String) Tatcadonhang.getSelectedItem());

            JOptionPane.showMessageDialog(this,
                "Đã cập nhật đơn hàng thành ĐÃ BÁN!");
        } else {
            JOptionPane.showMessageDialog(this,
                "Cập nhật đơn hàng thất bại!");
        }
    }//GEN-LAST:event_BanHangActionPerformed

    private void Xemchitiet2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Xemchitiet2ActionPerformed
        // TODO add your handling code here:
        // Lấy dòng đang chọn
        int row = jTable3.getSelectedRow();

        // Kiểm tra đã chọn đơn hàng chưa
        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn đơn hàng cần xem chi tiết!"
            );
            return;
        }

        // Lấy ID đơn hàng ở cột đầu tiên
        int orderId = getSelectedId(row, jTable3);

        // Mở cửa sổ chi tiết đơn hàng
        ChiTietDonHang dialog = new ChiTietDonHang(this, true, orderId);
        dialog.setVisible(true);
    }//GEN-LAST:event_Xemchitiet2ActionPerformed

    private void ThatbaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThatbaiActionPerformed
        // TODO add your handling code here:
        int row = jTable3.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn đơn hàng!");
            return;
        }

        int id = getSelectedId(row, jTable3);
        String status = jTable3.getValueAt(row, 5).toString();

        if (!status.equalsIgnoreCase("CONFIRMED")
            && !status.equalsIgnoreCase("CHECKING")
            && !status.equalsIgnoreCase("CHECKED")) {

            JOptionPane.showMessageDialog(this,
                "Trạng thái đơn hàng không thể chuyển thành thất bại!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn chuyển đơn hàng thành thất bại?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (orderDAO.updateStatus(id, "FAILED")) {
            loadDataToTable3(
                (String) Tatcadonhang.getSelectedItem());

            JOptionPane.showMessageDialog(this,
                "Cập nhật đơn hàng thành thất bại thành công!");
        } else {
            JOptionPane.showMessageDialog(this,
                "Cập nhật thất bại!");
        }
    }//GEN-LAST:event_ThatbaiActionPerformed

    private void Lammoi3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lammoi3ActionPerformed
        // TODO add your handling code here:
        Tatcadonhang.setSelectedIndex(0);
        loadDataToTable3("Tất cả");

        JOptionPane.showMessageDialog(
            this, "Đã làm mới danh sách đơn hàng!");
    }//GEN-LAST:event_Lammoi3ActionPerformed

    private void TatcadonhangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TatcadonhangActionPerformed
        // TODO add your handling code here:
        String status = (String) Tatcadonhang.getSelectedItem();
        loadDataToTable3(status);
    }//GEN-LAST:event_TatcadonhangActionPerformed

    private void MoKhoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoKhoaActionPerformed
        // TODO add your handling code here:
        int row = jTable2.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn tài khoản cần mở khóa!");
            return;
        }

        int id = getSelectedId(row, jTable2);

        if (userDAO.updateStatus(id, "ACTIVE")) {
            loadDataToTable2();

            JOptionPane.showMessageDialog(
                this, "Đã mở khóa tài khoản ID: " + id);
        } else {
            JOptionPane.showMessageDialog(
                this, "Mở khóa tài khoản thất bại!");
        }
    }//GEN-LAST:event_MoKhoaActionPerformed

    private void KhoaTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KhoaTKActionPerformed
        // TODO add your handling code here:
        int row = jTable2.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn tài khoản cần khóa!");
            return;
        }

        int id = getSelectedId(row, jTable2);

        User user = userDAO.findById(id);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản!");
            return;
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            JOptionPane.showMessageDialog(
                this,
                "Không thể khóa tài khoản ADMIN!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn khóa tài khoản ID: " + id + "?",
            "Xác nhận khóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.updateStatus(id, "LOCKED")) {
                loadDataToTable2();

                JOptionPane.showMessageDialog(
                    this, "Đã khóa tài khoản ID: " + id);
            } else {
                JOptionPane.showMessageDialog(
                    this, "Khóa tài khoản thất bại!");
            }
        }
    }//GEN-LAST:event_KhoaTKActionPerformed

    private void LamMoi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamMoi2ActionPerformed
        // TODO add your handling code here:
        loadDataToTable2();
        JOptionPane.showMessageDialog(this, "Đã làm mới danh sách tài khoản!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_LamMoi2ActionPerformed
    
    private void loadDonChoThanhToan() {
         String[] columnNames = {"ID đơn", "Tên máy", "Người mua", "Người bán",
                            "Giá máy", "Phí 1% cần nhận", "Ngày xác nhận"};
    ReadOnlyTableModel model = new ReadOnlyTableModel(columnNames);
    jTableDuyetThanhToan.setModel(model);

    // Ẩn cột ID đơn (cột 0)
    jTableDuyetThanhToan.getColumnModel().getColumn(0).setMinWidth(0);
    jTableDuyetThanhToan.getColumnModel().getColumn(0).setMaxWidth(0);
    jTableDuyetThanhToan.getColumnModel().getColumn(0).setWidth(0);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    for (Order o : orderDAO.findByStatus(OrderDAO.WAITING_PAYMENT)) {
        Phone phone = phoneDAO.findById(o.getPhoneId());
        User buyer = userDAO.findById(o.getBuyerId());
        User seller = userDAO.findById(o.getSellerId());

        double phiDichVu = o.getPrice() * 0.01;

        model.addRow(new Object[]{
            o.getId(),
            phone != null ? phone.getName() : UNKNOWN,
            buyer != null ? buyer.getUsername() : UNKNOWN,
            seller != null ? seller.getUsername() : UNKNOWN,
            String.format("%,.0f", o.getPrice()),
            String.format("%,.0f VNĐ", phiDichVu),
            o.getConfirmedAt() != null ? sdf.format(o.getConfirmedAt()) : ""
        });
    }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            AdminFrame frame = new AdminFrame();
            frame.setLocationRelativeTo(null); // Hiện chính giữa màn hình
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BanHang;
    private javax.swing.JButton DangXuat;
    private javax.swing.JButton Duyet;
    private javax.swing.JButton DuyetThanhToan;
    private javax.swing.JButton KhoaTK;
    private javax.swing.JButton Kiemtra;
    private javax.swing.JButton LamMoi;
    private javax.swing.JButton LamMoi2;
    private javax.swing.JButton Lammoi3;
    private javax.swing.JButton Lammoi4;
    private javax.swing.JButton MoKhoa;
    private javax.swing.JComboBox<String> Tatcadonhang;
    private javax.swing.JComboBox<String> ThanhTim1;
    private javax.swing.JButton Thatbai;
    private javax.swing.JButton Tuchoi;
    private javax.swing.JButton Xemchitiet;
    private javax.swing.JButton Xemchitiet2;
    private javax.swing.JButton Xóa;
    private javax.swing.JButton btnHuyDonQuaHan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTableDuyetThanhToan;
    // End of variables declaration//GEN-END:variables
}