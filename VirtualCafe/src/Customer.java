import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import Helpers.CustomerActions;

public class Customer{
    public static void main(String[] args) {
        System.out.println("Welcome to the cafe!\nEnter your name: ");
        try {
            Scanner input = new Scanner(System.in);
            String customerName = input.nextLine();

            try (CustomerActions customer = new CustomerActions(customerName)) {
                System.out.println("Welcome, " + customerName + "!");

                while(true) {
                    System.out.println("What would you like to do? (order, pay, list, exit)");
                    String command = input.nextLine();
                    String[] substrings = command.split(" ");
                    switch (substrings[0].toLowerCase()) {
                        case "order":
                            if (substrings.length == 2) {
                                int numDrink = Integer.parseInt(substrings[1]);
                                String drinkType = substrings[2];

                                if (drinkType.toLowerCase().compareTo("coffee") == 0) {
                                    customer.createOrder(numDrink, 0);

                                } else if (drinkType.toLowerCase().compareTo("tea") == 0) {
                                    customer.createOrder(0, numDrink);

                                } else {
                                    System.out.println("Incorrect drink type or number format");
                                }
                            }
                            else if (substrings.length > 2) {
                                int numCoffee = 0;
                                int numTea = 0;
                                for (int i = 1; i < substrings.length; i++) {
                                    if (substrings[i].toLowerCase().compareTo("coffee") == 0) {
                                        numCoffee = Integer.parseInt(substrings[i-1]);

                                    } else if (substrings[i].toLowerCase().compareTo("tea") == 0) {
                                        numTea = Integer.parseInt(substrings[i-1]);
                                    }
                                }
                                if (numCoffee > 0 || numTea > 0) {
                                    customer.createOrder(numCoffee, numTea);
                                } else {
                                    System.out.println("Incorrect drink type or number format");
                                }
                            }
                            break;
                        case "status":
                            for (String status : customer.getOrderStatus()) System.out.println(status);
                            break;
                        case "debug":
                            if (substrings[1].toLowerCase().compareTo("all") == 0) {
                                for (String order : customer.DEBUG_ALL_ORDERS()) {
                                    System.out.println(order);
                                }
                            }
                            else if (substrings[1].toLowerCase().compareTo("trayed") == 0) {
                                for (String drink : customer.DEBUG_trayed()) {
                                    System.out.println(drink);
                                }
                            }
                            else if (substrings[1].toLowerCase().compareTo("waiting") == 0) {
                                for (String drink : customer.DEBUG_waiting()) {
                                    System.out.println(drink);
                                }
                            }
                            else if (substrings[1].toLowerCase().compareTo("brewing") == 0) {
                                for (String drink : customer.DEBUG_brewing()) {
                                    System.out.println(drink);
                                }
                            }
                            else {
                                System.out.println("Invalid debug command");
                            }
                            
                            break;
                        case "exit":
                            customer.exit();
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invalid command");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
