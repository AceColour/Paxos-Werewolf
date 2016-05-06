package Paxos;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by erickchandra on 5/4/16.
 *
 * Authors:
 * Erick Chandra (13513021@std.stei.itb.ac.id; erickchandra.1@gmail.com)
 * Muhamad Fikri Alhawarizmi (13513009@std.stei.itb.ac.id; mfikria@gmail.com)
 * Muhammad Nizami (13512501@std.stei.itb.ac.id; nizami_muhammad@yahoo.co.id)
 *
 * This class implements Learner in Paxos Algorithm.
 */
public class Learner {
    // Attributes
    Integer quorumSize;
    HashMap<Integer, Integer> acceptedValueHashMap = new HashMap<>(); // KEY: playerId, VAL: acceptedValue.

    // Constructor
    public Learner(Integer quorumSize) {
        this.quorumSize = quorumSize;
    }

    // Getter


    // Setter


    // Methods
    public Integer receiveAccepted(Integer playerId, Integer acceptedValue) {
        // If it reaches quorum, return major value, else return null.
        // Major value will be calculated by iterating through the whole counting procedure. It will take the first value that meet quorumSize.
        acceptedValueHashMap.put(playerId, acceptedValue);

        // Check if it has reached quorum.
        HashMap<Integer, Integer> acceptedValueCounterHashMap = new HashMap<>();    // For counting all incoming acceptedValue.

        Iterator iterator = acceptedValueHashMap.entrySet().iterator();
        HashMap.Entry<Integer, Integer> acceptedValueHashMapInIterator;
        while (iterator.hasNext()) {
            acceptedValueHashMapInIterator = (HashMap.Entry<Integer, Integer>) iterator.next();
            if (acceptedValueCounterHashMap.containsKey(acceptedValueHashMapInIterator.getValue())) {
                acceptedValueCounterHashMap.put(acceptedValueHashMapInIterator.getValue(), acceptedValueCounterHashMap.get(acceptedValueHashMapInIterator.getValue()) + 1);
            }
            else {
                acceptedValueCounterHashMap.put(acceptedValueHashMapInIterator.getValue(), 1);
            }
        }

        iterator = acceptedValueCounterHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            acceptedValueHashMapInIterator = (HashMap.Entry<Integer, Integer>) iterator.next();
            if (acceptedValueHashMapInIterator.getValue() >= quorumSize) {
                return acceptedValueHashMapInIterator.getKey();
            }
        }

        return null;
    }

}
