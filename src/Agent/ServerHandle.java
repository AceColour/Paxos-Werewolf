package Agent;

import java.net.Socket;
import java.util.HashSet;

/**
 * Created by erickchandra on 5/5/16.
 */
public class ServerHandle extends Thread {
    // Attributes
    Socket clientSocket;
    public static HashSet<ServerHandle> serverHandleHashSet;

    // Constructor
    public ServerHandle(Socket clientSocket) {
        this.clientSocket = clientSocket;
        if (serverHandleHashSet == null) {
            serverHandleHashSet = new HashSet<>();
            printlnConsoleLog("New HashSet of ServerHandle has been created.");
        }
        serverHandleHashSet.add(this);
        printlnConsoleLog("ServerHandle has been added into HashSet.");
    }

    // Getter


    // Setter


    // Methods
    public void printlnConsoleLog(String string) {
        System.out.println("|| SERVERHANDLE: " + string);
    }
}
