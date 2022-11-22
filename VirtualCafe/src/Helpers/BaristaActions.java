package Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// NOTES: can use override toString for drinks or orders to print them in readable format
// NOTES: takes 90s to complete order of 4 coffees and 4 teas

public class BaristaActions implements Runnable{

    private Map<String, Order> orders = new TreeMap<>();

    // Waiting area
    private List<Drink> waitingArea = new ArrayList<>();

    // Brewing area
    private Thread[] brewingCoffee = new Thread[2];
    private Thread[] brewingTea = new Thread[2];
    private List<Drink> brewingArea = new ArrayList<>();

    // Tray area
    private List<Drink> trayArea = new ArrayList<>();

    private List<Drink> drinks = new ArrayList<>();

    
    public String createOrder(String customerName, int numCoffee, int numTea) {

        // If order by the customer already exists, then just add drinks to the existing order
        // otherwise create a new order

        // TODO: finish functionality for cases where order already exists
        if (orders.containsKey(customerName)) {
            if (numCoffee > 0) orders.get(customerName).addDrink(numCoffee, DrinkType.Coffee);
            if (numTea > 0) orders.get(customerName).addDrink(numTea, DrinkType.Tea);
        } else {
            Order order = new Order(customerName, numCoffee, numTea);
            orders.put(customerName, order);
            waitingArea.addAll(order.getDrinks());
            drinks.addAll(order.getDrinks());
        }
        
        //return "Order " + numCoffee + " coffee " + numTea + " tea created for " + customerName;
        return "Order created";
        
    }

    public List<String> getOrders(String customerName) {
        List<String> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerName().compareTo(customerName) == 0) {

            }
        }
        return orderList;
    }

    public List<String> DEBUG_ALL_ORDERS() {
        List<String> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            //orderList.addAll(order.getDrinks())
        }
        return orderList;
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
            synchronized (this) {
                if (waitingArea.size() > 0) {
                    //System.out.println("bum");
                    for(int d = 0; d < waitingArea.size(); d++) {
                        Drink drink = waitingArea.get(d);
                        if (drink.getDrinkType() == DrinkType.Coffee) {
                            // Previously used enhanced for loop, but causes concurrent modification exception due to internal iterator
                            for (int i = 0; i < brewingCoffee.length; i++) {
                                if (brewingCoffee[i] == null || !brewingCoffee[i].isAlive()) {
                                    brewingCoffee[i] = new Thread(drink);
                                    brewingCoffee[i].start();
                                    brewingArea.add(drink);
                                    System.out.println("Brewing " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                                    waitingArea.remove(drink);
                                    break;
                                }
                            }
                            continue;
                        } else if (drink.getDrinkType() == DrinkType.Tea) {
                            for (int i = 0; i < brewingTea.length; i++) {
                                if (brewingTea[i] == null || !brewingTea[i].isAlive()) {
                                    brewingTea[i] = new Thread(drink);
                                    brewingTea[i].start();
                                    brewingArea.add(drink);
                                    System.out.println("Brewing " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                                    waitingArea.remove(drink);
                                    break;
                                }
                            }
                            continue;
                        }
                    }
                }
                // Check if any of the brewing threads are done; if so, move the drink to the tray area and remove from brewing area
                if (brewingArea.size() > 0) {
                    //System.out.println("Brewing area: " + brewingArea.size());
                    for (int i = 0; i < brewingArea.size(); i++) {
                        Drink drink = brewingArea.get(i);
                        if (drink.getDrinkStatus() == DrinkStatus.Trayed) {
                            trayArea.add(drink);
                            System.out.println("Trayed " + drink.getDrinkType() + " for " + drink.getOrder().getCustomerName());
                            brewingArea.remove(drink);
                        }
                    }
                }
                // Check trayed area for fully completed orders. If a all drinks belonging to an order are trayed, then remove the order from the map and set completed to true
                if (trayArea.size() > 0) {
                    for (int i = 0; i < trayArea.size(); i++) {
                        Drink drink = trayArea.get(i);
                        for (int j = 0; j < orders.size(); j++) {
                            Order order = (Order) orders.values().toArray()[j];
                            if (order.getCustomerName().compareTo(drink.getDrinkOwner()) == 0) {
                                order.addToTray(drink);
                                order.getDrinks().remove(drink);
                                trayArea.remove(drink);
                            }
                            if (order.getDrinks().size() == 0) {
                                System.out.println("Order completed");
                                order.setComplete(true);
                                orders.remove(order.getCustomerName());
                                System.out.println("Order map size: " + orders.size());
                            }
                        }
                    }
                }
            }
        }
    }
}
