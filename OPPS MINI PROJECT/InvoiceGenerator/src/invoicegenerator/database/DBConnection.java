package invoicegenerator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DBConnection {
    private static final String DB_FILE = "invoice_db.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Create database file if it doesn't exist
            File dbFile = new File(DB_FILE);
            boolean needToCreateTables = !dbFile.exists();
            
            connection = DriverManager.getConnection(URL);
            
            if (needToCreateTables) {
                createTables();
            }
        }
        return connection;
    }

    private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create customers table
            stmt.execute("""
                CREATE TABLE customers (
                    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    address TEXT,
                    email TEXT,
                    phone TEXT
                )
            """);

            // Create invoices table
            stmt.execute("""
                CREATE TABLE invoices (
                    invoice_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER,
                    invoice_date DATE,
                    subtotal DECIMAL(10,2),
                    tax DECIMAL(10,2),
                    total DECIMAL(10,2),
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                )
            """);

            // Create invoice_items table
            stmt.execute("""
                CREATE TABLE invoice_items (
                    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    invoice_id INTEGER,
                    description TEXT,
                    quantity INTEGER,
                    unit_price DECIMAL(10,2),
                    amount DECIMAL(10,2),
                    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
                )
            """);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}