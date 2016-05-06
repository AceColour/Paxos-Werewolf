package Communication;

/**
 * Created by erickchandra on 5/5/16.
 */
public interface TCPThreadListener {
    void onTCPDataReceived(String receivedString);
}
