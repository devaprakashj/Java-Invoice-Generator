package invoicegenerator.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Invoice {
    private int invoiceId;
    private Customer customer;
    private Date invoiceDate;
    private List<InvoiceItem> items;
    private double subtotal;
    private double tax;
    private double total;

    public Invoice(int invoiceId, Customer customer) {
        this.invoiceId = invoiceId;
        this.customer = customer;
        this.invoiceDate = new Date();
        this.items = new ArrayList<>();
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
        calculateTotals();
    }

    private void calculateTotals() {
        subtotal = items.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
        tax = subtotal * 0.18; // 18% GST
        total = subtotal + tax;
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }
}