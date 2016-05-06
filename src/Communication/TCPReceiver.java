package Communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by erickchandra on 5/5/16.
 */
public class TCPReceiver extends Thread {
    // Attributes
    Socket socket;
    TCPThreadListener tcpThreadListener;

    // Constructor
    public TCPReceiver(Socket socket, TCPThreadListener tcpThreadListener) {
        this.socket = socket;
        this.tcpThreadListener = tcpThreadListener;
    }

    // Getter


    // Setter


    // Methods
    public void run() {
        printlnConsoleLog("Thread is running.");
        startListen();
    }

    public void startListen() {
        try {
            while (true) {
                printlnConsoleLog("Listening for request and response through " + socket.getLocalAddress() + " with port " + socket.getPort());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                tcpThreadListener.onTCPDataReceived(dataInputStream.readUTF());
                printlnConsoleLog("Received: " + dataInputStream.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| TCPRECEIVER: " + string);
    }
}
