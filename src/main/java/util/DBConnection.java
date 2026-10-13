package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/phone_shop"
        + "?useSSL=false"
        + "&serverTimezone=Asia/Ho_Chi_Minh"
        + "&characterEncoding=UTF-8";

    private static final String USER = "root";
    private static final String PASSWORD = "Quan@Emiya1415";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }}