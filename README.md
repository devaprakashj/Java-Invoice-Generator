# Java-Invoice-Generator
Java-based invoice generation system with client-server model, SQLite DB, and A–Z business reports (OOPs mini project).
# InvoiceGenerator — Project Reports A to Z

## Overview

This repository implements a simple invoice generation system (client, server, UI, DB, PDF/text export). This README focuses on the reports that the project produces or should produce — an A-to-Z catalogue of reports, what each report is for, input filters, typical output columns, example SQL to obtain the data, scheduling suggestions, and security/access notes.

Reports are intended to be generated from the application's SQLite database file `invoice_db.db` at the project root (or from MySQL if you change `DBConnection`). Generated report files should be stored under `resources/reports/` (create this folder if you want to persist outputs).

## Quick run (Windows PowerShell)

- Compile (from project root):
```powershell
javac -cp "lib\*;src" src\invoicegenerator\**\*.java
```
- Start server (background/minimized):
```powershell
Start-Process java -ArgumentList '-cp','src;lib\sqlite-jdbc.jar;lib\mysql-connector-java.jar;lib\itextpdf.jar','invoicegenerator.server.InvoiceServer' -WindowStyle Minimized
```
- Launch UI:
```powershell
java -cp "src;lib\sqlite-jdbc.jar;lib\mysql-connector-java.jar;lib\itextpdf.jar" invoicegenerator.ui.InvoiceUI
```

## Dependencies

- Java 11+ (recommended)
- `lib/sqlite-jdbc.jar` (if using embedded SQLite)
- `lib/mysql-connector-java.jar` (if using MySQL)
- `lib/itextpdf.jar` (optional, for PDF generation if switched back to iText)

## Reports A → Z

For each report below you'll find: Purpose • Typical filters/inputs • Typical output columns • Example SQL (SQLite style, adapt for MySQL) • Schedule & output format • Notes / access.

A — Aged Receivables Report
- Purpose: Shows outstanding invoice balances grouped by ageing buckets (0-30, 31-60, 61-90, 90+ days).
- Inputs: End date, customer filter (optional).
- Output: customer_id, customer_name, invoice_id, due_date, balance, bucket.
- Example SQL (simplified):
  ```sql
  SELECT c.customer_id, c.name, i.invoice_id, i.invoice_date, i.total - coalesce(p.paid,0) AS balance
  FROM invoices i JOIN customers c ON i.customer_id=c.customer_id
  LEFT JOIN (SELECT invoice_id, SUM(amount) AS paid FROM payments GROUP BY invoice_id) p USING(invoice_id)
  WHERE (i.total - coalesce(p.paid,0)) > 0;
  ```
- Schedule: Daily or weekly.
- Format: CSV / PDF.
- Access: Finance team.

B — Billing Summary (Period)
- Purpose: Summary of billed amounts over a period.
- Inputs: Start date, end date, customer/product filters.
- Output: invoice_count, subtotal_sum, tax_sum, total_sum.
- Example SQL:
  ```sql
  SELECT COUNT(*) AS invoice_count, SUM(subtotal) AS subtotal, SUM(tax) AS tax, SUM(total) AS total
  FROM invoices WHERE invoice_date BETWEEN ? AND ?;
  ```
- Schedule: Monthly.
- Format: CSV / PDF.

C — Customer Ledger (Detailed)
- Purpose: Full transaction list per customer (invoices, payments, credit notes).
- Inputs: customer_id, date range.
- Output: date, type (invoice/payment), ref_id, description, debit, credit, running_balance.
- Example SQL: combine `invoices`, `payments`.
- Schedule: On-demand.
- Format: PDF/CSV.

D — Daily Sales Report
- Purpose: Sales totals per day.
- Inputs: Date or date range.
- Output: date, invoice_count, subtotal, tax, total.
- Example SQL:
  ```sql
  SELECT invoice_date, COUNT(*) AS invoices, SUM(total) AS total FROM invoices GROUP BY invoice_date;
  ```
- Schedule: Daily.

E — Expense Summary
- Purpose: If vendor/expense tracking added — shows expenses by category.
- Inputs: Category, date range.
- Output: category, amount, count.
- Schedule: Monthly.

F — Financial Summary (P&L snapshot)
- Purpose: High-level Profit & Loss (requires revenue & expense entries).
- Inputs: period (month, quarter).
- Output: revenue, cogs, gross_profit, operating_expenses, net_income.

G — GST / VAT Returns
- Purpose: Tax reporting (e.g., GST collected and payable).
- Inputs: Period (month/quarter).
- Output: taxable_sales, tax_collected, tax_paid, net_tax_due.
- Example SQL:
  ```sql
  SELECT SUM(subtotal) AS taxable, SUM(tax) AS tax FROM invoices WHERE invoice_date BETWEEN ? AND ?;
  ```
- Schedule: Monthly/Quarterly.
- Notes: Ensure tax rounding and jurisdiction rules.

H — Hourly Sales (if tracked)
- Purpose: Sales by hour-of-day (useful for retail/time-bound services).
- Inputs: date range.
- Output: hour, subtotal, total.

