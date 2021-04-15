package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
@Component
public class SqliteUtils {
    private final Map<String, Connection> connections = new HashMap<>();

    public synchronized Connection getConnection(String dbName) {
        Connection conn = connections.computeIfAbsent(dbName, (k) -> connect(dbName));
        try {
            if (conn.isClosed()) {
                conn = connect(dbName);
                connections.put(dbName, conn);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return conn;
    }

    private Connection connect(String dbname) {
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