package com.restaurante.backend;

import java.sql.*;

public class DbCheck {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/restaurant";
        String user = "postgres";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");

            System.out.println("\n--- Flyway Schema History ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank DESC")) {
                while (rs.next()) {
                    System.out.printf("Version: %s, Desc: %s, Success: %b%n",
                            rs.getString("version"), rs.getString("description"), rs.getBoolean("success"));
                }
            }

            System.out.println("\n--- Orders Table Columns ---");
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, "public", "orders", null)) {
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("COLUMN_NAME") + " (" + rs.getString("TYPE_NAME") + ")");
                }
            }
            
            System.out.println("\n--- Tenant Sequences Table ---");
            try (ResultSet rs = metaData.getTables(null, "public", "tenant_sequences", null)) {
                if (rs.next()) {
                    System.out.println("Table tenant_sequences EXISTS");
                } else {
                    System.out.println("Table tenant_sequences DOES NOT EXIST");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