I — Invoice Detail Report
- Purpose: Detailed invoice (line-items) for audit or customer service.
- Inputs: invoice_id or range.
- Output: invoice header + item lines (description, qty, price, amount).
- Format: PDF or HTML template (`resources/invoice_template.html`).

J — Journal Entries (if present)
- Purpose: Shows accounting journal entries if integrated.
- Inputs: date range, account filter.

K — KPI Dashboard Data Extract
- Purpose: Data feed for KPI dashboards: MTD revenue, AR days, conversion rates.
- Inputs: timeframe.
- Output: key metric values.

L — Late Payments Report
- Purpose: Invoices past due, sorted by days overdue.
- Inputs: min days overdue, customer filter.
- Output: invoice_id, customer, due_date, days_overdue, balance.
- Schedule: Weekly.

M — Monthly Sales by Product/Service
- Purpose: Revenue breakdown by product/service.
- Inputs: month, product filter.
- Output: product_id, name, qty_sold, revenue.

N — Notes & Comments Extract
- Purpose: Export internal invoice notes/comments for review.
- Inputs: date range, user filter.
- Output: invoice_id, note_text, user, timestamp.

O — Outstanding Balances by Customer
- Purpose: Total unpaid per customer.
- Inputs: none or date cutoff.
- Output: customer_id, customer_name, outstanding_total.

P — Payment History
- Purpose: All payments received (useful for reconciliation).
- Inputs: date range.
- Output: payment_id, invoice_id, amount, date, method.

Q — Quarterly Tax Summary
- Purpose: Consolidated quarterly tax report for filing.
- Inputs: quarter/year.
- Output: taxable_sales, tax_collected, input_tax_credits (if applicable).

R — Revenue by Product / Category
- Purpose: Revenue grouped by product or category.
- Inputs: date range.
- Output: product/category, revenue, units_sold.

S — Sales by Region / Location
- Purpose: Geography-based sales breakdown (requires customer region data).
- Inputs: date range.
- Output: region, revenue, invoices.

T — Tax Summary (per jurisdiction)
- Purpose: Tax collected, broken down by tax rate or jurisdiction.
- Inputs: date range.
- Output: tax_rate, taxable_amount, tax_amount.

U — Unpaid Invoices (open items)
- Purpose: List of all invoices with outstanding balances.
- Inputs: none or aging cutoff.
- Output: invoice_id, customer, outstanding.

V — Vendor / Supplier Report (if vendors tracked)
- Purpose: Payables, outstanding supplier balances.
- Inputs: date range.
- Output: vendor_id, outstanding_payables.

W — Weekly Sales Trend
- Purpose: Sales trend over rolling week(s).
- Inputs: week range.
- Output: week_start, revenue, invoices.

X — eXception & Error Log
- Purpose: App-level errors/exceptions (useful for ops).
- Inputs: date range.
- Output: timestamp, component, error_message, stack_trace (restricted access).

Y — Yearly Summary / Fiscal Year Rollup
- Purpose: Annual totals for revenue, tax, net revenue.
- Inputs: fiscal year.
- Output: year, revenue, tax, net.

Z — ZIP / Postal Code Breakdown
- Purpose: Sales grouped by postal code for demographic analysis.
- Inputs: date range.
- Output: postal_code, revenue, customers_count.

---

## Implementation notes

- Report SQL examples above are intentionally simplified. Production queries should use parameterized prepared statements and proper joins and aggregation.
- Create a `resources/reports/` folder. When generating reports from the UI or server, write CSV/JSON/PDF files to that folder and present a download link in the UI.
- For scheduled reports, integrate a lightweight scheduler (e.g., `java.util.concurrent.ScheduledExecutorService` in `InvoiceServer`) or an external scheduler (Windows Task Scheduler / cron) that calls a report generator.
- For PDF output, you can re-enable iText usage by adding `lib/itextpdf.jar` to the classpath and replacing the current text-based `PDFGenerator` with the iText-based implementation already in the code (be mindful of iText license if using later iText versions).
- Access control: restrict sensitive reports (tax returns, exceptions) to authorized roles only.

## How to add a new report

1. Decide the data source (which tables/views).
2. Add a method in `invoicegenerator.service` (e.g., `ReportService.java`) to execute the query and transform results into DTOs.
3. Add an endpoint or a UI action in `InvoiceUI` to call the service and either stream the CSV/PDF to disk or to the UI.
4. Add unit tests for the SQL logic and integration tests if possible.

## Where to look in this repo

- Java sources: `src/invoicegenerator/` (models, database, service, ui, util, server, client)
- DB connection settings: `src/invoicegenerator/database/DBConnection.java` (switch between SQLite and MySQL here)
- Report templates: `resources/invoice_template.html` (HTML invoice template)
- Output folder suggestion: `resources/reports/`

## Contact & Next steps

If you'd like, I can:
- Implement a `ReportService` class and a few example reports (e.g., Daily Sales, Outstanding Invoices, Invoice Detail).
- Add a UI menu to generate the most-used reports and save to `resources/reports/`.
- Add scheduled report support to `InvoiceServer`.

Please tell me which reports (A–Z) you want implemented first or if you want me to create the `resources/reports/` folder and wire a simple CSV reporter for `Daily Sales`, `Unpaid Invoices`, and `Invoice Detail` now.
