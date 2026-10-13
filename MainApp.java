package javaswing;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public MainApp() {
        setTitle("Quản lý mua bán thiết bị");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topContainer = new JPanel(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("  Xin chào: Nguyễn Văn An"), BorderLayout.WEST);
        headerPanel.add(new JButton("Đăng xuất"), BorderLayout.EAST);
        
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDanhSach = new JButton("Danh sách máy");
        JButton btnDonMua = new JButton("Đơn mua");
        JButton btnDonBan = new JButton("Đơn bán");
        
        menuPanel.add(btnDanhSach);
        menuPanel.add(btnDonMua);
        menuPanel.add(btnDonBan);

        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(menuPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(createDanhSachPanel(), "DanhSach");
        mainContentPanel.add(createDonMuaPanel(), "DonMua");
        mainContentPanel.add(createDonBanPanel(), "DonBan");

        add(mainContentPanel, BorderLayout.CENTER);

        btnDanhSach.addActionListener(e -> cardLayout.show(mainContentPanel, "DanhSach"));
        btnDonMua.addActionListener(e -> cardLayout.show(mainContentPanel, "DonMua"));
        btnDonBan.addActionListener(e -> cardLayout.show(mainContentPanel, "DonBan"));
    }

    private JPanel createDanhSachPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm theo tên:"));
        searchPanel.add(new JTextField(15));
        searchPanel.add(new JLabel("Hãng:"));
        searchPanel.add(new JComboBox<>(new String[]{"Tất cả", "Apple", "Samsung", "Xiaomi"}));
        searchPanel.add(new JButton("Tìm"));
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên máy", "Hãng", "Giá", "Người bán"};
        Object[][] data = {
            {"1", "iPhone 13 Pro", "Apple", "15,000,000", "user02"},
            {"2", "Samsung S22", "Samsung", "12,000,000", "user01"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(new JButton("+ Đăng bán máy mới"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDonMuaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tên máy", "Giá", "Người bán", "Trạng thái"};
        Object[][] data = {
            {"1", "iPhone 13 Pro", "15,000,000", "user02", "PENDING"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDonBanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tên máy", "Giá", "Người mua", "Trạng thái"};
        Object[][] data = {
            {"2", "Xiaomi 12", "8,000,000", "user03", "PENDING"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút Xác nhận / Hủy
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(new JButton("√ Xác nhận"));
        bottomPanel.add(new JButton("X Hủy"));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}