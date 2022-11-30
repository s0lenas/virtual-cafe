package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerActions implements AutoCloseable {
    final int port = 8888;

    private final Scanner reader;
    private final PrintWriter writer;

    public CustomerActions(String customerName) throws Exception {
        Socket socket = new Socket("localhost", port);
        reader = new Scanner(socket.getInputStream());

        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(customerName);

        String line =  reader.nextLine();
        if (line.trim().compareToIgnoreCase("success") != 0) {
            throw new Exception(line);
        }
    }

    public void createOrder(int numCoffee, int numTea) {
        writer.println("ORDER " + numCoffee + " " + numTea);
    }

    public String[] getOrderStatus() {
        writer.println("STATUS");

        String line = reader.nextLine();
        int numLines = Integer.parseInt(line);

        String[] status = new String[numLines];

        for (int i = 0; i < status.length; i++) {
            status[i] = (reader.nextLine());
        }
        return status;
    }

    public List<String> DEBUG_ALL_ORDERS() {
        writer.println("DEBUG ALL");

        String line = reader.nextLine();
        int numOrders = Integer.parseInt(line);
        
        List<String> orderList = new ArrayList<String>();

        for(int i = 0; i < numOrders; i++) {
            line = reader.nextLine();
            orderList.add(line);
        }
        
        return orderList;
    }

    public List<String> DEBUG_trayed() {
        writer.println("DEBUG TRAYED");

        String line = reader.nextLine();
        int numOrders = Integer.parseInt(line);
        
        List<String> orderList = new ArrayList<String>();

        for(int i = 0; i < numOrders; i++) {
            line = reader.nextLine();
            orderList.add(line);
        }
        
        return orderList;
    }

    public List<String> DEBUG_waiting() {
        writer.println("DEBUG WAITING");

        String line = reader.nextLine();
        int numOrders = Integer.parseInt(line);
        
        List<String> orderList = new ArrayList<String>();

        for(int i = 0; i < numOrders; i++) {
            line = reader.nextLine();
            orderList.add(line);
        }
        
        return orderList;
    }

    public List<String> DEBUG_brewing() {
        writer.println("DEBUG BREWING");

        String line = reader.nextLine();
        int numOrders = Integer.parseInt(line);
        
        List<String> orderList = new ArrayList<String>();

        for(int i = 0; i < numOrders; i++) {
            line = reader.nextLine();
            orderList.add(line);
        }
        
        return orderList;
    }

    public void exit() {
        writer.println("EXIT");
    }

    @Override
    public void close() throws Exception {
        writer.close();
        reader.close();
    }
}

