package Agent;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by erickchandra on 5/4/16.
 */
public class Client {
    // Attributes
    Socket ccSocket; // Client-Client Socket: UDP
    Socket csSocket; // Client-Server Socket: TCP
    String serverIpAddress;
    int serverPortNumber;
    int clientPortNumber;

    // Constructor
    public Client(String serverIpAddress, int serverPortNumber, int clientPortNumber) {
        this.serverIpAddress = serverIpAddress;
        this.serverPortNumber = serverPortNumber;
        this.clientPortNumber = clientPortNumber;
    }

    // Getter


    // Setter


    // Methods
    public void run() {
        startClient();
    }

    public void startClient() {
        try {
            printlnConsoleLog("Connecting to " + this.serverIpAddress + " on port " + this.serverPortNumber);
            csSocket = new Socket(this.serverIpAddress, this.serverPortNumber);
            printlnConsoleLog("Just connected to " + csSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| CLIENT: " + string);
    }
}
