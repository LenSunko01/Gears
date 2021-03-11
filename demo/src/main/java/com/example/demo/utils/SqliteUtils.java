package com.example.demo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteUtils {
    public static Connection connect(String dbname) {
        Connection conn;
        try {
            String url = "jdbc:sqlite:" + dbname;
            conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}