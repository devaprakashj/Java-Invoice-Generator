package invoicegenerator.util;

import java.io.*;
import java.text.SimpleDateFormat;
import invoicegenerator.model.*;

public class PDFGenerator {
    public void generateInvoicePDF(Invoice invoice, String outputPath) throws IOException {
        // For now, we'll generate a simple text-based invoice
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            // Write company header
            writer.println("Your Company Name");
            writer.println("123 Business Street");
            writer.println("City, State 12345");
            writer.println("Phone: (123) 456-7890\n");

            // Write customer information
            writer.println("Bill To:");
            writer.println(invoice.getCustomer().getName());
            writer.println(invoice.getCustomer().getAddress());
            writer.println("Phone: " + invoice.getCustomer().getPhone());
            writer.println("Email: " + invoice.getCustomer().getEmail() + "\n");

            // Write invoice details
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            writer.println("Invoice Details:");
            writer.println("Invoice Number: " + invoice.getInvoiceId());
            writer.println("Date: " + dateFormat.format(invoice.getInvoiceDate()) + "\n");

            // Write items table
            writer.println(String.format("%-40s %10s %12s %12s", "Description", "Quantity", "Unit Price", "Amount"));
            writer.println("-".repeat(76));

            for (InvoiceItem item : invoice.getItems()) {
                writer.println(String.format("%-40s %10d %12.2f %12.2f",
                    item.getDescription(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getQuantity() * item.getUnitPrice()));
            }

            writer.println("-".repeat(76));

            // Write totals
            writer.println(String.format("%64s %12.2f", "Subtotal:", invoice.getSubtotal()));
            writer.println(String.format("%64s %12.2f", "GST (18%):", invoice.getTax()));
            writer.println(String.format("%64s %12.2f", "Total:", invoice.getTotal()));
        }
    }
}