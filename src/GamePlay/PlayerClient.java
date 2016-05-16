package GamePlay;

/**
 * Created by erickchandra on 5/16/16.
 */
public class PlayerClient {
    // Attributes
    Integer playerId;
    Boolean isAlive;
    String udpIpAddress;
    Integer udpPortNumber;
    String username;
    String role = null;

    // Constructor
    public PlayerClient(Integer playerId, Boolean isAlive, String udpIpAddress, Integer udpPortNumber, String username) {
        this.playerId = playerId;
        this.isAlive = isAlive;
        this.udpIpAddress = udpIpAddress;
        this.udpPortNumber = udpPortNumber;
        this.username = username;
    }

    // Getter
    public Integer getPlayerId() {
        return this.playerId;
    }

    public Boolean getIsAlive() {
        return this.isAlive;
    }

    public String getUdpIpAddress() {
        return this.udpIpAddress;
    }

    public Integer getUdpPortNumber() {
        return this.udpPortNumber;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRole() {
        return this.role;
    }

    // Setter
    public void setIsAlive(Boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Methods

}
