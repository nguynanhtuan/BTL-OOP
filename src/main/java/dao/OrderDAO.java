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


public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

 
    public static final String PENDING = "PENDING";
    public static final String WAITING_PAYMENT = "WAITING_PAYMENT";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELED = "CANCELED";

 
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

 
    public boolean updateStatus(int orderId, String status) {
            String sql = "UPDATE orders SET status = ?, "
               + "paid_at = CASE "
               + "WHEN ? = 'CONFIRMED' AND paid_at IS NULL THEN NOW() "
               + "ELSE paid_at END, "
               + "checked_at = CASE "
               + "WHEN ? = 'CHECKED' AND checked_at IS NULL THEN NOW() "
               + "ELSE checked_at END "
               + "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, status);
            ps.setInt(4, orderId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật trạng thái đơn " + orderId, e);
            return false;
        }
    }

  
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

 

   
    public Order findById(int id) {
        List<Order> list = query("SELECT * FROM orders WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

 
    public List<Order> findAll() {
        return query("SELECT * FROM orders ORDER BY id DESC");
    }

    
    public List<Order> findByStatus(String status) {
        return query("SELECT * FROM orders WHERE status = ? ORDER BY confirmed_at ASC, id ASC",
                     status);
    }

    
    public List<Order> findByBuyer(int buyerId) {
        return query("SELECT * FROM orders WHERE buyer_id = ? ORDER BY id DESC", buyerId);
    }

   
    public List<Order> findBySeller(int sellerId) {
        return query("SELECT * FROM orders WHERE seller_id = ? ORDER BY id DESC", sellerId);
    }


  
    public boolean sellerConfirmOrder(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'WAITING_PAYMENT', confirmed_at = NOW() "
              + "WHERE id = ? AND status = 'PENDING'",
                "UPDATE phones SET status = 'SOLD' WHERE id = ?");
    }


    public boolean confirmOrderAndSellPhone(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'CONFIRMED', confirmed_at = NOW() WHERE id = ?",
                "UPDATE phones SET status = 'SOLD' WHERE id = ?");
    }

   
    public boolean cancelWaitingPaymentOrder(int orderId) {
        return updateOrderAndPhone(orderId,
                "UPDATE orders SET status = 'CANCELED' WHERE id = ? AND status = 'WAITING_PAYMENT'",
                "UPDATE phones SET status = 'APPROVED' WHERE id = ?");
    }

  
    public boolean cancelOverdueAndRestorePhone(int orderId) {
        return cancelWaitingPaymentOrder(orderId);
    }

  

    
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

    
    private int getPhoneId(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT phone_id FROM orders WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("phone_id") : -1;
            }
        }
    }


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
        o.setPaidAt(rs.getTimestamp("paid_at"));
        o.setCheckedAt(rs.getTimestamp("checked_at"));
        return o;
    }
    public model.OrderDetail getOrderDetail(int orderId) {
    String sql = "SELECT o.phone_id, p.name AS phone_name, p.price, p.image_path, o.status, "
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
                d.setPhoneId(rs.getInt("phone_id"));
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