package invoicegenerator.client;

import java.io.*;
import java.net.*;
import invoicegenerator.model.*;

public class InvoiceClient {
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public InvoiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public void sendInvoice(Invoice invoice) throws IOException {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        out.writeObject(invoice);
        out.flush();
    }

    public Object receiveResponse() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public static void main(String[] args) {
        InvoiceClient client = new InvoiceClient("localhost", 5000);
        try {
            client.connect();
            // Example usage:
            // Create and send invoice
            // Handle response
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
}