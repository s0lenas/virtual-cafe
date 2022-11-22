package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.List;

public class CustomerHandler implements Runnable {

    private final Socket socket;
    private BaristaActions barista;

    public CustomerHandler(Socket socket, BaristaActions barista) {
        // TODO Auto-generated constructor stub
        this.socket = socket;
        this.barista = barista;
    }

    @Override
    public void run() {
        String customerName = null;
        try (
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            try {
                customerName = scanner.nextLine();
                System.out.println("Customer " + customerName + " has connected");
                writer.println("success");

                while(true) {
                    String command = scanner.nextLine();
                    String[] substrings = command.split(" ");
                    switch (substrings[0].toLowerCase()) {
                        case "order":
                            writer.println(barista.createOrder(customerName, Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2])));
                            break;
                        case "status":
                            List<String> orderList = barista.getOrders(customerName);
                            if (orderList.size() == 0) {
                                writer.println("No orders found for " + customerName);
                            } else {
                                for (String order : orderList) {
                                    writer.println(order);
                                }
                            }
                            break;
                        case "debug":
                            if(substrings[1].toLowerCase().compareTo("all") == 0) {
                                List<String> allOrders = barista.DEBUG_ALL_ORDERS();
                                writer.println(allOrders.size());
                                if (allOrders.size() == 0) {
                                    writer.println("No orders found");
                                } else {
                                    for (String order : allOrders) {
                                        writer.println(order);
                                    }
                                }
                            }
                            else if(substrings[1].toLowerCase().compareTo("trayed") == 0) {
                                List<Drink> temp = barista.DEBUG_trayed();
                                writer.println(temp.size());
                                if (temp.size() == 0) {
                                    writer.println("No orders found");
                                } else {
                                    for (Drink drink : temp) {
                                        writer.println("Customer: " + drink.getDrinkOwner() + " Drink: " + drink.getDrinkType() + " Status: " + drink.getDrinkStatus());
                                    }
                                }
                            }
                            else if(substrings[1].toLowerCase().compareTo("waiting") == 0) {
                                List<Drink> temp = barista.DEBUG_waiting();
                                writer.println(temp.size());
                                if (temp.size() == 0) {
                                    writer.println("No orders found");
                                } else {
                                    for (Drink drink : temp) {
                                        writer.println("Customer: " + drink.getDrinkOwner() + " Drink: " + drink.getDrinkType() + " Status: " + drink.getDrinkStatus());
                                    }
                                }
                            }
                            else if(substrings[1].toLowerCase().compareTo("brewing") == 0) {
                                List<Drink> temp = barista.DEBUG_brewing();
                                writer.println(temp.size());
                                if (temp.size() == 0) {
                                    writer.println("No orders found");
                                } else {
                                    for (Drink drink : temp) {
                                        writer.println("Customer: " + drink.getDrinkOwner() + " Drink: " + drink.getDrinkType() + " Status: " + drink.getDrinkStatus());
                                    }
                                }
                            }
                            break;
                        case "exit":
                            // TODO: add exit functionality as per the assignment
                            writer.println("Goodbye");
                            return;
                    }
                }
            } catch (Exception e) {
                writer.println("ERROR " + e.getMessage());
                socket.close();
            }
        } catch (Exception e) {
        } finally {
            System.out.println("Customer " + customerName + " has left the cafe.");
        }
        
    }
}
