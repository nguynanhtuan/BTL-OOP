package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Order;
import util.DBConnection;

/**
 * DAO thao tác với bảng orders.
 * Toàn bộ SQL của đơn hàng nằm ở đây, tầng view chỉ gọi các hàm bên dưới.
 */
public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    // Các trạng thái dùng trong DAO (tránh gõ sai chuỗi)
    public static final String PENDING = "PENDING";
    public static final String WAITING_PAYMENT = "WAITING_PAYMENT";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELED = "CANCELED";

    // ==================== TẠO ĐƠN MUA ====================
    public boolean create(Order o) {
        String sql = "INSERT INTO orders (phone_id, buyer_id, seller_id, price, status) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, o.getPhoneId());
            ps.setInt(2, o.getBuyerId());
            ps.setInt(3, o.getSellerId());
            ps.setDouble(4, o.getPrice());
            ps.setString(5, o.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi tạo đơn hàng", e);
            return false;
        }
    }

    // ==================== CẬP NHẬT TRẠNG THÁI ====================
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật trạng thái đơn " + orderId, e);
            return false;
        }
    }

    // ==================== KIỂM TRA ĐÃ MUA CHƯA ====================
    public boolean existsByPhoneAndBuyer(int phoneId, int buyerId) {
        String sql = "SELECT 1 FROM orders WHERE phone_id = ? AND buyer_id = ? "
                   + "AND status <> 'CANCELED' LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phoneId);
            ps.setInt(2, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi kiểm tra đơn đã mua", e);
            return false;
        }
    }

    // ==================== TRUY VẤN ====================

    /** Tìm 1 đơn theo ID, không có trả null. */
    public Order findById(int id) {
        List<Order> list = query("SELECT * FROM orders WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    /** Lấy tất cả đơn (dùng cho Admin). */
    public List<Order> findAll() {
        return query("SELECT * FROM orders ORDER BY id DESC");
    }

    /** Lấy đơn theo trạng thái. */
    public List<Order> findByStatus(String status) {
        return query("SELECT * FROM orders WHERE status = ? ORDER BY confirmed_at ASC, id ASC",
                     status);
    }

    /** Lấy đơn mua của một người. */
    public List<Order> findByBuyer(int buyerId) {
        return query("SELECT * FROM orders WHERE buyer_id = ? ORDER BY id DESC", buyerId);
    }

    /** Lấy đơn bán của một người. */
    public List<Order> findBySeller(int sellerId) {
        return query("SELECT * FROM orders WHERE seller_id = ? ORDER BY id DESC", sellerId);
    }

    // ==================== NGHIỆP VỤ CÓ TRANSACTION ====================
    // Mỗi hàm cập nhật đồng thời đơn hàng + trạng thái máy: hoặc cùng thành công, hoặc cùng hoàn tác.

    /** Người bán xác nhận đơn: PENDING -> WAITING_PAYMENT, máy chuyển SOLD (giữ chỗ cho người mua). */
    public boolean sellerConfirmOrder(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'WAITING_PAYMENT', confirmed_at = NOW() "
              + "WHERE id = ? AND status = 'PENDING'",
                "UPDATE phones SET status = 'SOLD' WHERE id = ?");
    }

    /** Xác nhận đơn và chuyển máy thành SOLD (giữ lại để tương thích code cũ). */
    public boolean confirmOrderAndSellPhone(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'CONFIRMED', confirmed_at = NOW() WHERE id = ?",
                "UPDATE phones SET status = 'SOLD' WHERE id = ?");
    }

    /** Hủy đơn đang chờ thanh toán và mở bán lại máy (SOLD -> APPROVED). */
    public boolean cancelWaitingPaymentOrder(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'CANCELED' WHERE id = ? AND status = 'WAITING_PAYMENT'",
                "UPDATE phones SET status = 'APPROVED' WHERE id = ?");
    }

    /** Admin hủy đơn quá hạn thanh toán: cùng nghiệp vụ với hủy đơn chờ thanh toán. */
    public boolean cancelOverdueAndRestorePhone(int orderId) {
        return cancelWaitingPaymentOrder(orderId);
    }

    // ==================== HÀM PHỤ ====================

    /**
     * Chạy 2 câu UPDATE trong 1 transaction: câu 1 cập nhật đơn (tham số: orderId),
     * câu 2 cập nhật máy (tham số: phoneId của đơn). Câu nào không ảnh hưởng dòng nào thì hoàn tác cả hai.
     */
    private boolean updateOrderAndPhone(int orderId, String orderSql, String phoneSql) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int phoneId = getPhoneId(conn, orderId);
                if (phoneId == -1
                        || !executeUpdate(conn, orderSql, orderId)
                        || !executeUpdate(conn, phoneSql, phoneId)) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi transaction trên đơn " + orderId, e);
            return false;
        }
    }

    /** Chạy SELECT trả về danh sách Order, params gán theo thứ tự dấu ?. */
    private List<Order> query(String sql, Object... params) {
        List<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn đơn hàng: " + sql, e);
        }
        return list;
    }

    /** Lấy phone_id của đơn, trả -1 nếu không có. Dùng chung connection của transaction. */
    private int getPhoneId(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT phone_id FROM orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("phone_id") : -1;
            }
        }
    }

    /** Chạy UPDATE 1 tham số int, trả true nếu có ít nhất 1 dòng bị ảnh hưởng. */
    private boolean executeUpdate(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setPhoneId(rs.getInt("phone_id"));
        o.setBuyerId(rs.getInt("buyer_id"));
        o.setSellerId(rs.getInt("seller_id"));
        o.setPrice(rs.getDouble("price"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setConfirmedAt(rs.getTimestamp("confirmed_at"));
        return o;
    }
    public model.OrderDetail getOrderDetail(int orderId) {
    String sql = "SELECT p.name AS phone_name, p.price, p.image_path, o.status, "
               + "b.full_name AS buyer_name, "
               + "s.full_name AS seller_name, s.bank_name, s.bank_account "
               + "FROM orders o "
               + "JOIN phones p ON o.phone_id = p.id "
               + "JOIN users b ON o.buyer_id = b.id "
               + "JOIN users s ON o.seller_id = s.id "
               + "WHERE o.id = ?";

    try (java.sql.Connection conn = util.DBConnection.getConnection();
         java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setInt(1, orderId);
        try (java.sql.ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                model.OrderDetail d = new model.OrderDetail();
                d.setPhoneName(rs.getString("phone_name"));
                d.setPrice(rs.getDouble("price"));
                d.setImagePath(rs.getString("image_path"));
                d.setStatus(rs.getString("status"));
                d.setBuyerName(rs.getString("buyer_name"));
                d.setSellerName(rs.getString("seller_name"));
                d.setBankName(rs.getString("bank_name"));
                d.setBankAccount(rs.getString("bank_account"));
                return d;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}