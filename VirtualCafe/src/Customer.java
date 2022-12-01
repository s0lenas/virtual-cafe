import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Customer {

    private Socket socket;
    private Scanner user;
    private Scanner reader;
    private PrintWriter writer;
    private String customerName;

    // This is the constructor for the Customer class. It takes in a socket and a scanner,
    // instantiating them and a reader and writer
    public Customer(Socket socket, String customerName) {
        try {
            this.socket = socket;
            this.customerName = customerName;
            this.reader = new Scanner(socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) { closeAll(socket, reader, writer); }
    }

    // Infinite loop for the customer to send commands to the server.
    public void sendCommmand() {
        try {
            writer.println(customerName); // Sending the customer name to the server.

            System.out.println("Enter a command (order (eg.: 'order 1 coffee and 2 tea'), status, exit): ");
            user = new Scanner(System.in);
            while (socket.isConnected()) {
                String command = user.nextLine(); // Scanning for the next line from the user.
                String[] substrings = command.split(" "); // Splitting the command into substrings.

                // Checking first substring for the command.
                switch(substrings[0].toLowerCase()) {
                    case "order":
                        // Case where only one drink type is ordered.
                        if (substrings.length == 2) {
                            int numDrink = Integer.parseInt(substrings[1]);
                            String drinkType = substrings[2];

                            if (drinkType.toLowerCase().compareTo("coffee") == 0) createOrder(numDrink, 0); // Sends command to customer handler
                            else if (drinkType.toLowerCase().compareTo("tea") == 0) createOrder(0, numDrink);
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
                            if (numCoffee > 0 || numTea > 0) createOrder(numCoffee, numTea);
                            else System.out.println("Incorrect drink type or number format");
                        }
                        break;
                    // Status command from the user
                    case "status":
                        writer.println("STATUS"); // Sends status command to customer handler
                        break;
                    // Exit command from the user
                    case "exit":
                        writer.println("EXIT"); // Sends exit command to customer handler
                        System.exit(0); // Exiting the program.
                        break;
                    /// Default case is an invalid command
                    default:
                        System.out.println("Invalid command");
                        break;
                    }
            }
        } catch (Exception e) {
            closeAll(socket, reader, writer);
        }
    }

    // Method for creating an order with specified drink and quantity.
    // - Caught in the CustomerHandler class.
    public void createOrder(int numCoffee, int numTea) { writer.println("ORDER " + numCoffee + " " + numTea); }


    /**
     * Method for reading any kind of server output, including non-command initiated messages.
     *  - Creates new thread that runs a function that reads the next line from the socket and prints it to the console
     */
    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        String message = reader.nextLine();
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    closeAll(socket, reader, writer);
                }
            }
        }).start();
    }

    // Method for closing all the streams and sockets.
    public void closeAll(Socket socket, Scanner reader, PrintWriter writer) {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            // Asking for the customer name
            System.out.println("Enter your name: ");
            String customerName = scanner.nextLine();

            // Creating a new socket and connecting to the server with Customer class
            Socket socket = new Socket("localhost", 8888);
            Customer customer = new Customer(socket, customerName);

            // Starting sender loop and receiver thread
            customer.receiveMessage();
            customer.sendCommmand();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
}
