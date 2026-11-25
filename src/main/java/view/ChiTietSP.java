/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;
public class ChiTietSP extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ChiTietSP.class.getName());

     private model.Phone currentPhone;
    private model.User currentUser;
    
    
   public ChiTietSP(java.awt.Frame parent, boolean modal, model.Phone phone, model.User user) {
        super(parent, modal);
        this.currentPhone = phone;
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(parent); 
        loadData();         
    }
    public void hideBuyButton() {
        Mua_Ngay_.setVisible(false); 
    }
    private void loadData() {
        jLabel9.setText(currentPhone.getName());
        jLabel10.setText(currentPhone.getBrand());
        jLabel11.setText(String.format("%,.0f", currentPhone.getPrice()) + " VNĐ");
        txtMoTa.setText(currentPhone.getDescription());
        txtMoTa.setCaretPosition(0);
        jLabel18.setText(String.valueOf(currentPhone.getId()));
        String vPath = currentPhone.getVideoPath();
        if (vPath != null && !vPath.trim().isEmpty()) {
            jLabel16.setText("<html><a href=''>Xem video chi tiết</a></html>");
            jLabel16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            for (java.awt.event.MouseListener ml : jLabel16.getMouseListeners()) {
                jLabel16.removeMouseListener(ml);
            }
            jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(vPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            jLabel16.setText("Không có video");
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        model.User seller = userDAO.findById(currentPhone.getUserId());
        if (seller != null) {
            jLabel13.setText(seller.getFullName());
            jLabel14.setText(seller.getPhoneNumber());
        } else {
            jLabel13.setText("Không xác định");
            jLabel14.setText("Không xác định");
        }
        hienThiAnh();
    }
    private void hienThiAnh() {
        jPanel1.removeAll();
        jPanel1.setLayout(new java.awt.BorderLayout());

        byte[] data = new dao.PhoneDAO().getImageData(currentPhone.getId());

        if (data == null || data.length == 0) {
            javax.swing.JLabel lbl = new javax.swing.JLabel("Không có ảnh", javax.swing.SwingConstants.CENTER);
            lbl.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
            jPanel1.add(lbl, java.awt.BorderLayout.CENTER);
        } else {
            int w = jPanel1.getWidth() > 0 ? jPanel1.getWidth() : 200;
            int h = jPanel1.getHeight() > 0 ? jPanel1.getHeight() : 196;
            java.awt.Image img = new javax.swing.ImageIcon(data).getImage()
                    .getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
            javax.swing.JLabel lblImage = new javax.swing.JLabel(new javax.swing.ImageIcon(img));
            lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jPanel1.add(lblImage, java.awt.BorderLayout.CENTER);
    }

    jPanel1.revalidate();
    jPanel1.repaint();
}
    // ... (các phần khác giữ nguyên)
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Mua_Ngay_ = new javax.swing.JButton();
        Nut_Dong_ = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(jLabel8);

        jPanel2.setBackground(new java.awt.Color(236, 244, 251));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("CHI TIẾT SẢN PHẨM");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );

        jLabel2.setText("Tên máy:");

        jLabel9.setText("Tên sản phẩm");

        jLabel3.setText("Hãng:");

        jLabel10.setText("Tên Hãng");

        jLabel17.setText("Id:");

        jLabel18.setText("Id của máy");

        jLabel4.setText("Giá:");

        jLabel11.setText("Số tiền");

        jLabel5.setText("Mô tả:");

        jLabel15.setText("Video chi tiết:");

        jLabel16.setText("Link vd");

        jLabel6.setText("Người bán:");

        jLabel13.setText("Tên người bán");

        jLabel7.setText("Số ĐT:");

        jLabel14.setText("Số");

        Mua_Ngay_.setBackground(new java.awt.Color(0, 102, 102));
        Mua_Ngay_.setForeground(new java.awt.Color(255, 255, 255));
        Mua_Ngay_.setText("Mua Ngay");
        Mua_Ngay_.addActionListener(this::Mua_Ngay_ActionPerformed);

        Nut_Dong_.setBackground(new java.awt.Color(255, 102, 0));
        Nut_Dong_.setText("Đóng");
        Nut_Dong_.addActionListener(this::Nut_Dong_ActionPerformed);

        txtMoTa.setEditable(false);
        txtMoTa.setColumns(20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setRows(5);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setOpaque(false);
        jScrollPane1.setViewportView(txtMoTa);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel1)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Mua_Ngay_, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(Nut_Dong_, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(34, 34, 34)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18))))
                        .addContainerGap(69, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(jLabel5)
                                .addGap(59, 59, 59))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel14)
                            .addComponent(Nut_Dong_, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(Mua_Ngay_, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Nut_Dong_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nut_Dong_ActionPerformed

        this.dispose();
    }//GEN-LAST:event_Nut_Dong_ActionPerformed

    private void Mua_Ngay_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Mua_Ngay_ActionPerformed

        if (currentPhone.getUserId() == currentUser.getId()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Bạn không thể mua máy của chính mình!");
            return;
        }

        dao.OrderDAO orderDAO = new dao.OrderDAO();
        if (orderDAO.existsByPhoneAndBuyer(currentPhone.getId(), currentUser.getId())) {
            javax.swing.JOptionPane.showMessageDialog(this, "Bạn đã đặt mua máy này rồi, vui lòng chờ xác nhận!");
            return;
        }

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn mua " + currentPhone.getName() + " với giá " + String.format("%,.0f", currentPhone.getPrice()) + " đ?", 
            "Xác nhận mua hàng", 
            javax.swing.JOptionPane.YES_NO_OPTION);
            
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {

                model.Order order = new model.Order();
                order.setPhoneId(currentPhone.getId());
                order.setBuyerId(currentUser.getId());
                order.setSellerId(currentPhone.getUserId());
                order.setPrice(currentPhone.getPrice());
                order.setStatus("PENDING");

                if (orderDAO.create(order)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Đặt mua thành công! Vui lòng chờ người bán xác nhận.");
                    this.dispose(); // Đóng form
                    
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Đặt mua thất bại! Vui lòng thử lại.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
            }
        }
    
    }//GEN-LAST:event_Mua_Ngay_ActionPerformed

    public static void main(String args[]) {
    
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Mua_Ngay_;
    private javax.swing.JButton Nut_Dong_;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtMoTa;
    // End of variables declaration//GEN-END:variables
}
