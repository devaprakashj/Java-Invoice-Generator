import java.sql.*;

public class ShowDatabase {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:invoice_db.db";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            // Show customers
            System.out.println("\n=== CUSTOMERS ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
                while (rs.next()) {
                    System.out.printf("ID: %d, Name: %s, Address: %s, Email: %s, Phone: %s%n",
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("phone"));
                }
            }

            // Show invoices
            System.out.println("\n=== INVOICES ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM invoices")) {
                while (rs.next()) {
                    System.out.printf("Invoice ID: %d, Customer ID: %d, Date: %s, Total: %.2f%n",
                        rs.getInt("invoice_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("invoice_date"),
                        rs.getDouble("total"));
                }
            }

            // Show invoice items
            System.out.println("\n=== INVOICE ITEMS ===");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM invoice_items")) {
                while (rs.next()) {
                    System.out.printf("Item ID: %d, Invoice ID: %d, Description: %s, Quantity: %d, Unit Price: %.2f%n",
                        rs.getInt("item_id"),
                        rs.getInt("invoice_id"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}