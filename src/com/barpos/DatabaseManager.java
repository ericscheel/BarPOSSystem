package com.barpos;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:barpos.db";

    public static void init() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS products (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "name TEXT NOT NULL," +
                         "price REAL NOT NULL," +
                         "category TEXT NOT NULL)";
            conn.createStatement().execute(sql);
        }
    }

    public static List<String> loadCategories() throws SQLException {
        Set<String> categories = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT DISTINCT category FROM products";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return new ArrayList<>(categories);
    }

    public static List<Product> loadProducts(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT name, price FROM products WHERE category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(rs.getString("name"), rs.getDouble("price")));
            }
        }
        return products;
    }

    public static void addProduct(Product product, String category) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO products (name, price, category) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, category);
            stmt.executeUpdate();
        }
    }

    public static void updateProduct(Product oldProduct, String oldCategory, Product newProduct, String newCategory) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE products SET name = ?, price = ?, category = ? WHERE name = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newProduct.getName());
            stmt.setDouble(2, newProduct.getPrice());
            stmt.setString(3, newCategory);
            stmt.setString(4, oldProduct.getName());
            stmt.setString(5, oldCategory);
            stmt.executeUpdate();
        }
    }

    public static void deleteProduct(Product product, String category) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM products WHERE name = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setString(2, category);
            stmt.executeUpdate();
        }
    }

    public static void addCategory(String category) {
        // Kategorie wird durch Produktanlage erzeugt, keine eigene Tabelle nötig
    }

    public static void deleteCategory(String category) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM products WHERE category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            stmt.executeUpdate();
        }
    }

    public static boolean productExists(Product product, String category) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT COUNT(*) FROM products WHERE name = ? AND category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, product.getName());
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
