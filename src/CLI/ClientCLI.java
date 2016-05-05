package CLI;

import Agent.Client;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Scanner;

/**
 * Created by erickchandra on 5/5/16.
 */
public class ClientCLI {
    // Attributes
    String username;
    String serverIpAddress;
    Integer serverPortNumber;
    Integer clientPortNumber;
    Client client;

    Scanner scanner = new Scanner(System.in);

    // Getter
    public String getUsername() {
        return this.username;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public Integer getServerPortNumber() {
        return serverPortNumber;
    }

    public Integer getClientPortNumber() {
        return clientPortNumber;
    }

    // Setter
    public void setUsername(String username) {
        this.username = username;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public void setServerPortNumber(Integer serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
    }

    public void setClientPortNumber(Integer clientPortNumber) {
        this.clientPortNumber = clientPortNumber;
    }

    // Methods
    public void printConsole(String string) {
        System.out.print(string);
    }

    public void printlnConsole(String string) {
        System.out.println(string);
    }

    public  void printlnConsole() {
        System.out.println();
    }

    public void askUsername() {
        printConsole("Username: ");
        setUsername(scanner.next());
    }

    public void askServerIpAddress() {
        printConsole("Server IP Adress: ");
        setServerIpAddress(scanner.next());
    }

    public void askServerPort() {
        printConsole("Server Port: ");
        setServerPortNumber(scanner.nextInt());
    }

    public void askClientPort() {
        printConsole("Your port: ");
        setClientPortNumber(scanner.nextInt());
    }

    public void showWelcomeMessage() {
        printlnConsole("Welcome to Werewolf Game (Client)");
        printlnConsole("=================================");
        printlnConsole("For all networks, we are using IPv4.");
        printlnConsole("Enjoy playing Werewolf Game!");
        printlnConsole();
    }

    public void createAndRunClient() {
        client = new Client(this.serverIpAddress, this.serverPortNumber, this.clientPortNumber);
        client.run();
    }

    // Main
    public static void main(String[] args) {
        ClientCLI clientCLI = new ClientCLI();

        clientCLI.showWelcomeMessage();
        clientCLI.askUsername();
        clientCLI.askServerIpAddress();
        clientCLI.askServerPort();
        clientCLI.askClientPort();

        clientCLI.createAndRunClient();
    }
}
