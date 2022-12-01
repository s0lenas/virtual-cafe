package Helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BaristaActions implements Runnable {

    // Active customers in cafe
    private Map<String, Order> customers = new TreeMap<>(); // For finding current customers in cafe. Values only removed when customer leaves cafe

    // Active orders
    private Map<String, Order> orders = new TreeMap<>();

    // Waiting area
    private List<Drink> waitingArea = new ArrayList<>();

    // Brewing area
    private Thread[] brewingCoffee = new Thread[2];
    private Thread[] brewingTea = new Thread[2];
    private List<Drink> brewingArea = new ArrayList<>();

    // Tray area
    private List<Drink> trayArea = new ArrayList<>();
    
    // Method for creating a new order
    public synchronized void createOrder(String customerName, int numCoffee, int numTea, Socket socket) {
        // If order by the customer already exists, then just add drinks to the existing order
        // otherwise create a new order with specified customer name, number of drinks for each type, and the customer socket.
        if (orders.containsKey(customerName)) {
            if (numCoffee > 0) {
                for (int i = 0; i < numCoffee; i++) {
                    Drink coffee = new Drink(orders.get(customerName), DrinkType.Coffee);
                    orders.get(customerName).getDrinks().add(coffee);
                    waitingArea.add(coffee);
                }
                System.out.println("Added " + numCoffee + " coffee(s) to " + customerName + "'s order");
            }
            if (numTea > 0) {
                for (int i = 0; i < numTea; i++) {
                    Drink tea = new Drink(orders.get(customerName), DrinkType.Tea);
                    orders.get(customerName).getDrinks().add(tea);
                    waitingArea.add(tea);
                }
                System.out.println("Added " + numTea + " tea(s) to " + customerName + "'s order");
            }
            cafeStatusUpdate();
        } else {
            Order order = new Order(customerName, numCoffee, numTea, socket);
            orders.put(customerName, order);
            customers.put(customerName, order);
            cafeStatusUpdate();
            System.out.println("Order received for " + customerName + " (" + order.orderMessage());
            waitingArea.addAll(order.getDrinks());
        }
    }

    // Getting order status for a specified customer
    public synchronized List<String> orderStatus(String customerName) {
        List<String> linesList = new ArrayList<>();
        // In case of an exception, returning a single line with a message to user.
        try { 
            linesList = Arrays.asList(orders.get(customerName).toString().split("\n"));
        } catch (Exception e) { 
            linesList.add("No orders found for " + customerName);
            return linesList;
        }
        return linesList;
    }

    // Method for completely removing a customer from the cafe
    public synchronized void exitCommand(String customerName) {
        orders.remove(customerName);
        customers.remove(customerName);
        // Checking each area for customer related items and removing them
        for (int i = 0; i < waitingArea.size(); i++) {
            if (waitingArea.get(i).getDrinkOwner().equals(customerName)) { waitingArea.remove(i); i--; }
        }
        for (int i = 0; i < brewingArea.size(); i++) {
            if (brewingArea.get(i).getDrinkOwner().equals(customerName)) { brewingArea.remove(i); i--; }
        }
        for (int i = 0; i < 2; i++) {
            if (brewingCoffee[i] != null && brewingCoffee[i].getName().equals(customerName)) {
                brewingCoffee[i].interrupt();
                brewingCoffee[i] = null;
            }
            if (brewingTea[i] != null && brewingTea[i].getName().equals(customerName)) {
                brewingTea[i].interrupt();
                brewingTea[i] = null;
            }
        }
        for (int i = 0; i < trayArea.size(); i++) { trayArea.remove(i); i--; }

        if (customers.isEmpty()) { cafeStatusUpdate(); }
        System.out.println("Order for " + customerName + " has been removed");
    }

    // Method for printing out the current status of the cafe to server terminal
    // - Counts drinks of each type in each area and appends them to string
    private synchronized void cafeStatusUpdate() {
        System.out.println("\nNumber of clients in cafe: " + customers.size());
        System.out.println("Number of clients waiting for orders: " + orders.size());

        int numCoffee = 0;
        int numTea = 0;

        String status = "Waiting area: ";
        for (Drink drink : waitingArea) {
            if (drink.getDrinkType() == DrinkType.Coffee) numCoffee++;
            else numTea++;
        }
        status += numCoffee + " coffee(s) and " + numTea + " tea(s)";

        numCoffee = numTea = 0;
        for (Drink drink : brewingArea) {
            if (drink.getDrinkType() == DrinkType.Coffee) numCoffee++;
            else numTea++;
        }
        status += "\nBrewing area: " + numCoffee + " coffee(s) and " + numTea + " tea(s)";

        numCoffee = numTea = 0;
        for (Drink drink : trayArea) {
            if (drink.getDrinkType() == DrinkType.Coffee) numCoffee++;
            else numTea++;
        }
        status += "\nTray area: " + numCoffee + " coffee(s) and " + numTea + " tea(s)";

        System.out.println(status + "\n");
    }

    // BONUS: Method for printing a message to the customer
    private synchronized void sendMsg(String msg, Order order) {
        String message = "SERVER: " + msg;
        try {
            // Sending message to socket contained within the order
            PrintWriter out = new PrintWriter(order.getSocket().getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            synchronized(this){
                // Waiting and brewing area handler:
                // - If there are any drinks in the waiting area, then check if there are any free brewing threads
                // - If there are free brewing threads, then start brewing the first drink in the waiting area
                // - If there are no free brewing threads, then do nothing
                if (waitingArea.size() > 0) {
                    for(int d = 0; d < waitingArea.size(); d++) {
                        Drink drink = waitingArea.get(d);
                        // Coffee brewer
                        if (drink.getDrinkType() == DrinkType.Coffee) {
                            for (int i = 0; i < brewingCoffee.length; i++) { // Previously used enhanced "for loop", but causes concurrent modification exception due to internal iterator
                                if (brewingCoffee[i] == null || !brewingCoffee[i].isAlive()) {
                                    brewingCoffee[i] = new Thread(drink);
                                    brewingCoffee[i].start();
                                    brewingCoffee[i].setName(drink.getDrinkOwner());
                                    brewingArea.add(drink);
                                    waitingArea.remove(drink);
                                    d--;
                                    cafeStatusUpdate();
                                    break;
                                }
                            }
                            continue;
                        // Tea brewer
                        } else if (drink.getDrinkType() == DrinkType.Tea) {
                            for (int i = 0; i < brewingTea.length; i++) {
                                if (brewingTea[i] == null || !brewingTea[i].isAlive()) {
                                    brewingTea[i] = new Thread(drink);
                                    brewingTea[i].start();
                                    brewingTea[i].setName(drink.getDrinkOwner());
                                    brewingArea.add(drink);
                                    waitingArea.remove(drink);
                                    d--;
                                    cafeStatusUpdate();
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                }
                // Tray area handler:
                // - Check for drinks in brewing area that are finished brewing
                // - If there are any finished drinks, then add them to the tray area and remove them from brewing area
                if (brewingArea.size() > 0) {
                    for (int i = 0; i < brewingArea.size(); i++) {
                        Drink drink = brewingArea.get(i);
                        if (drink.getDrinkStatus() == DrinkStatus.Trayed) {
                            trayArea.add(drink);
                            brewingArea.remove(drink);
                            i--;
                            cafeStatusUpdate();
                        }
                    }
                }

                // Order completion handler:
                // - Iterate through drinks in the tray area. To prevent concurrent modification exception, instatiating and order from data within the drink.
                // - If the order is finished, for code reusability, call the exitCommand method to remove the customer from the cafe, but add the order back to customers map
                // - BONUS: Send a message to the customer to let them know their order is ready
                if (trayArea.size() > 0) {
                    for (int i = 0; i < trayArea.size(); i++) {
                        Order order = orders.get(trayArea.get(i).getDrinkOwner());
                        if (order.isComplete()) {
                            sendMsg("Your order is complete!", order);
                            exitCommand(order.getCustomerName());
                            customers.put(order.getCustomerName(), order);
                            cafeStatusUpdate();
                            System.out.println("Order delivered to " + order.getCustomerName() + " (" + order.orderMessage());
                            break;
                        }
                    }
                }
            }
        }
    }
}
