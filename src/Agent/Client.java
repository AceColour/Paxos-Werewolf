package Agent;

import Communication.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by erickchandra on 5/4/16.
 */
public class Client implements TCPThreadListener, UDPThreadListener {
    // Attributes
    DatagramSocket ccSocket; // Client-Client Socket: UDP
    Socket csSocket; // Client-Server Socket: TCP
    String serverIpAddress;
    int serverPortNumber;
    int clientPortNumber;
    TCPReceiver tcpReceiver;
    TCPSender tcpSender;
    UDPReceiver udpReceiver;
    UDPSender udpSender;

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
            // Connecting TCP
            printlnConsoleLog("Connecting to " + this.serverIpAddress + " on port " + this.serverPortNumber);
            csSocket = new Socket(this.serverIpAddress, this.serverPortNumber);
            printlnConsoleLog("Just connected to " + csSocket.getRemoteSocketAddress());
            tcpReceiver = new TCPReceiver(csSocket, this);
            tcpReceiver.start();
            tcpSender = new TCPSender(csSocket);
            tcpSender.sendMessage("HELLO!");

            // Connecting UDP
            printlnConsoleLog("Creating UDP socket.");
            ccSocket = new DatagramSocket(this.clientPortNumber);
            printlnConsoleLog("UDP socket is just created.");
            udpReceiver = new UDPReceiver(ccSocket, this);
            udpReceiver.start();
            udpSender = new UDPSender(ccSocket);
            udpSender.sendMessage("HELLO!", "localhost", this.clientPortNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTCPDataReceived(String receivedString) {
        printlnConsoleLog("Received and caught from TCP: " + receivedString);
    }

    @Override
    public void onUDPDataReceived(String receivedString, String fromIpAddress, Integer fromPortNumber) {
        printlnConsoleLog("Received and caught from UDP: " + receivedString);
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| CLIENT: " + string);
    }
}
