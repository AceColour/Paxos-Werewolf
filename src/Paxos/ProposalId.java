package Paxos;

/**
 * Created by erickchandra on 5/6/16.
 */
public class ProposalId {
    // Attributes
    Integer proposalNumber;
    Integer uniqueId;       // In this project context, uniqueId is playerId.

    // Constructor
    public ProposalId(Integer proposalNumber, Integer uniqueId) {
        this.proposalNumber = proposalNumber;
        this.uniqueId = uniqueId;
    }

    // Getter
    public Integer getProposalNumber() {
        return proposalNumber;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    // Setter


    // Methods
    public Boolean isGreaterThan(ProposalId proposalId) {
        if (this.getProposalNumber() > proposalId.getProposalNumber()) {
            return true;
        }
        else if (this.getProposalNumber() < proposalId.getProposalNumber()) {
            return false;
        }
        else {      // Both has the same proposalNumber
            if (this.getUniqueId() > proposalId.getUniqueId()) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public Boolean equals(ProposalId proposalId) {
        if (this.getProposalNumber() == proposalId.getProposalNumber() && this.getUniqueId() == proposalId.getUniqueId()) {
            return true;
        }
        else {
            return false;
        }
    }

}
