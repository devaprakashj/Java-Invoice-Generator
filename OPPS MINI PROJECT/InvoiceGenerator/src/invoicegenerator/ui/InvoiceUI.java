package invoicegenerator.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import invoicegenerator.model.*;
import invoicegenerator.service.InvoiceService;
import invoicegenerator.util.PDFGenerator;

public class InvoiceUI extends JFrame {
    private JTextField customerNameField, customerAddressField, customerEmailField, customerPhoneField;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JLabel subtotalLabel, taxLabel, totalLabel;
    private InvoiceService invoiceService;
    private PDFGenerator pdfGenerator;

    public InvoiceUI() {
        invoiceService = new InvoiceService();
        pdfGenerator = new PDFGenerator();
        
        setTitle("Invoice Generator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Add customer information panel
        mainPanel.add(createCustomerPanel(), BorderLayout.NORTH);
        
        // Add items table
        mainPanel.add(createItemsPanel(), BorderLayout.CENTER);
        
        // Add totals and buttons panel
        mainPanel.add(createTotalsPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Initialize the table model
        initializeTable();
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        
        panel.add(new JLabel("Name:"));
        customerNameField = new JTextField();
        panel.add(customerNameField);
        
        panel.add(new JLabel("Address:"));
        customerAddressField = new JTextField();
        panel.add(customerAddressField);
        
        panel.add(new JLabel("Email:"));
        customerEmailField = new JTextField();
        panel.add(customerEmailField);
        
        panel.add(new JLabel("Phone:"));
        customerPhoneField = new JTextField();
        panel.add(customerPhoneField);
        
        return panel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Invoice Items"));
        
        tableModel = new DefaultTableModel(
            new Object[]{"Description", "Quantity", "Unit Price", "Amount"}, 0
        );
        itemsTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        
        addButton.addActionListener(e -> addNewRow());
        removeButton.addActionListener(e -> removeSelectedRow());
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Totals"));
        
        panel.add(new JLabel("Subtotal:"));
        subtotalLabel = new JLabel("₹0.00");
        panel.add(subtotalLabel);
        
        panel.add(new JLabel("Tax (18%):"));
        taxLabel = new JLabel("₹0.00");
        panel.add(taxLabel);
        
        panel.add(new JLabel("Total:"));
        totalLabel = new JLabel("₹0.00");
        panel.add(totalLabel);
        
        JButton generateButton = new JButton("Generate Invoice");
        generateButton.addActionListener(e -> generateInvoice());
        panel.add(generateButton);
        
        return panel;
    }

    private void initializeTable() {
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && 
                (e.getColumn() == 1 || e.getColumn() == 2)) { // Only update on quantity or price changes
                updateTotals();
            }
        });
    }

    private void addNewRow() {
        tableModel.addRow(new Object[]{"", "0", "0.00", "0.00"});
    }

    private void removeSelectedRow() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            updateTotals();
        }
    }

    private void updateTotals() {
        double subtotal = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String qtyStr = tableModel.getValueAt(i, 1).toString();
                String priceStr = tableModel.getValueAt(i, 2).toString();
                if (!qtyStr.isEmpty() && !priceStr.isEmpty()) {
                    int quantity = Integer.parseInt(qtyStr);
                    double unitPrice = Double.parseDouble(priceStr);
                    double amount = quantity * unitPrice;
                    tableModel.setValueAt(String.format("%.2f", amount), i, 3);
                    subtotal += amount;
                }
            } catch (NumberFormatException ex) {
                // Handle invalid number input
            }
        }
        
        double tax = subtotal * 0.18;
        double total = subtotal + tax;
        
        subtotalLabel.setText(String.format("₹%.2f", subtotal));
        taxLabel.setText(String.format("₹%.2f", tax));
        totalLabel.setText(String.format("₹%.2f", total));
    }

    private void generateInvoice() {
        // Create customer
        Customer customer = new Customer(
            1, // You would normally get this from your database
            customerNameField.getText(),
            customerAddressField.getText(),
            customerEmailField.getText(),
            customerPhoneField.getText()
        );
        
        // Create invoice
        Invoice invoice = new Invoice(1, customer); // You would normally get the ID from your database
        
        // Add items to invoice
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String description = tableModel.getValueAt(i, 0).toString();
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            double unitPrice = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
            
            invoice.addItem(new InvoiceItem(description, quantity, unitPrice));
        }
        
        try {
            // Save invoice to database
            invoiceService.saveInvoice(invoice);
            
            // Generate PDF
            String pdfPath = "invoice_" + invoice.getInvoiceId() + ".pdf";
            pdfGenerator.generateInvoicePDF(invoice, pdfPath);
            
            JOptionPane.showMessageDialog(this,
                "Invoice generated successfully!\nPDF saved as: " + pdfPath,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating invoice: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InvoiceUI ui = new InvoiceUI();
            ui.setVisible(true);
        });
    }
}