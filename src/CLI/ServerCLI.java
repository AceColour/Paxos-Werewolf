package CLI;

import Agent.Server;

import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by erickchandra on 5/5/16.
 */
public class ServerCLI {
    // Attributes
    Server server;
    Integer serverPortNumber;
    Scanner scanner = new Scanner(System.in);

    // Getter


    // Setter


    // Methods
    public void askServerPort() {
        printConsole("Server Port: ");
        this.serverPortNumber = scanner.nextInt();
    }

    public void createAndRunServer() {
        this.server = new Server(this.serverPortNumber);
        this.server.run();
    }

    public void showWelcomeMessage() {
        printlnConsole("Welcome to Werewolf Game (Server)");
        printlnConsole("=================================");
        printlnConsole("We are using IPv4 for all networks.");
        printlnConsole("Enjoy playing Werewolf Game.");
        printlnConsole();
    }

    public void printConsole(String string) {
        System.out.print(string);
    }

    public void printlnConsole(String string) {
        System.out.println(string);
    }

    public void printlnConsole() {
        System.out.println();
    }

    public static void main(String[] args) {
        ServerCLI serverCLI = new ServerCLI();
        serverCLI.showWelcomeMessage();
        serverCLI.askServerPort();
        serverCLI.createAndRunServer();
    }
}
