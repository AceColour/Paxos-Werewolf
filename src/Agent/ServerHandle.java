package Agent;

import Communication.*;
import GamePlay.Game;
import GamePlay.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by erickchandra on 5/5/16.
 */
public class ServerHandle extends Thread implements TCPThreadListener {
    // Attributes
    Socket clientSocket;
    public static HashSet<ServerHandle> serverHandleHashSet;
    TCPReceiver tcpReceiver;
    TCPSender tcpSender;

    static Game game;
    static Integer state;
    /*
     * State information:
     * 0: Waiting Join and Ready method
     * 1: All joined players are ready and Game is started
     * 2: Server is waiting for KPU election
     * 3: Server is waiting for vote_civilian (Civilian votes who will be killed)
     * 4: Server is waiting for vote_werewolf (Werewolf votes which civilian will be killed)
     * 5: Game over
     */

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

        if (game == null) {
            game = new Game();
        }

        if (state == null) {
            state = 0;
        }
    }

    // Getter


    // Setter


    // Methods
    @Override
    public void onTCPDataReceived(String receivedString) {
        printlnConsoleLog("Received and caught from TCP: " + receivedString);
        JSONObject jsonObject;
        if (parseJSONStringToJSONObject(receivedString) == null) {
            printlnConsoleLog("Invalid string received.");
            JSONObject sendJSONObject = new JSONObject();
            sendJSONObject.put("status", "error");
            sendJSONObject.put("description", "wrong request");
            sendTcpMessage(sendJSONObject.toJSONString());
        }
        else {
            jsonObject = parseJSONStringToJSONObject(receivedString);
            if (jsonObject.containsKey("method")) {
                // Inside this block, perform something based on states.
                if (jsonObject.get("method").equals("join") && state != 0) {
                    JSONObject sendJSONObject = new JSONObject();
                    sendJSONObject.put("status", "fail");
                    sendJSONObject.put("status", "Please wait. Game is currently running.");
                    sendTcpMessage(sendJSONObject.toJSONString());
                }
                else if (jsonObject.get("method").equals("leave") && state != 0) {
                    JSONObject sendJSONObject = new JSONObject();
                    sendJSONObject.put("status", "fail");
                    sendJSONObject.put("status", "You are not allowed to leave. Game is running.");
                    sendTcpMessage(sendJSONObject.toJSONString());
                }
                else if (jsonObject.get("method").equals("client_address") && state == 0) {
                    JSONObject sendJSONObject = new JSONObject();
                    sendJSONObject.put("status", "fail");
                    sendJSONObject.put("status", "Game is not running. No client address could be retrieved.");
                    sendTcpMessage(sendJSONObject.toJSONString());
                }
                else if (jsonObject.get("method").equals("client_address") && state != 0 && state != 1) {
                    JSONObject sendJSONObject = new JSONObject();
                    sendJSONObject.put("status", "ok");

                    HashSet<Player> playerHashSet = game.getPlayerHashSet();
                    Iterator<Player> playerIterator = playerHashSet.iterator();
                    Player currentPlayerInIterator;

                    JSONArray jsonArrayClientList = new JSONArray();

                    while (playerIterator.hasNext()) {
                        currentPlayerInIterator = playerIterator.next();
                        JSONObject jsonObjectPlayerDetails =  new JSONObject();
                        jsonObjectPlayerDetails.put("player_id", currentPlayerInIterator.getPlayerId());
                        jsonObjectPlayerDetails.put("is_alive", currentPlayerInIterator.getIsAlive() ? 1 : 0);
                        jsonObjectPlayerDetails.put("address", currentPlayerInIterator.getUdpIpAddress());
                        jsonObjectPlayerDetails.put("port", currentPlayerInIterator.getUdpPortNumber());
                        jsonObjectPlayerDetails.put("username", currentPlayerInIterator.getUsername());
                        if (currentPlayerInIterator.getIsAlive()) {
                            jsonObjectPlayerDetails.put("role", currentPlayerInIterator.getIsWerewolf());
                        }
                        jsonArrayClientList.add(jsonObjectPlayerDetails);
                    }

                    sendJSONObject.put("clients", jsonArrayClientList);
                    sendJSONObject.put("description", "List of clients retrieved.");

                    sendTcpMessage(sendJSONObject.toJSONString());
                }
                else {
                    receiveAndExecute(jsonObject);
                }
            }
            else {
                // Send Fail: No method found.
                JSONObject sendJSONObject = new JSONObject();
                sendJSONObject.put("status", "fail");
                sendJSONObject.put("description", "No method found.");
                tcpSender = new TCPSender(clientSocket);
                tcpSender.sendMessage(sendJSONObject.toJSONString());
                try {
                    tcpSender.join();
                    printlnConsoleLog("TCP Sender Thread has ended.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void receiveAndExecute(JSONObject receivedJSONObject) {
        switch (state) {
            case 0:
                if (receivedJSONObject.get("method").equals("join")) {
                    if (game.isUsernameFound(receivedJSONObject.get("username").toString())) {
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "fail");
                        sendJSONObject.put("description", "Username exists.");
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }
                    else {
                        Integer playerId;
                        playerId = game.joinGame(receivedJSONObject.get("username").toString(), clientSocket.getRemoteSocketAddress().toString(), Integer.parseInt(receivedJSONObject.get("udp_port").toString()));
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "ok");
                        sendJSONObject.put("player_id", playerId);
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }
                }
                else if (receivedJSONObject.get("method").equals("leave")) {
                    if (game.leaveGame(clientSocket.getRemoteSocketAddress().toString())) {
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "ok");
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }
                    else {
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "fail");
                        sendJSONObject.put("description", "You are not in the list.");
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }
                }
                else {
                    // Ignore
                }
                break;
            case 1:
                if (receivedJSONObject.get("method").equals("ready")){
                    if (game.readyGame(clientSocket.getRemoteSocketAddress().toString())) {
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "ok");
                        sendJSONObject.put("description", "waiting for other player to start.");
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }
                    else {
                        JSONObject sendJSONObject = new JSONObject();
                        sendJSONObject.put("status", "fail");
                        sendJSONObject.put("description", "You have not joined the game.");
                        sendTcpMessage(sendJSONObject.toJSONString());
                    }

                    if (game.getIsStarted()) {
                        // Game has started
                        state = 2;
                    }
                }

                break;
            case 2:
                if (receivedJSONObject.get("method").equals("accepted_proposal"))
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    public void run() {

    }

    public void sendTcpMessage(String message) {
        tcpSender = new TCPSender(clientSocket);
        tcpSender.sendMessage(message);
        try {
            tcpSender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printlnConsoleLog(String string) {
        System.out.println("|| SERVERHANDLE: " + string);
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
}
