package com.agents;

import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.agents.models.Administrator;
import com.agents.models.Kitchen;
import com.agents.models.Storage;

public class App {
    private static ArrayList<Client> clients = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            Server server = new Server(serverSocket);
            Thread serverThread = new Thread(server);
            serverThread.start();
            System.out.println("Server started");

            Administrator administrator = new Administrator(AgentNames.ADMIN, 5001);
            startClient(administrator);

            Kitchen kitchen = new Kitchen(AgentNames.KITCHEN, 5001);
            startClient(kitchen);

            Storage storage = new Storage(AgentNames.STORAGE, 5001);
            startClient(storage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a client and adds it to the list of clients.
     * 
     * @param client Client to start
     */
    private static void startClient(Client client) {
        clients.add(client);
        client.startClient();

        logger.info(MessageFormat.format("Client {0} started", client.clientName));
    }

    /**
     * Stops all clients.
     */
    public static void stopClients() {
        for (Client client : clients) {
            client.finishClient();
        }
    }

}
