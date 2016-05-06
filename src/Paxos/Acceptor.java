package Paxos;

import java.util.HashMap;

/**
 * Created by erickchandra on 5/4/16.
 *
 * Authors:
 * Erick Chandra (13513021@std.stei.itb.ac.id; erickchandra.1@gmail.com)
 * Muhamad Fikri Alhawarizmi (13513009@std.stei.itb.ac.id; mfikria@gmail.com)
 * Muhammad Nizami (13512501@std.stei.itb.ac.id; nizami_muhammad@yahoo.co.id)
 *
 * This class implements Acceptor in Paxos Algorithm.
 */
public class Acceptor {
    // Attributes
    ProposalId promisedProposalId = null;
    Integer lastAcceptedValue = null;

    // Getter
    public ProposalId getPromisedProposalId() {
        return promisedProposalId;
    }

    public Integer getLastAcceptedValue() {
        return lastAcceptedValue;
    }

    // Setter


    // Methods
    public HashMap<ProposalId, Integer> receivePromise(ProposalId proposalId) {
        // Returns HashMap containing promised ProposalId and lastAcceptedValue.
        // If no lastAcceptedValue, it will be null.
        // If promise REJECTED, this function will return null.
        if (promisedProposalId == null) {
            // Directly accept promise
            HashMap<ProposalId, Integer> proposalIdIntegerHashMap = new HashMap<>();
            proposalIdIntegerHashMap.put(proposalId, lastAcceptedValue);
            this.promisedProposalId = proposalId;
            return proposalIdIntegerHashMap;
        }
        else {
            if (proposalId.isGreaterThan(this.promisedProposalId)) {
                // Accept promise
                HashMap<ProposalId, Integer> proposalIdIntegerHashMap = new HashMap<>();
                proposalIdIntegerHashMap.put(proposalId, lastAcceptedValue);
                this.promisedProposalId = proposalId;
                return proposalIdIntegerHashMap;
            }
            else {
                // Reject promise
                return null;
            }
        }
    }

    public Boolean receiveAccept(ProposalId proposalId, Integer acceptValue) {
        // If Accept request accepted, return true, else false.
        if (promisedProposalId.equals(proposalId)) {
            lastAcceptedValue = acceptValue;
            return true;
        }
        else {
            return false;
        }
    }

}
