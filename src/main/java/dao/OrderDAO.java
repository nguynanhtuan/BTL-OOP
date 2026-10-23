package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import util.DBConnection;

public class OrderDAO {

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
            e.printStackTrace();
        }
        return false;
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
            e.printStackTrace();
        }
        return false;
    }

    // ==================== KIỂM TRA ĐÃ MUA CHƯA ====================
    public boolean existsByPhoneAndBuyer(int phoneId, int buyerId) {
        String sql = "SELECT * FROM orders WHERE phone_id = ? AND buyer_id = ? "
                   + "AND status != 'CANCELED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phoneId);
            ps.setInt(2, buyerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== LẤY ĐƠN MUA CỦA TÔI ====================
    public List<Order> findByBuyer(int buyerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== LẤY ĐƠN BÁN CỦA TÔI ====================
    public List<Order> findBySeller(int sellerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE seller_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== HÀM PHỤ ====================
    private Order mapRow(ResultSet rs) throws Exception {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setPhoneId(rs.getInt("phone_id"));
        o.setBuyerId(rs.getInt("buyer_id"));
        o.setSellerId(rs.getInt("seller_id"));
        o.setPrice(rs.getDouble("price"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}