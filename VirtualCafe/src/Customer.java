import java.util.Scanner;

import Helpers.CustomerActions;

public class Customer{
    public static void main(String[] args) {
        System.out.println("Welcome to the cafe!\nEnter your name: ");
        try {

            // Taking the input from the user and storing it in the variable customerName.
            Scanner input = new Scanner(System.in);
            String customerName = input.nextLine();

            // Creating a new instance of the CustomerActions class and passing the customerName
            // variable to the constructor.
            try (CustomerActions customer = new CustomerActions(customerName)) {
                System.out.println("Welcome, " + customerName + "!");

                while(true) {
                    System.out.println("Choose from the following: order (eg.: order 1 tea and 3 coffee), status, exit");

                    String command = input.nextLine();
                    String[] substrings = command.split(" "); // Received input is split into substrings.

                    switch (substrings[0].toLowerCase()) {
                        // Order command from the user
                        case "order":
                            // Case where only one type of drink was ordered by the customer.
                            if (substrings.length == 2) {
                                int numDrink = Integer.parseInt(substrings[1]);
                                String drinkType = substrings[2];

                                if (drinkType.toLowerCase().compareTo("coffee") == 0) customer.createOrder(numDrink, 0);
                                else if (drinkType.toLowerCase().compareTo("tea") == 0) customer.createOrder(0, numDrink);
                                else System.out.println("Incorrect drink type or number format");
                            }
                            // Case where two drink types were ordered (both coffee and tea).
                            else if (substrings.length > 2) {
                                int numCoffee = 0;
                                int numTea = 0;
                                // The loop is checking if the substring is coffee or tea and
                                // if it is, it is setting the number of coffee or tea to the previous
                                // index, as the number of drinks comes before the drink type.
                                for (int i = 1; i < substrings.length; i++) {
                                    if (substrings[i].toLowerCase().compareTo("coffee") == 0) {
                                        numCoffee = Integer.parseInt(substrings[i-1]);
                                    } else if (substrings[i].toLowerCase().compareTo("tea") == 0) {
                                        numTea = Integer.parseInt(substrings[i-1]);
                                    }
                                }
                                if (numCoffee > 0 || numTea > 0) customer.createOrder(numCoffee, numTea);
                                else System.out.println("Incorrect drink type or number format");
                            }
                            break;
                        // Status command from the user
                        case "status":
                            // Receiving String array from the CustomerActions class and printing it out.
                            for (String status : customer.getOrderStatus()) System.out.println(status);
                            break;
                        // Exit command from the user
                        case "exit":
                            customer.exit(); // Calling the exit method from the CustomerActions class.
                            System.exit(0); // Exiting the program.
                            break;
                        /// Default case is an invalid command
                        default:
                            System.out.println("Invalid command");
                            break;
                    }
                }
            }
        } catch (Exception e) { System.out.println(e.getMessage()); }
    }
}
