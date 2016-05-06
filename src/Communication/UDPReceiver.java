package Communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by erickchandra on 5/5/16.
 */
public class UDPReceiver extends Thread {
    // Attributes
    DatagramSocket datagramSocket;
    UDPThreadListener udpThreadListener;

    // Constructor
    public UDPReceiver(DatagramSocket datagramSocket, UDPThreadListener udpThreadListener) {
        this.datagramSocket = datagramSocket;
        this.udpThreadListener = udpThreadListener;
    }

    // Getter


    // Setter


    // Methods
    public void run() {
        startListen();
    }

    public void startListen() {
        try {
            while (true) {
                byte[] receivedDataBuffer = new byte[65507];
                DatagramPacket datagramPacket = new DatagramPacket(receivedDataBuffer, receivedDataBuffer.length);
                printlnConsoleLog("Listening for request and response through " + datagramSocket.getLocalAddress() + " with port " + datagramSocket.getLocalPort());
                datagramSocket.receive(datagramPacket);
                udpThreadListener.onUDPDataReceived(new String(datagramPacket.getData()), new String(datagramPacket.getAddress().toString()), new Integer(datagramPacket.getPort()));
                printlnConsoleLog("Received: " + new String(datagramPacket.getData()) + " from " + datagramPacket.getAddress().toString() + " with port " + datagramPacket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| UDPRECEIVER: " + string);
    }
}
