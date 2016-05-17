package Agent;

import CLI.ClientCLI;
import Communication.*;
import GamePlay.PlayerClient;
import Paxos.Acceptor;
import Paxos.ProposalId;
import Paxos.Proposer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.sql.rowset.serial.SerialJavaObject;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.*;

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
    Integer playerId;
    Proposer proposer;
    Acceptor acceptor;

    // Clients information
    HashSet<PlayerClient> playerClientHashSet = null;

    // For Paxos Algorithm Information
    Boolean isProposer = false;
    Integer kpuIdSelected = null;
    ExecutorService executorService;

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
            JSONObject receivedJSONObject = new JSONObject(parseJSONStringToJSONObject(receivedString));
            if (receivedJSONObject.get("clients") != null) {
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = null;
                try {
                    jsonArray = (JSONArray) jsonParser.parse(receivedJSONObject.get("clients").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (playerClientHashSet == null) {
                    playerClientHashSet = new HashSet<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject;
                        jsonObject = (JSONObject) jsonArray.get(i);
                        PlayerClient playerClient = new PlayerClient(Integer.parseInt(jsonObject.get("player_id").toString()), Integer.parseInt(jsonObject.get("is_alive").toString()) == 1, jsonObject.get("address").toString(), Integer.parseInt(jsonObject.get("port").toString()), jsonObject.get("username").toString());
                        playerClientHashSet.add(playerClient);
                    }
                }
                else {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject;
                        jsonObject = (JSONObject) jsonArray.get(i);
                        PlayerClient playerClient = new PlayerClient(Integer.parseInt(jsonObject.get("player_id").toString()), Integer.parseInt(jsonObject.get("is_alive").toString()) == 1, jsonObject.get("address").toString(), Integer.parseInt(jsonObject.get("port").toString()), jsonObject.get("username").toString());
                        String role;
                        if (!playerClient.getIsAlive()) {
                            role = jsonObject.get("role").toString();

                            // Iterate through playerClientHashSet and update value
                            Iterator<PlayerClient> iterator = playerClientHashSet.iterator();
                            PlayerClient currentPlayerClientInIterator;
                            while (iterator.hasNext()) {
                                currentPlayerClientInIterator = iterator.next();
                                if (currentPlayerClientInIterator.getPlayerId() == playerClient.getPlayerId()) {
                                    currentPlayerClientInIterator.setIsAlive(playerClient.getIsAlive());
                                    currentPlayerClientInIterator.setRole(role);
                                    break;
                                }
                                else {
                                    // Do nothing, continue loop
                                }
                            }
                        }
                    }
                }
            }
            else {
                receiveAndExecute(receivedJSONObject);
            }
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
                    this.playerId = Integer.parseInt(receivedJsonObject.get("player_id").toString());
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
                    jsonObject = new JSONObject();
                    jsonObject.put("method", "client_address");
                    sendTcpMessage(jsonObject.toJSONString());
                    while (playerClientHashSet == null) {
                        sendTcpMessage(jsonObject.toJSONString());
                    }

                    setProposerOrAcceptor();
                    if (isProposer) {
                        proposer = new Proposer(playerId, quorumSize());
                        startProposer();
                    }
                    else {
                        acceptor = new Acceptor();
                    }
                }
                break;
            case 5:
                if (isProposer) {
                    if (receivedJsonObject.get("status").equals("ok") && receivedJsonObject.get("description").equals("accepted")) {
                        HashMap<ProposalId, Integer> hashMap;
                        hashMap = proposer.receivePromise(Integer.parseInt(receivedJsonObject.get("previous_accepted").toString()));
                        sendUdpAccept(hashMap);
                    }
                }
                else {
                    if (receivedJsonObject.get("method").equals("prepare_proposal")) {
                        JSONParser jsonParser = new JSONParser();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = (JSONArray) jsonParser.parse(receivedJsonObject.get("proposal_id").toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ProposalId tempProposalId = new ProposalId(Integer.parseInt(jsonArray.get(0).toString()), Integer.parseInt(jsonArray.get(1).toString()));
                        HashMap<ProposalId, Integer> reply = acceptor.receivePromise(tempProposalId);
                        sendUdpAccept(reply);
                    }
                    else if (receivedJsonObject.get("method").equals("accept_proposal")) {
                        JSONParser jsonParser = new JSONParser();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = (JSONArray) jsonParser.parse(receivedJsonObject.get("proposal_id").toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ProposalId tempProposalId = new ProposalId(Integer.parseInt(jsonArray.get(0).toString()), Integer.parseInt(jsonArray.get(1).toString()));
                        if (acceptor.receiveAccept(tempProposalId, Integer.parseInt(receivedJsonObject.get("kpu_id").toString()))) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("method", "accepted_proposal");
                            jsonObject.put("kpu_id", Integer.parseInt(receivedJsonObject.get("kpu_id").toString()));
                            jsonObject.put("description", "Kpu is selected.");
                            sendTcpMessage(jsonObject.toJSONString());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public void setProposerOrAcceptor() {
        HashSet<PlayerClient> playerClientHashSetClone = new HashSet<>(playerClientHashSet);
        Iterator<PlayerClient> iterator = playerClientHashSetClone.iterator();
        PlayerClient currentPlayerClientInIterator;
        Integer maxPlayerId = null;
        while (iterator.hasNext()) {
            currentPlayerClientInIterator = iterator.next();
            if (maxPlayerId == null) {
                maxPlayerId = currentPlayerClientInIterator.getPlayerId();
            }
            else {
                if (currentPlayerClientInIterator.getPlayerId() > maxPlayerId) {
                    maxPlayerId = currentPlayerClientInIterator.getPlayerId();
                }
            }
        }

        if (maxPlayerId == this.playerId) {
            isProposer = true;
        }
        else {
            // Find Max 2
            Integer maxPlayerId2 = null;
            iterator = playerClientHashSetClone.iterator();
            while (iterator.hasNext()) {
                currentPlayerClientInIterator = iterator.next();
                if (maxPlayerId2 == null && maxPlayerId != currentPlayerClientInIterator.getPlayerId()) {
                    maxPlayerId2 = currentPlayerClientInIterator.getPlayerId();
                }
                else {
                    if (maxPlayerId != currentPlayerClientInIterator.getPlayerId() && currentPlayerClientInIterator.getPlayerId() > maxPlayerId2) {
                        maxPlayerId2 = currentPlayerClientInIterator.getPlayerId();
                    }
                }
            }

            if (maxPlayerId2 == this.playerId) {
                isProposer = true;
            }
        }
    }

    public Integer quorumSize() {
        return (playerClientHashSet.size() / 2) + 1;
    }

    public void startProposer() {
        executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(new Task());

        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
//            e.printStackTrace();
            future.cancel(true);
            printlnConsoleLog("Proposer timeout.");
            if (kpuIdSelected == null && state == 5) {
                sendUdpPrepare(proposer);
            }
        }

        executorService.shutdownNow();

        if (state == 5 && kpuIdSelected == null) {
            startProposer();
        }

        kpuIdSelected = null;
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

    public void sendUdpPrepare(Proposer proposer) {
        ProposalId proposalId;
        proposalId = proposer.sendPrepare();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(proposalId.getProposalNumber());
        jsonArray.add(proposalId.getUniqueId());
        jsonObject.put("method", "prepare_proposal");
        jsonObject.put("proposal_id", jsonArray);

        Iterator<PlayerClient> iterator = playerClientHashSet.iterator();
        PlayerClient currentPlayerClientInIterator;
        while (iterator.hasNext()) {
            currentPlayerClientInIterator = iterator.next();

            sendUdpMessage(jsonObject.toJSONString(), currentPlayerClientInIterator.getUdpIpAddress(), currentPlayerClientInIterator.getUdpPortNumber());
        }
    }

    public void sendUdpAccept(HashMap<ProposalId, Integer> hashMap) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("method", "accept_proposal");

        ProposalId sendProposalId = null;
        Integer sendAcceptValue = null;
        for (ProposalId proposalId : hashMap.keySet()) {
            sendProposalId = proposalId;
            sendAcceptValue = hashMap.get(proposalId);
        }
        jsonArray.add(sendProposalId.getProposalNumber());
        jsonArray.add(sendProposalId.getUniqueId());

        jsonObject.put("proposal_id", jsonArray);
        jsonObject.put("kpu_id", sendAcceptValue);

        Iterator<PlayerClient> iterator = playerClientHashSet.iterator();
        PlayerClient currentPlayerClientInIterator;
        while (iterator.hasNext()) {
            currentPlayerClientInIterator = iterator.next();

            sendUdpMessage(jsonObject.toJSONString(), currentPlayerClientInIterator.getUdpIpAddress(), currentPlayerClientInIterator.getUdpPortNumber());
        }
    }
}

class Task implements Callable<String> {
    @Override
    public String call() throws Exception {
        while (!Thread.interrupted()) {
            // Waiting to timeout
        }
        return "Ready!";
    }
}