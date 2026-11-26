package mc.jazhdo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DB {
    private Connection conn;
    private final ExecutorService executor = Executors.newSingleThreadExecutor((r) -> {
        Thread t = new Thread("jazhdo-db");
        t.setDaemon(true);
        return t;
    });

    public DB() {
        executor.submit(() -> {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:" + Main.appDirectory.resolve("database.db").toAbsolutePath().toString());
            } catch (SQLException ex) {
                Main.logger.error("Error getting the database. Shutting down... Error: {}", ex.getMessage());
                System.exit(500);
                throw new ExceptionInInitializerError(ex);
            }
        });
    }

    @FunctionalInterface 
    public interface DBRunnable {
        void accept(Connection conn) throws SQLException;
    }
    @FunctionalInterface 
    public interface DBRunnableWithReturn<T> {
        T apply(Connection conn) throws SQLException;
    }

    public void run(DBRunnable runnable) {
        CompletableFuture.runAsync(() -> {
            try {
                runnable.accept(conn);
            } catch (SQLException ex) {
                Main.logger.error("An error occurred in the database thread: {}", ex.getMessage());
            }
        }, executor);
    }

    public <T> CompletableFuture<T> run(DBRunnableWithReturn<T> runnable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runnable.apply(conn);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }, executor);
    }
}
