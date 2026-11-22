package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://phone-shop-mysql-iq200lal-c502.d.aivencloud.com:25950/phone_shop"
        + "?sslMode=REQUIRED"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=Asia/Ho_Chi_Minh"
        + "&characterEncoding=UTF-8"
        + "&cachePrepStmts=true"
        + "&useServerPrepStmts=true";

    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_yQcSYQwX9SJ1ptJXJ35";

    private static final HikariDataSource ds;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(URL);
        cfg.setUsername(USER);
        cfg.setPassword(PASSWORD);
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(2);
        cfg.setMaxLifetime(300000);   // 5 phút, tránh server cắt kết nối rảnh
        ds = new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws Exception {
        return ds.getConnection();
    }
}