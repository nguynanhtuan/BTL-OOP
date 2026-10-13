
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Admin extends JFrame {
    private JTable tableTin, tableUser;
    private DefaultTableModel modelTin, modelUser;
    private JComboBox<String> cbStatus;

    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color RED = new Color(220, 38, 38);
    private final Color ORANGE = new Color(234, 88, 12);

    public Admin() {
        setTitle("Admin Dashboard");
        setSize(1050, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(30, 41, 59));

        JLabel greeting = new JLabel("Xin chào, Quản trị viên");
        greeting.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(greeting, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));
        tabs.addTab("  Quản lý tin  ", createTinPanel());
        tabs.addTab("  Quản lý User  ", createUserPanel());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);

        loadTin();
        loadUser();
    }

    private JPanel createTinPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(new JLabel("Lọc trạng thái:"));

        cbStatus = new JComboBox<>(new String[]{
            "Tất cả", "PENDING", "APPROVED", "REJECTED"
        });
        top.add(cbStatus);

        JButton btnRefresh = button("Làm mới", BLUE);
        top.add(btnRefresh);

        modelTin = new DefaultTableModel(
            new String[]{"ID", "Tên máy", "Người đăng",
                        "Trạng thái", "Ngày đăng"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableTin = new JTable(modelTin);
        styleTable(tableTin);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);

        JButton btnDuyet = button("Duyệt", GREEN);
        JButton btnTuChoi = button("Từ chối", ORANGE);
        JButton btnXoa = button("Xóa", RED);

        actions.add(btnDuyet);
        actions.add(btnTuChoi);
        actions.add(btnXoa);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableTin), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        cbStatus.addActionListener(e -> loadTin());
        btnRefresh.addActionListener(e -> loadTin());

        btnDuyet.addActionListener(e ->
            updateTin("APPROVED")
        );

        btnTuChoi.addActionListener(e ->
            updateTin("REJECTED")
        );

        btnXoa.addActionListener(e -> deleteTin());

        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel subtitle = new JLabel("Danh sách tài khoản");
        subtitle.setFont(new Font("Arial", Font.BOLD, 18));

        modelUser = new DefaultTableModel(
            new String[]{"ID", "Username", "Họ tên",
                         "SĐT", "Vai trò", "Trạng thái"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableUser = new JTable(modelUser);
        styleTable(tableUser);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.add(subtitle, BorderLayout.WEST);

        JButton btnRefresh = button("Làm mới", BLUE);
        top.add(btnRefresh, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Color.WHITE);

        JButton btnKhoa = button("Khóa tài khoản", RED);
        JButton btnMoKhoa = button("Mở khóa", GREEN);

        actions.add(btnKhoa);
        actions.add(btnMoKhoa);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableUser), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadUser());
        btnKhoa.addActionListener(e -> updateUser("LOCKED"));
        btnMoKhoa.addActionListener(e -> updateUser("ACTIVE"));

        return panel;
    }

    private JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(
            new Color(226, 232, 240)
        );
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
    }

    private void loadTin() {
        if (modelTin == null) return;

        modelTin.setRowCount(0);
        String status = cbStatus.getSelectedItem().toString();

        String sql = "SELECT id, ten_may, nguoi_dang, " +
                     "trang_thai, ngay_dang FROM tin_dang";

        if (!status.equals("Tất cả")) {
            sql += " WHERE trang_thai = ?";
        }
        sql += " ORDER BY id DESC";

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!status.equals("Tất cả")) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelTin.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ten_may"),
                        rs.getString("nguoi_dang"),
                        rs.getString("trang_thai"),
                        rs.getDate("ngay_dang")
                    });
                }
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadUser() {
        if (modelUser == null) return;

        modelUser.setRowCount(0);

        String sql = "SELECT id, username, full_name, phone, " +
                     "role, status FROM users ORDER BY id";

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelUser.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("phone"),
                    rs.getString("role"),
                    rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int selectedId(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn một dòng trong bảng!"
            );
            return -1;
        }

        // Hỗ trợ đúng khi bảng được sắp xếp
        row = table.convertRowIndexToModel(row);
        return Integer.parseInt(
            table.getModel().getValueAt(row, 0).toString()
        );
    }

    private void updateTin(String status) {
        int id = selectedId(tableTin);
        if (id == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận chuyển tin sang " + status + "?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE tin_dang SET trang_thai = ? WHERE id = ?";

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

            loadTin();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteTin() {
        int id = selectedId(tableTin);
        if (id == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn xóa tin này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM tin_dang WHERE id = ?";

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            loadTin();
            JOptionPane.showMessageDialog(this, "Đã xóa tin!");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateUser(String status) {
        int id = selectedId(tableUser);
        if (id == -1) return;

        int row = tableUser.convertRowIndexToModel(
            tableUser.getSelectedRow()
        );

        String role = modelUser.getValueAt(row, 4).toString();
        String currentStatus =
            modelUser.getValueAt(row, 5).toString();

        if (role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(
                this, "Không thể khóa hoặc mở khóa ADMIN tại đây!"
            );
            return;
        }

        if (currentStatus.equals(status)) {
            JOptionPane.showMessageDialog(
                this, "Tài khoản đã ở trạng thái này!"
            );
            return;
        }

        String action = status.equals("LOCKED")
            ? "khóa" : "mở khóa";

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn " + action + " tài khoản này?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection con = DBconnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

            loadUser();
            JOptionPane.showMessageDialog(
                this, "Cập nhật tài khoản thành công!"
            );
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            this,
            "Có lỗi khi thao tác với database:\n" + ex.getMessage(),
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Admin().setVisible(true)
        );
    }
}