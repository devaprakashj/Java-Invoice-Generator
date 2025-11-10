package invoicegenerator.service;

import invoicegenerator.model.Customer;
import invoicegenerator.model.Invoice;
import invoicegenerator.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {
    public void saveInvoice(Invoice invoice) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO invoices (customer_id, invoice_date, subtotal, tax, total) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, invoice.getCustomer().getCustomerId());
            stmt.setDate(2, new java.sql.Date(invoice.getInvoiceDate().getTime()));
            stmt.setDouble(3, invoice.getSubtotal());
            stmt.setDouble(4, invoice.getTax());
            stmt.setDouble(5, invoice.getTotal());
            
            stmt.executeUpdate();
            
            // Get the generated invoice ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getInt(1));
                }
            }
        }
    }

    public Invoice getInvoice(int invoiceId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT i.*, c.* FROM invoices i JOIN customers c ON i.customer_id = c.customer_id WHERE i.invoice_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                    
                    Invoice invoice = new Invoice(invoiceId, customer);
                    invoice.setInvoiceDate(rs.getDate("invoice_date"));
                    // Load invoice items here
                    return invoice;
                }
            }
        }
        return null;
    }

    public List<Invoice> getInvoicesByCustomer(int customerId) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM invoices WHERE customer_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create invoice objects and add to list
                    // This is a simplified version
                }
            }
        }
        return invoices;
    }
}