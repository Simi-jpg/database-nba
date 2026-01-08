package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import utils.ClearConsole;

public class DBConnection {
    private static String userName = "*******";
    private static String password = "*******";

    private static String connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
            + "database=cs3380;"
            + "user=" + userName + ";"
            + "password=" + password + ";"
            + "encrypt=false;"
            + "trustServerCertificate=false;"
            + "loginTimeout=30;";

    private static Connection conn = null;

    public static Connection getConnection() {

        try {
            if (conn == null || conn.isClosed()) {

                conn = DriverManager.getConnection(connectionUrl);
                conn.setAutoCommit(false);
                System.out.println("[DB] Connected successfully.");
                
                ClearConsole.clear();
            }

        } catch (SQLException e) {
            System.out.println("[DB] Failed to connect.");

            e.printStackTrace();
        }

        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {

            try {

                conn.close();
                System.out.println("[DB] Connection closed");

            } catch (SQLException e) {

                System.out.println("[DB] Error while closing connection");
                System.out.println(e.getMessage());
            }
        }
    }
}
