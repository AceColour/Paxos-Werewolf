package GamePlay;

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
 * This class implements the modified Werewolf Game.
 */
public class Game {
    // Attributes
    Integer dayCount;
    Boolean isDay;
    Boolean isRunning;
    Boolean isStarted;

    static HashSet<Player> playerHashSet;   // For storing all Players' information.

    // Constructor
    public Game() {
        if (playerHashSet == null) {
            playerHashSet = new HashSet<>();
        }

        if (isRunning == null) {
            isRunning = false;
        }

        if (isStarted == null) {
            isStarted = false;
        }

        if (isDay == null) {
            isDay = true;
        }

        if (dayCount == null) {
            dayCount = 0;
        }
    }

    // Getter
    public Boolean getIsDay() {
        return isDay;
    }

    public Boolean getIsStarted() { return isStarted; }

    // Setter


    // Methods
    public void changePhase() {
        if (!isDay) {
            dayCount++;
        }
        isDay = !isDay;
    }

    public static HashSet<Player> getPlayerHashSet() {
        return playerHashSet;
    }

    public Integer getPlayerIdFromUdpIpAddress(String udpIpAddress) {
        // Returns PlayerId if it is found.
        // Returns -1 if PlayerId is not found.
        // Needed for checking incoming request or response.

        Iterator<Player> playerIterator = playerHashSet.iterator();
        Player currentPlayerInIterator;

        while (playerIterator.hasNext()) {
            currentPlayerInIterator = playerIterator.next();
            if (currentPlayerInIterator.getUdpIpAddress().equals(udpIpAddress)){
                return currentPlayerInIterator.getPlayerId();
            }
        }

        return -1;
    }

    public static boolean isUsernameFound(String username) {
        Iterator<Player> playerIterator = playerHashSet.iterator();
        Player currentPlayerInIterator;
        if (playerIterator.hasNext()) {
            do {
                currentPlayerInIterator = playerIterator.next();
                if (currentPlayerInIterator.getUsername().equals(username)) {
                    return true;
                }
            } while (playerIterator.hasNext());
            return false;
        }
        else {
            return false;
        }
    }

    public Integer joinGame(String username, String udpIpAddress, Integer udpPortNumber) {
        Player player = new Player(username, udpIpAddress, udpPortNumber);
        playerHashSet.add(player);
        return player.getPlayerId();
    }

    public Boolean readyGame(String udpIpAddress) {
        Integer playerId = getPlayerIdFromUdpIpAddress(udpIpAddress);
        if (playerId < 0) {
            return false;
        }
        else {
            Iterator<Player> playerIterator = playerHashSet.iterator();
            Player playerInIterator;
            while (playerIterator.hasNext()) {
                playerInIterator = playerIterator.next();
                if (playerInIterator.getPlayerId() == playerId) {
                    playerInIterator.readyGame();
                    // CHECK IF GAME IS AVAILABLE TO BE STARTED
                    if (isReadyStart()) {
                        startGame();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public Boolean isReadyStart() {
        // To check whether or not the game is available to start
        // Checking procedure: Making sure all Players are CONNECTED AND THEN NOT LEAVING AND THEN READY.
        Boolean found = false;
        Iterator<Player> playerIterator = playerHashSet.iterator();
        Player currentPlayerInIterator;
        while (!found && playerIterator.hasNext()) {
            currentPlayerInIterator = playerIterator.next();
            if (currentPlayerInIterator.getIsConnected() && !currentPlayerInIterator.getIsLeft() && currentPlayerInIterator.getIsReady()) {
                // PASS (doing nothing) and check next
            }
            else {
                found = true;
            }
        }

        return (!found);
    }

    public Boolean leaveGame(String udpIpAddress) {
        Integer playerId = getPlayerIdFromUdpIpAddress(udpIpAddress);
        if (playerId < 0) {
            return false;
        }
        else {
            Iterator<Player> playerIterator = playerHashSet.iterator();
            Player playerInIterator;
            Boolean found = false;
            while (!found && playerIterator.hasNext()) {
                playerInIterator = playerIterator.next();
                if (playerInIterator.getPlayerId() == playerId) {
                    playerIterator.remove();
                    found = true;
                }
            }
            return found;
        }
    }

    public void startGame() {
        isStarted = true;
    }
}
