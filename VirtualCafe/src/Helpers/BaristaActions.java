package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// NOTES: takes 90s to complete order of 4 coffees and 4 teas
// NOTES: can maybe add time it took to complete specific order

public class BaristaActions implements Runnable {

    private int stateChecker = 0;
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
    
    public void createOrder(String customerName, int numCoffee, int numTea) {

        // If order by the customer already exists, then just add drinks to the existing order
        // otherwise create a new order
        Order order = new Order(customerName, numCoffee, numTea);
        if (orders.containsKey(customerName)) {
            if (numCoffee > 0) {
                orders.get(customerName).addDrink(numCoffee, DrinkType.Coffee);
                for (int i = 0; i < numCoffee; i++) waitingArea.add(new Drink(order, DrinkType.Coffee));

                System.out.println("Added " + numCoffee + " coffee(s) to " + customerName + "'s order");
            }
            if (numTea > 0) {
                orders.get(customerName).addDrink(numTea, DrinkType.Tea);
                for (int i = 0; i < numTea; i++) waitingArea.add(new Drink(order, DrinkType.Tea));

                System.out.println("Added " + numTea + " tea(s) to " + customerName + "'s order");
            }
            cafeStatusUpdate();
        } else {
            System.out.println("Order received for " + customerName + " (" + order.orderMessage());
            orders.put(customerName, order);
            customers.put(customerName, order);
            cafeStatusUpdate();
            waitingArea.addAll(order.getDrinks());
        }
    }

    // Getting order status for a specified customer
    public String[] orderStatus(String customerName) {
        String[] lines = new String[4];
        // In case of an exception, returning a single line with a message to user.
        try { lines = orders.get(customerName).toString().split("\n");
        } catch (Exception e) { 
            String[] error = {"No orders found for " + customerName};
            return error; 
        }
        return lines;
    }

    public void exitCommand(String customerName) {
        orders.remove(customerName);
        customers.remove(customerName);
        System.out.println("Order for " + customerName + " has been removed");
        for (int i = 0; i < waitingArea.size(); i++) {
            if (waitingArea.get(i).getDrinkOwner().equals(customerName)) {
                waitingArea.remove(i);
                i--;
            }
        }
        for (int i = 0; i < brewingArea.size(); i++) {
            if (brewingArea.get(i).getDrinkOwner().equals(customerName)) {
                brewingArea.remove(i);
                i--;
            }
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
        for (int i = 0; i < trayArea.size(); i++) {
            if (trayArea.get(i).getDrinkOwner().equals(customerName)) {
                trayArea.remove(i);
                i--;
            }
        }
        cafeStatusUpdate();
    }

    private void cafeStatusUpdate() {
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

    public Order getOrder(Order order) {
        return order;
    }

    public List<Drink> DEBUG_trayed() {
        List<Drink> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            orderList.addAll(order.getTray());
        }
        return orderList;
    }

    public List<Drink> DEBUG_waiting() {
        return waitingArea;
    }

    public List<Drink> DEBUG_brewing() {
        return brewingArea;
    }

    @Override
    public void run() {
        while(true) {
            // If there are drinks in the waiting area, start brewing in respective tea or coffee areas
            synchronized(this){
                if (waitingArea.size() > 0) {
                    for(int d = 0; d < waitingArea.size(); d++) {
                        Drink drink = waitingArea.get(d);
                        if (drink.getDrinkType() == DrinkType.Coffee) {
                            // Previously used enhanced for loop, but causes concurrent modification exception due to internal iterator
                            for (int i = 0; i < brewingCoffee.length; i++) {
                                if (brewingCoffee[i] == null || !brewingCoffee[i].isAlive()) {
                                    brewingCoffee[i] = new Thread(drink);
                                    brewingCoffee[i].start();
                                    brewingCoffee[i].setName(drink.getDrinkOwner());
                                    brewingArea.add(drink);
                                    //System.out.println("Brewing " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                                    waitingArea.remove(drink);
                                    cafeStatusUpdate();
                                    break;
                                }
                            }
                            continue;
                        } else if (drink.getDrinkType() == DrinkType.Tea) {
                            for (int i = 0; i < brewingTea.length; i++) {
                                if (brewingTea[i] == null || !brewingTea[i].isAlive()) {
                                    brewingTea[i] = new Thread(drink);
                                    brewingTea[i].start();
                                    brewingTea[i].setName(drink.getDrinkOwner());
                                    brewingArea.add(drink);
                                    //System.out.println("Brewing " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                                    waitingArea.remove(drink);
                                    cafeStatusUpdate();
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                }
                // Check if any of the brewing threads are done; if so, move the drink to the tray area and remove from brewing area
                if (brewingArea.size() > 0) {
                    for (int i = 0; i < brewingArea.size(); i++) {
                        Drink drink = brewingArea.get(i);
                        if (drink.getDrinkStatus() == DrinkStatus.Trayed) {
                            trayArea.add(drink);
                            //System.out.println("Trayed " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                            brewingArea.remove(drink);
                            cafeStatusUpdate();
                        }
                    }
                }
                // Check trayed area for fully completed orders. If a all drinks belonging to an order are trayed, then remove the order from the map and set completed to true
                if (trayArea.size() > 0) {
                    for (int o = 0; o < orders.size(); o++) {
                        Order order = (Order) orders.values().toArray()[o];
                        if (orders.get(order.getCustomerName()).isComplete()) {
                            for (int i = 0; i < trayArea.size(); i++) {
                                if (trayArea.get(i).getDrinkOwner().equals(order.getCustomerName())) {
                                    trayArea.remove(i);
                                    i--;
                                }
                            }
                            orders.remove(order.getCustomerName());
                            System.out.println("Order delivered to " + order.getCustomerName() + " (" + order.orderMessage());
                        }
                    }
                }
            }
        }
    }
}
