package Agent;

import Communication.TCPReceiver;
import Communication.TCPThreadListener;
import Communication.UDPReceiver;
import Communication.UDPThreadListener;

import java.net.Socket;
import java.util.HashSet;

/**
 * Created by erickchandra on 5/5/16.
 */
public class ServerHandle extends Thread implements TCPThreadListener {
    // Attributes
    Socket clientSocket;
    public static HashSet<ServerHandle> serverHandleHashSet;
    TCPReceiver tcpReceiver;

    // Constructor
    public ServerHandle(Socket clientSocket) {
        this.clientSocket = clientSocket;
        if (serverHandleHashSet == null) {
            serverHandleHashSet = new HashSet<>();
            printlnConsoleLog("New HashSet of ServerHandle has been created.");
        }
        serverHandleHashSet.add(this);
        printlnConsoleLog("ServerHandle has been added into HashSet.");

        // Running TCP Receiver
        tcpReceiver = new TCPReceiver(clientSocket, this);
        tcpReceiver.start();
    }

    // Getter


    // Setter


    // Methods
    @Override
    public void onTCPDataReceived(String receivedString) {
        printlnConsoleLog("Received and caught from TCP: " + receivedString);
    }

    public void run() {

    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| SERVERHANDLE: " + string);
    }
}
