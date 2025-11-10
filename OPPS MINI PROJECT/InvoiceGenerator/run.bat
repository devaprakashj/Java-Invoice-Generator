@echo off
start "Invoice Server" java -cp "lib\*;src" invoicegenerator.server.InvoiceServer
timeout /t 2
start "Invoice Client" java -cp "lib\*;src" invoicegenerator.ui.InvoiceUI