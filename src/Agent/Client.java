package Agent;

import CLI.ClientCLI;
import Communication.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.sql.rowset.serial.SerialJavaObject;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
    ClientCLI clientCLI;
    Boolean isLeave = false;

    Integer state = 0;
    /*
     * State number:
     * 0: Client has not joined Game
     * 1: Client has joined Game but not yet ready (permitted to leave)
     * 2: Client ready and wait Start Game command from server
     * 3: Game is running and waiting for Vote command from server
     * 4: Client is dead in the game
     * 5: Client is performing Paxos consensus
     * 6: Game over
     */

    // Constructor
    public Client(ClientCLI clientCLI, String serverIpAddress, int serverPortNumber, int clientPortNumber) {
        this.serverIpAddress = serverIpAddress;
        this.serverPortNumber = serverPortNumber;
        this.clientPortNumber = clientPortNumber;
        this.clientCLI = clientCLI;
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
//            tcpSender = new TCPSender(csSocket);
//            tcpSender.sendMessage("HELLO!");

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
        if (parseJSONStringToJSONObject(receivedString) == null) {
            printlnConsoleLog("Invalid string received.");
        }
        else {
            receiveAndExecute(new JSONObject(parseJSONStringToJSONObject(receivedString)));
        }
    }

    @Override
    public void onUDPDataReceived(String receivedString, String fromIpAddress, Integer fromPortNumber) {
        printlnConsoleLog("Received and caught from UDP: " + receivedString);
    }

    public void sendTcpMessage(String message) {
        tcpSender = new TCPSender(csSocket);
        tcpSender.sendMessage(message);
        try {
            tcpSender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendUdpMessage(String message, String toIpAddress, Integer toPortNumber) {
        udpSender = new UDPSender(ccSocket);
        udpSender.sendMessage(message, toIpAddress, toPortNumber);
        try {
            udpSender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| CLIENT: " + string);
    }

    public JSONObject parseJSONStringToJSONObject(String string) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject((JSONObject) jsonParser.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    public void receiveAndExecute(JSONObject receivedJsonObject) {
        switch (state) {
            case 0:
                if (receivedJsonObject.get("status").equals("ok")) {
                    state = 1;
                    clientCLI.askForReadyOrLeave();
                }
                else {
                    clientCLI.askForUsername();
                }
                break;
            case 1:
                if (receivedJsonObject.get("status").equals("ok")) {
                    if (isLeave) {
                        state = 0;
                        clientCLI.printlnConsole("You have left the game.");
                        clientCLI.askForJoinGame();
                    }
                    else {
                        state = 2;
                        clientCLI.printlnConsole("Waiting to start game...");
                    }
                }
                else {
                    clientCLI.askForReadyOrLeave();
                }
                break;
            case 2:
                if (receivedJsonObject.get("method").equals("start")) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", "ok");
                    sendTcpMessage(jsonObject.toJSONString());
                }
                break;
            default:
                break;
        }
    }

    public void sendJoinRequest(String username, Integer udpPortNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "join");
        jsonObject.put("username", username);
        try {
            jsonObject.put("udp_address", InetAddress.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        jsonObject.put("udp_port", udpPortNumber);
        sendTcpMessage(jsonObject.toJSONString());
    }

    public void sendReadyRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "ready");
        sendTcpMessage(jsonObject.toJSONString());
    }

    public void sendLeaveRequest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "leave");
        isLeave = true;
        sendTcpMessage(jsonObject.toJSONString());
    }
}
