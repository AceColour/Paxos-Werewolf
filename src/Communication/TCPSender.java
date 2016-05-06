package Communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by erickchandra on 5/5/16.
 */
public class TCPSender extends Thread {
    // Attributes
    Socket socket;

    // Getter


    // Setter


    // Methods
    public TCPSender(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String message) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            printlnConsoleLog("Sent message: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| TCPSENDER: " + string);
    }
}
