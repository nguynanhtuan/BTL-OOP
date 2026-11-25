/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

public class GiaoDienDangNhap extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GiaoDienDangNhap.class.getName());
        
    public GiaoDienDangNhap() {
        initComponents();
        setLocationRelativeTo(null);
        getContentPane().setBackground(jPanel2.getBackground());
        try {
            java.net.URL iconURL = getClass().getResource("/icons/logo.png");
            if (iconURL != null) {
                this.setIconImage(new javax.swing.ImageIcon(iconURL).getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Dang_Nhap_ = new javax.swing.JButton();
        Dang_Ki_ = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        jPasswordField1.setText("jPasswordField1");

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(240, 242, 245));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new java.awt.Color(236, 244, 251));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel2.setText("Đăng nhập");

        Dang_Nhap_.setBackground(new java.awt.Color(0, 102, 102));
        Dang_Nhap_.setForeground(new java.awt.Color(255, 255, 255));
        Dang_Nhap_.setText("Đăng nhập");
        Dang_Nhap_.addActionListener(this::Dang_Nhap_ActionPerformed);

        Dang_Ki_.setBackground(new java.awt.Color(144, 202, 249));
        Dang_Ki_.setText("Đăng kí");
        Dang_Ki_.addActionListener(this::Dang_Ki_ActionPerformed);

        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel4.setText("Password:");

        jLabel3.setText("UserName:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Số điện thoại hỗ trợ: 0393163529");
        jLabel5.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(32, 32, 32)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Dang_Ki_, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                    .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                    .addComponent(Dang_Nhap_, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(Dang_Nhap_, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Dang_Ki_, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void Dang_Ki_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dang_Ki_ActionPerformed

        view.DangKi dangKiForm = new view.DangKi(this, true);
        this.setVisible(false);
        dangKiForm.setVisible(true);
        this.setVisible(true); 
    }//GEN-LAST:event_Dang_Ki_ActionPerformed

    private void Dang_Nhap_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dang_Nhap_ActionPerformed

        String username = jTextField1.getText().trim();
        String password = new String(jPasswordField2.getPassword()).trim();  


        if (username.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        try {

            dao.UserDAO userDAO = new dao.UserDAO(); 
            model.User user = userDAO.login(username, password); 

            if (user != null) {
                this.dispose();
                String role = user.getRole(); 
                if (role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("quản trị viên"))) {
                    new view.AdminFrame(user).setVisible(true);
                } else {
                    new view.Main(user).setVisible(true);
                }

            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không chính xác (hoặc tài khoản đã bị khóa)!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }//GEN-LAST:event_Dang_Nhap_ActionPerformed

    public static void main(String args[]) {
        new Thread(() -> {
        try (java.sql.Connection c = util.DBConnection.getConnection()) { }
        catch (Exception e) { e.printStackTrace(); }
    }).start();

    java.awt.EventQueue.invokeLater(() -> {

        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getSource() instanceof javax.swing.JButton) {
                javax.swing.JButton button =
                        (javax.swing.JButton) event.getSource();

                button.setFocusPainted(false);
            }
        }, java.awt.AWTEvent.FOCUS_EVENT_MASK);

        new GiaoDienDangNhap().setVisible(true);
    });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Dang_Ki_;
    private javax.swing.JButton Dang_Nhap_;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
