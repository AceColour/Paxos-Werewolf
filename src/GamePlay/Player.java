package GamePlay;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by erickchandra on 5/4/16.
 *
 * Authors:
 * Erick Chandra (13513021@std.stei.itb.ac.id; erickchandra.1@gmail.com)
 * Muhamad Fikri Alhawarizmi (13513009@std.stei.itb.ac.id; mfikria@gmail.com)
 * Muhammad Nizami (13512501@std.stei.itb.ac.id; nizami_muhammad@yahoo.co.id)
 *
 * This class is for storing player information.
 */
public class Player {
    // Attributes
    static Integer lastPlayerId;            // For storing the last assigned PlayerId. Needed for incrementing next PlayerId.


    Integer playerId;
    String username;
    String udpIpAddress;
    Integer udpPortNumber;
    Boolean isReady;        // The player is whether or not ready for the game.
    Boolean isAlive;        // The player is whether or not alive in Werewolf Game.
    Boolean isConnected;    // The player is whether connected or disconnected (related to network communication).
    Boolean isLeft;         // The player has whether or not left the Werewolf Game (related to Join and Leave Game).
    Boolean isWerewolf;     // The player is whether or not a werewolf (for role need).
    // Precedences
    //// isConnected > isLeft > isAlive

    // Constructor
    public Player(String username, String udpIpAddress, Integer udpPortNumber) {
        // When creating object, the username MUST BE CHECKED first.
        // Initial State: Username does NOT exist. Client has requested JOIN method.

        if (lastPlayerId == null) {
            lastPlayerId = 0;
        }
        else {
            lastPlayerId++;
        }

        this.playerId = lastPlayerId;
        this.username = username;
        this.udpIpAddress = udpIpAddress;
        this.udpPortNumber = udpPortNumber;
        this.isAlive = false;
        this.isConnected = true;
        this.isLeft = false;
    }

    // Getter
    public Integer getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public String getUdpIpAddress() {
        return udpIpAddress;
    }

    public Integer getUdpPortNumber() { return udpPortNumber; }

    public static Integer getLastPlayerId() {
        return lastPlayerId;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public Boolean getIsLeft() {
        return isLeft;
    }

    public Boolean getIsReady() { return isReady; }

    public Boolean getIsAlive() { return isAlive; }

    public Boolean getIsWerewolf() { return isWerewolf; }

    // Setter
    public void setIsWerewolf(Boolean isWerewolf) {
        this.isWerewolf = isWerewolf;
    }


    // Methods
    public void readyGame() {
        this.isReady = true;
    }

}
