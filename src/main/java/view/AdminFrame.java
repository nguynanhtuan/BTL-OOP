/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import dao.PhoneDAO;
import dao.UserDAO;
import dao.OrderDAO;
import model.Phone;
import model.User;
import model.Order;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.text.DecimalFormat;


/**
 *
 * @author Admin
 */
public class AdminFrame extends javax.swing.JFrame {
    // Khai báo biến để lưu thông tin người dùng đang đăng nhập nếu cần dùng sau này
    private model.User currentUser;

    // Hàm khởi tạo nhận đối tượng User theo đúng quy chuẩn của nhóm
    public AdminFrame(model.User user) {
        initComponents();
        this.currentUser = user;
        
        khoaSuaBang();
        
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jTable2.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jTable3.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        loadDataToTable1("Tất cả");
        loadDataToTable2();
        loadDataToTable3("Tất cả");
        // (Tùy chọn) Bạn có thể hiển thị tên Admin lên tiêu đề form hoặc một Label nào đó:
        // this.setTitle("Trang Quản Trị - Xin chào: " + user.getFullName());
    }
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminFrame.class.getName());
    private final PhoneDAO phoneDAO = new PhoneDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    
    private List<Order> getAllOrders() {
        Map<Integer, Order> orders = new LinkedHashMap<>();

        List<User> users = userDAO.findAll();

        for (User user : users) {
            // Lấy đơn hàng người dùng mua
            for (Order order : orderDAO.findByBuyer(user.getId())) {
                orders.put(order.getId(), order);
            }

            // Lấy đơn hàng người dùng bán
            for (Order order : orderDAO.findBySeller(user.getId())) {
                orders.put(order.getId(), order);
            }
        }

        List<Order> list = new ArrayList<>(orders.values());

        // Sắp xếp ID đơn hàng giảm dần
        list.sort(Comparator.comparingInt(Order::getId).reversed());

        return list;
    }
    /**
     * Creates new form AdminFrame
     */
    public AdminFrame() {
        initComponents();
        khoaSuaBang();
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jTable2.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jTable3.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        loadDataToTable1("Tất cả");
        loadDataToTable2();
        loadDataToTable3("Tất cả");
    }

    
    // Hiển thị danh sách tin đăng trên jTable1
    private void loadDataToTable1(String statusFilter) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);

        List<Phone> list;

        if ("Tất cả".equals(statusFilter)) {
            list = phoneDAO.findAll();
        } else {
            list = phoneDAO.findByStatus(statusFilter);
        }

        // Định dạng ngày/tháng/năm
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Phone p : list) {
            User u = userDAO.findById(p.getUserId());

            String tenNguoiDang = (u != null)
                    ? u.getUsername()
                    : "Không xác định";

            // Lấy ngày đăng từ Phone
            String ngayDang = "";

            if (p.getCreatedAt() != null) {
                ngayDang = sdf.format(p.getCreatedAt());
            }

            Object[] row = {
                p.getId(),
                p.getName(),
                tenNguoiDang,
                p.getStatus(),
                ngayDang
            };

            model.addRow(row);
        }
    }

    // Hiển thị danh sách tài khoản trên jTable2
    private void loadDataToTable2() {
        DefaultTableModel model =
                (DefaultTableModel) jTable2.getModel();

        model.setRowCount(0);

        List<User> list = userDAO.findAll();

        for (User u : list) {
            Object[] row = {
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getPhoneNumber(),
                u.getRole(),
                u.getStatus()
            };

            model.addRow(row);
        }
    }
    private void loadDataToTable3(String statusFilter) {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);

        DecimalFormat df = new DecimalFormat("#,##0");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        List<Order> list = getAllOrders();

        for (Order order : list) {
            String status = order.getStatus();

            // Lọc trạng thái
            if (!"Tất cả".equals(statusFilter)) {
                boolean khop =
                        statusFilter.equalsIgnoreCase(status)
                        || (statusFilter.equalsIgnoreCase("CONFIRM")
                        && status.equalsIgnoreCase("CONFIRMED"));

                if (!khop) {
                    continue;
                }
            }

            // Lấy thông tin sản phẩm và người dùng
            Phone phone = phoneDAO.findById(order.getPhoneId());
            User seller = userDAO.findById(order.getSellerId());
            User buyer = userDAO.findById(order.getBuyerId());

            String tenSanPham = phone != null
                    ? phone.getName() : "Không xác định";

            String nguoiBan = seller != null
                    ? seller.getUsername() : "Không xác định";

            String nguoiMua = buyer != null
                    ? buyer.getUsername() : "Không xác định";

            String ngayXacNhan = order.getConfirmedAt() != null
                    ? sdf.format(order.getConfirmedAt())
                    : "Chưa xác nhận";

            Object[] row = {
                order.getId(),
                tenSanPham,
                nguoiBan,
                nguoiMua,
                df.format(order.getPrice()) + " VNĐ",
                status,
                ngayXacNhan
            };

            model.addRow(row);
        }
    }
    
    private void khoaSuaBang() {
        DefaultTableModel model1 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel old1 =
                (DefaultTableModel) jTable1.getModel();

        for (int i = 0; i < old1.getColumnCount(); i++) {
            model1.addColumn(old1.getColumnName(i));
        }

        jTable1.setModel(model1);

        DefaultTableModel model2 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel old2 =
                (DefaultTableModel) jTable2.getModel();

        for (int i = 0; i < old2.getColumnCount(); i++) {
            model2.addColumn(old2.getColumnName(i));
        }

        jTable2.setModel(model2);
        DefaultTableModel model3 = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "ID đơn", "Sản phẩm", "Người bán",
                "Người mua", "Giá", "Trạng thái", "Ngày xác nhận"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable3.setModel(model3);
    }
    
    // Hàm phụ trợ lấy ID của dòng đang chọn trên JTable
    private int getSelectedId(int row, javax.swing.JTable table) {
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ThanhTim1 = new javax.swing.JComboBox<>();
        LamMoi = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Duyet = new javax.swing.JButton();
        Tuchoi = new javax.swing.JButton();
        Xóa = new javax.swing.JButton();
        Xemchitiet = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        LamMoi2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        KhoaTK = new javax.swing.JButton();
        MoKhoa = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Tatcadonhang = new javax.swing.JComboBox<>();
        Lammoi3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        Thatbai = new javax.swing.JButton();
        Xemchitiet2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        DangXuat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

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

        Duyet.setBackground(new java.awt.Color(51, 255, 51));
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

        Xemchitiet.setBackground(new java.awt.Color(0, 51, 255));
        Xemchitiet.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xemchitiet.setForeground(new java.awt.Color(255, 255, 255));
        Xemchitiet.setText("Xem chi tiết ");
        Xemchitiet.addActionListener(this::XemchitietActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Xemchitiet)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Duyet)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Tuchoi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Xóa))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ThanhTim1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(ThanhTim1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(LamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Xóa)
                    .addComponent(Tuchoi)
                    .addComponent(Duyet)
                    .addComponent(Xemchitiet)))
        );

        jTabbedPane1.addTab("Quản lý tin", jPanel1);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Danh sách tài khoản");

        LamMoi2.setBackground(new java.awt.Color(51, 51, 255));
        LamMoi2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LamMoi2.setForeground(new java.awt.Color(255, 255, 255));
        LamMoi2.setText("Làm mới");
        LamMoi2.addActionListener(this::LamMoi2ActionPerformed);

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

        KhoaTK.setBackground(new java.awt.Color(255, 0, 0));
        KhoaTK.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        KhoaTK.setForeground(new java.awt.Color(255, 255, 255));
        KhoaTK.setText("Khóa tài khoản");
        KhoaTK.addActionListener(this::KhoaTKActionPerformed);

        MoKhoa.setBackground(new java.awt.Color(0, 255, 0));
        MoKhoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        MoKhoa.setForeground(new java.awt.Color(255, 255, 255));
        MoKhoa.setText("Mở khóa");
        MoKhoa.addActionListener(this::MoKhoaActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LamMoi2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(MoKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(KhoaTK)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LamMoi2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MoKhoa, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(KhoaTK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Quản lý user", jPanel2);

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

        Thatbai.setBackground(new java.awt.Color(255, 0, 0));
        Thatbai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Thatbai.setForeground(new java.awt.Color(255, 255, 255));
        Thatbai.setText("Thất bại");
        Thatbai.addActionListener(this::ThatbaiActionPerformed);

        Xemchitiet2.setBackground(new java.awt.Color(51, 51, 255));
        Xemchitiet2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Xemchitiet2.setForeground(new java.awt.Color(255, 255, 255));
        Xemchitiet2.setText("Xem chi tiết");
        Xemchitiet2.addActionListener(this::Xemchitiet2ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(Tatcadonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Lammoi3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(Xemchitiet2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Thatbai, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(Tatcadonhang)
                    .addComponent(Lammoi3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Xemchitiet2)
                    .addComponent(Thatbai)))
        );

        jTabbedPane1.addTab("Quản lý đơn hàng", jPanel4);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Xin chào: Quản trị viên.");
        jLabel1.setMaximumSize(new java.awt.Dimension(121, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(121, 20));
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        DangXuat.setBackground(new java.awt.Color(0, 0, 0));
        DangXuat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        DangXuat.setForeground(new java.awt.Color(255, 255, 255));
        DangXuat.setText("Đăng xuất");
        DangXuat.addActionListener(this::DangXuatActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 506, Short.MAX_VALUE)
                .addComponent(DangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 7, Short.MAX_VALUE)
                .addComponent(DangXuat))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ThanhTim1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThanhTim1ActionPerformed
        // TODO add your handling code here:
        String selectedStatus = (String) ThanhTim1.getSelectedItem();
        loadDataToTable1(selectedStatus);
    }//GEN-LAST:event_ThanhTim1ActionPerformed

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

    private void LamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamMoiActionPerformed
        // TODO add your handling code here:
        ThanhTim1.setSelectedIndex(0);
        loadDataToTable1("Tất cả");
        JOptionPane.showMessageDialog(this, "Đã làm mới danh sách quản lý tin!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_LamMoiActionPerformed

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

    private void LamMoi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamMoi2ActionPerformed
        // TODO add your handling code here:
        loadDataToTable2();
        JOptionPane.showMessageDialog(this, "Đã làm mới danh sách tài khoản!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_LamMoi2ActionPerformed

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

    private void TatcadonhangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TatcadonhangActionPerformed
        // TODO add your handling code here:
        String status = (String) Tatcadonhang.getSelectedItem();
        loadDataToTable3(status);
    }//GEN-LAST:event_TatcadonhangActionPerformed

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

    private void Lammoi3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lammoi3ActionPerformed
        // TODO add your handling code here:
        Tatcadonhang.setSelectedIndex(0);
        loadDataToTable3("Tất cả");

        JOptionPane.showMessageDialog(
                this, "Đã làm mới danh sách đơn hàng!");
    }//GEN-LAST:event_Lammoi3ActionPerformed

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

        if (status.equalsIgnoreCase("SOLD")) {
            JOptionPane.showMessageDialog(this,
                    "Đơn hàng đã bán, không thể chuyển thành thất bại!");
            return;
        }

        if (status.equalsIgnoreCase("FAILED")) {
            JOptionPane.showMessageDialog(this,
                    "Đơn hàng này đã thất bại!");
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
    private javax.swing.JButton DangXuat;
    private javax.swing.JButton Duyet;
    private javax.swing.JButton KhoaTK;
    private javax.swing.JButton LamMoi;
    private javax.swing.JButton LamMoi2;
    private javax.swing.JButton Lammoi3;
    private javax.swing.JButton MoKhoa;
    private javax.swing.JComboBox<String> Tatcadonhang;
    private javax.swing.JComboBox<String> ThanhTim1;
    private javax.swing.JButton Thatbai;
    private javax.swing.JButton Tuchoi;
    private javax.swing.JButton Xemchitiet;
    private javax.swing.JButton Xemchitiet2;
    private javax.swing.JButton Xóa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    // End of variables declaration//GEN-END:variables
}