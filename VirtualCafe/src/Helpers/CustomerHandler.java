package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class CustomerHandler implements Runnable {

    private final Socket socket;
    private BaristaActions barista;

    // This is the constructor for the CustomerHandler class. It takes in a socket and a barista actions object.
    public CustomerHandler(Socket socket, BaristaActions barista) {
        this.socket = socket;
        this.barista = barista;
    }

    @Override
    public void run() {
        String customerName = null;

        // Creating a try-with-resources block. This means that the resources (scanner and writer) will
        // be closed automatically when the try block ends.
        try (
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            try {
                // Reading customer name from the Scanner and sending a success message back to the customer.
                customerName = scanner.nextLine();
                System.out.println("Customer " + customerName + " has connected");

                while(true) {
                    // Scanning for the next line from the CustomerActions class.
                    String command = scanner.nextLine();
                    String[] substrings = command.split(" ");
                    
                    switch (substrings[0].toLowerCase()) {
                        // "ORDER" command from the CustomerActions class.
                        case "order":
                            barista.createOrder(customerName, Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]), socket);
                            break;
                        // "STATUS" command from the CustomerActions class.
                        case "status":
                            List<String> tempList = barista.orderStatus(customerName);
                            for (String s : tempList) {
                                writer.println(s);
                            }
                            break;
                        // "EXIT" command from the CustomerActions class.
                        case "exit":
                            barista.exitCommand(customerName);
                            socket.close();
                            return;
                    }
                }
            } catch (Exception e) {
                writer.println("ERROR " + e.getMessage());
                socket.close();
            }
        } catch (Exception e) {
        } finally { System.out.println("Customer " + customerName + " has left the cafe."); }
    }
}
