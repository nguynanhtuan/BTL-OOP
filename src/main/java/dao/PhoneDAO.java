package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Phone;
import util.DBConnection;

public class PhoneDAO {
    public boolean updateImage(int phoneId, java.io.File file) {
    try {
        java.awt.image.BufferedImage src = javax.imageio.ImageIO.read(file);
        int w = 400, h = src.getHeight() * w / src.getWidth();
        java.awt.image.BufferedImage small =
            new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = small.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();

        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(small, "jpg", bos);

        String sql = "UPDATE phones SET image_data = ? WHERE id = ?";
        try (java.sql.Connection c = util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBytes(1, bos.toByteArray());
            ps.setInt(2, phoneId);
            return ps.executeUpdate() > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
    public byte[] getImageData(int phoneId) {
    String sql = "SELECT image_data FROM phones WHERE id = ?";
    try (java.sql.Connection c = util.DBConnection.getConnection();
         java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, phoneId);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBytes(1);
        }
    } catch (Exception e) { e.printStackTrace(); }
    return null;
}

    
    public int insert(Phone p) {
    String sql = "INSERT INTO phones (name, brand, price, description, image_path, video_path, status, user_id) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, p.getName());
        ps.setString(2, p.getBrand());
        ps.setDouble(3, p.getPrice());
        ps.setString(4, p.getDescription());
        ps.setString(5, p.getImagePath());
        ps.setString(6, p.getVideoPath());
        ps.setString(7, p.getStatus());
        ps.setInt(8, p.getUserId());
        if (ps.executeUpdate() > 0) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}

 
    public boolean update(Phone p) {
        // Đã thêm cột video_path
        String sql = "UPDATE phones SET name=?, brand=?, price=?, description=?, "
                   + "image_path=?, video_path=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBrand());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getImagePath());
            ps.setString(6, p.getVideoPath()); 
            ps.setString(7, p.getStatus());
            ps.setInt(8, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
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

 
    private Phone mapRow(ResultSet rs) throws Exception {
        Phone p = new Phone();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setPrice(rs.getDouble("price"));
        p.setDescription(rs.getString("description"));
        p.setImagePath(rs.getString("image_path"));
        p.setVideoPath(rs.getString("video_path")); 
        p.setStatus(rs.getString("status"));
        p.setUserId(rs.getInt("user_id"));
        p.setCreatedAt(rs.getTimestamp("created_at")); 
        return p;
    }
}