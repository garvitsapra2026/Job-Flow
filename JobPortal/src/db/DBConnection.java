package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    // Use the same JDBC connection style as RailSync.
    private static final String URL = "jdbc:mysql://localhost:3306/jobportal";
    private static final String USER = "root";
    private static final String PASSWORD = "garvit@123"; // Change if your MySQL password is different.

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected Successfully!");
            return con;
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
