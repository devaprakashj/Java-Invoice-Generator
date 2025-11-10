package invoicegenerator.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class InvoiceServer {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private boolean running;

    public InvoiceServer() {
        pool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Server started on port " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        pool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.err.println("Error setting up client handler: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Read request from client
                    Object request = in.readObject();
                    // Process request and send response
                    // This is where you would handle different types of requests
                    // like creating invoices, fetching customer data, etc.
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        InvoiceServer server = new InvoiceServer();
        server.start();
    }
}