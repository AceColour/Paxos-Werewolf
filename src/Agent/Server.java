package Agent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by erickchandra on 5/4/16.
 */
public class Server {
    // Attributes
    Integer serverPortNumber;
    ServerSocket serverSocket;

    // Constructor
    public Server(Integer serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
        try {
            serverSocket = new ServerSocket(this.serverPortNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter


    // Setter


    // Methods
    public void run() {
        startServer();
    }

    public void startServer() {
        while (true) {
            try {
                printlnConsoleLog("Waiting for client on port " + this.serverPortNumber);
                Socket socket = serverSocket.accept();
                printlnConsoleLog("Just connected to " + socket.getRemoteSocketAddress());

                ServerHandle serverHandle = new ServerHandle(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| SERVER: " + string);
    }
}
