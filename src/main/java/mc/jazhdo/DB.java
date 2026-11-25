package mc.jazhdo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final Connection conn;
    static {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + Main.appDirectory.resolve("database.db").toAbsolutePath().toString());
        } catch (SQLException ex) {
            Main.logger.error("Error getting the database. Shutting down... Error: {}", ex.getMessage());
            System.exit(500);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void init() {
        
    }

    public static Connection get() {
        return conn;
    }
}
