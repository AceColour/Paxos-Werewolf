package Communication;

/**
 * Created by erickchandra on 5/5/16.
 */
public interface UDPThreadListener {
    void onUDPDataReceived (String receivedString, String fromIpAddress, Integer fromPortNumber);
}
