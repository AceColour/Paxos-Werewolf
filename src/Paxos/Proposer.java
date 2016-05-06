package Paxos;


import GamePlay.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by erickchandra on 5/4/16.
 *
 * Authors:
 * Erick Chandra (13513021@std.stei.itb.ac.id; erickchandra.1@gmail.com)
 * Muhamad Fikri Alhawarizmi (13513009@std.stei.itb.ac.id; mfikria@gmail.com)
 * Muhammad Nizami (13512501@std.stei.itb.ac.id; nizami_muhammad@yahoo.co.id)
 *
 * This class implements Proposer in Paxos Algorithm.
 */
public class Proposer {
    // Attributes
    private Integer lastProposalNumber = 0;
    private Integer uniqueId;
    private Integer quorumSize;
    private HashMap<Integer, Integer> acceptorPreviousAcceptedValueHashMap = new HashMap<>(); // KEY: acceptedValue, VAL: count. If Acceptor sends nothing, KEY will be intentionally set to -1.
    private Integer lastProposalValue;

    // Constructor
    public Proposer(Integer uniqueId, Integer quorumSize) {
        this.uniqueId = uniqueId;
        this.quorumSize = quorumSize;
    }

    // Getter
    public Integer getQuorumSize() {
        return quorumSize;
    }

    // Setter
    public void setQuorumSize(Integer quorumSize) {
        this.quorumSize = quorumSize;
    }

    // Methods
    public ProposalId sendPrepare() {
        ProposalId proposalId = new ProposalId(++lastProposalNumber, this.uniqueId);
        this.acceptorPreviousAcceptedValueHashMap.clear();
        return proposalId;
    }

    public HashMap<ProposalId, Integer> receivePromise(Integer acceptorPreviousAcceptedValue) {
        // The return value is Accept! message to be sent by caller (the one who hold this class/object).
        // Returns value if and only if promises has reached quorum.
        // Returns null if it has not reached quorum.
        // KEY: ProposalId, VAL: ValueToBeAccepted. In this project context, VAL: KpuId.
        if (acceptorPreviousAcceptedValueHashMap.containsKey(acceptorPreviousAcceptedValue)) {
            acceptorPreviousAcceptedValueHashMap.put(acceptorPreviousAcceptedValue, acceptorPreviousAcceptedValueHashMap.get(acceptorPreviousAcceptedValue) + 1);
        }
        else {
            acceptorPreviousAcceptedValueHashMap.put(acceptorPreviousAcceptedValue, 1);
        }

        // Checking if it has reached quorum
        if (isPromiseQuorum()) {
            // Calculate value to be sent.
            HashMap<ProposalId, Integer> proposalIdIntegerHashMap = new HashMap<>();
            proposalIdIntegerHashMap.put(new ProposalId(this.lastProposalNumber, this.uniqueId), calculateSendAcceptValue());
            return proposalIdIntegerHashMap;
        }
        else {
            return null;
        }
    }

    public Boolean isPromiseQuorum() {
        if (acceptorPreviousAcceptedValueHashMap.size() >= quorumSize) {
            return true;
        }
        else {
            return false;
        }
    }

    public Integer calculateSendAcceptValue() {
        if (acceptorPreviousAcceptedValueHashMap.containsKey(-1) && acceptorPreviousAcceptedValueHashMap.size() == acceptorPreviousAcceptedValueHashMap.get(-1)) {
            // Randomly pick a value (KpuId) from available Player.
            Integer randomValue;
            Boolean found = false;
            Random random = new Random();
            do {
                randomValue = random.nextInt(Player.getLastPlayerId() + 1);

                // Perform check if the randomValue is valid for being a KpuId.
                Iterator<Player> playerIterator = Player.getPlayerHashSet().iterator();
                Player currentPlayerInIterator;
                Boolean playerFound = false;
                while (!playerFound && playerIterator.hasNext()) {
                    currentPlayerInIterator = playerIterator.next();
                    if (currentPlayerInIterator.getPlayerId() == randomValue && currentPlayerInIterator.getIsConnected() && !currentPlayerInIterator.getIsLeft()) {
                        playerFound = true;
                    }
                    else {
                        break;      // No need to check the other players because playerId is unique, therefore break and continue outer loop.
                    }
                }
            } while (!found);
            return randomValue;
        }
        else {
            // Calculate Majority from Accepted Values.
            // Majority will be taken from the most accepted value. If there is two or more values have the same count, then it will take the smallest key number.

            Integer majorKey, majorValue;
            Iterator iterator = acceptorPreviousAcceptedValueHashMap.entrySet().iterator();
            HashMap.Entry<Integer, Integer> integerIntegerEntry = (HashMap.Entry<Integer, Integer>) iterator.next();
            majorKey = integerIntegerEntry.getKey();
            majorValue = integerIntegerEntry.getValue();
            while (iterator.hasNext()) {
                integerIntegerEntry = (HashMap.Entry<Integer, Integer>) iterator.next();
                if (integerIntegerEntry.getValue() > majorValue) {
                    majorKey = integerIntegerEntry.getKey();
                    majorValue = integerIntegerEntry.getValue();
                }
            }

            return majorKey;
        }
    }
}
