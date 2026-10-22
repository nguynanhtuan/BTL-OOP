package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Phone;
import util.DBConnection;

public class PhoneDAO {

    // ==================== THÊM TIN ĐĂNG ====================
    public boolean insert(Phone p) {
        String sql = "INSERT INTO phones (name, brand, price, description, image_path, status, user_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBrand());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getImagePath());
            ps.setString(6, p.getStatus());
            ps.setInt(7, p.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== CẬP NHẬT TIN ====================
    public boolean update(Phone p) {
        String sql = "UPDATE phones SET name=?, brand=?, price=?, description=?, "
                   + "image_path=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBrand());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getImagePath());
            ps.setString(6, p.getStatus());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== XÓA TIN ====================
    public boolean delete(int id) {
        String sql = "DELETE FROM phones WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== ĐỔI TRẠNG THÁI ====================
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE phones SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== LẤY MÁY ĐÃ DUYỆT ====================
    public List<Phone> findAllApproved() {
        List<Phone> list = new ArrayList<>();
        String sql = "SELECT * FROM phones WHERE status = 'APPROVED' ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== LẤY TẤT CẢ TIN ====================
    public List<Phone> findAll() {
        List<Phone> list = new ArrayList<>();
        String sql = "SELECT * FROM phones ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== LỌC THEO TRẠNG THÁI ====================
    public List<Phone> findByStatus(String status) {
        List<Phone> list = new ArrayList<>();
        String sql = "SELECT * FROM phones WHERE status = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== TÌM THEO TÊN ====================
    public List<Phone> searchByName(String keyword) {
        List<Phone> list = new ArrayList<>();
        String sql = "SELECT * FROM phones WHERE status = 'APPROVED' "
                   + "AND name LIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== LẤY TIN CỦA 1 USER ====================
    public List<Phone> findByUser(int userId) {
        List<Phone> list = new ArrayList<>();
        String sql = "SELECT * FROM phones WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== TÌM 1 TIN THEO ID ====================
    public Phone findById(int id) {
        String sql = "SELECT * FROM phones WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== ĐẾM TIN CHỜ DUYỆT ====================
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM phones WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== HÀM PHỤ ====================
    private Phone mapRow(ResultSet rs) throws Exception {
        Phone p = new Phone();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setPrice(rs.getDouble("price"));
        p.setDescription(rs.getString("description"));
        p.setImagePath(rs.getString("image_path"));
        p.setStatus(rs.getString("status"));
        p.setUserId(rs.getInt("user_id"));
        return p;
    }
}