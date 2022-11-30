package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CustomerActions implements AutoCloseable {
    final int port = 8888;

    private final Scanner reader;
    private final PrintWriter writer;

    // This is the constructor for the CustomerActions class. It creates a new socket and connects to
    // the server. It then creates a reader and writer for the socket. It then sends the customer name
    // to the server and waits for a response. If the response is not "success" it throws an exception.
    public CustomerActions(String customerName) throws Exception {
        Socket socket = new Socket("localhost", port);
        reader = new Scanner(socket.getInputStream());

        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(customerName);

        String line =  reader.nextLine();
        if (line.trim().compareToIgnoreCase("success") != 0) throw new Exception(line);
    }

    // Sends an "ORDER" command to the CustomerHandler class.
    public void createOrder(int numCoffee, int numTea) { writer.println("ORDER " + numCoffee + " " + numTea); }

    // Sends a "STATUS" command to the CustomerHandler class.
    public String[] getOrderStatus() {
        writer.println("STATUS");

        String line = reader.nextLine();
        int numLines = Integer.parseInt(line); // The first line is the number of lines to read.

        String[] status = new String[numLines]; // Creating an array to store the status.

        for (int i = 0; i < status.length; i++) status[i] = (reader.nextLine()); // Reading the status message line by line.

        return status;
    }

    // Sends an "EXIT" command to the CustomerHandler class.
    public void exit() { writer.println("EXIT"); }

    @Override
    public void close() throws Exception {
        writer.close();
        reader.close();
    }
}

