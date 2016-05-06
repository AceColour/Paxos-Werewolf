package Communication;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

/**
 * Created by erickchandra on 5/5/16.
 */
public class UDPSender extends Thread {
    // Attributes
    DatagramSocket datagramSocket;

    // Getter


    // Setter


    // Methods
    public UDPSender(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void sendMessage(String message, String toIpAddress, Integer toPortNumber) {
        try {
            byte[] sendDataBuffer = new byte[65507];
            sendDataBuffer = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(sendDataBuffer, sendDataBuffer.length, InetAddress.getByName(toIpAddress), toPortNumber);
            datagramSocket.send(datagramPacket);
            printlnConsoleLog("Sent message: " + message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| UDPSENDER: " + string);
    }
}
