/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;

public class DangKi extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DangKi.class.getName());

    public DangKi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        Quay_Lai_ = new javax.swing.JButton();
        Dang_Ki_ = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(236, 244, 251));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel1.setText("Đăng kí tài khoản");

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        jLabel4.setText("Nhâp lại MK:");

        jLabel5.setText("Họ Tên:");

        jTextField3.addActionListener(this::jTextField3ActionPerformed);

        jLabel6.setText("Số ĐT:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MB Bank", "Agri Bank", "Sacom Bank", "VietCom Bank", "VietTin Bank" }));

        jLabel8.setText("Ngân hàng");

        jLabel7.setText("Số tài khoản:");

        Quay_Lai_.setBackground(new java.awt.Color(255, 102, 0));
        Quay_Lai_.setText("Quay lại");
        Quay_Lai_.addActionListener(this::Quay_Lai_ActionPerformed);

        Dang_Ki_.setBackground(new java.awt.Color(0, 102, 102));
        Dang_Ki_.setForeground(new java.awt.Color(255, 255, 255));
        Dang_Ki_.setText("Đăng kí");
        Dang_Ki_.addActionListener(this::Dang_Ki_ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Quay_Lai_, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(Dang_Ki_, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField1)
                                .addComponent(jPasswordField1)
                                .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                .addComponent(jTextField2)
                                .addComponent(jTextField3)
                                .addComponent(jTextField4)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addComponent(Dang_Ki_, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Quay_Lai_, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void Quay_Lai_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Quay_Lai_ActionPerformed

        this.dispose();
        
    }//GEN-LAST:event_Quay_Lai_ActionPerformed

    private void Dang_Ki_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dang_Ki_ActionPerformed

        String username = jTextField1.getText().trim();
        String password = new String(jPasswordField1.getPassword()).trim();
        String confirmPassword = new String(jPasswordField2.getPassword()).trim();
        String fullName = jTextField2.getText().trim();
        String phone = jTextField3.getText().trim();
        String bankName = jComboBox1.getSelectedItem().toString();
        String bankAccount = jTextField4.getText().trim();

        if (username.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập Username!");
            return;
        }
        if (password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập Password!");
            return;
        }
        if (confirmPassword.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập lại mật khẩu!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
            return;
        }
        if (fullName.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ Tên!");
            return;
        }
        if (phone.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập Số ĐT!");
            return;
        }
        if (!phone.matches("\\d{9,11}")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Số ĐT phải gồm 9 đến 11 chữ số!");
            return;
        }
        if (bankName.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngân hàng!");
            return;
        }
        if (bankAccount.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập Số tài khoản!");
            return;
        }
            if (bankAccount.length() < 8 || bankAccount.length() > 19) {
                javax.swing.JOptionPane.showMessageDialog(this, "Số tài khoản ngân hàng không hợp lệ (phải từ 8 đến 19 chữ số)!");
                return;
            }
        

        try {

            model.User u = new model.User();
            u.setUsername(username);
            u.setPassword(password);
            u.setFullName(fullName);
            u.setPhoneNumber(phone);
            u.setBankName(bankName);
            u.setBankAccount(bankAccount);
            dao.UserDAO userDAO = new dao.UserDAO();
            boolean success = userDAO.register(u); 
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!");
                this.dispose();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên tài khoản có thể đã tồn tại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
        }
        
    }//GEN-LAST:event_Dang_Ki_ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DangKi dialog = new DangKi(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Dang_Ki_;
    private javax.swing.JButton Quay_Lai_;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
