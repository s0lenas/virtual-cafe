import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Helpers.BaristaActions;
import Helpers.CustomerHandler;

public class Barista {
    private final static int port = 8888;
    private static final BaristaActions barista = new BaristaActions();

    public static void main(String[] args) {
        OpenCafe();
    }

    private static void OpenCafe() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            
            // Creating a new thread for the barista to run on.
            Thread baristaThread = new Thread(barista);
            baristaThread.start();

            System.out.println("Barista is ready to take orders");
            // This is the main loop of the server. It waits for a client to connect, and then creates
            // a new thread to handle the client.
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new CustomerHandler(socket, barista)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
