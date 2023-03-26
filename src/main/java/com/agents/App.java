package com.agents;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.agents.models.Administrator;
import com.agents.models.Kitchen;

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

            Message message = new Message(AgentNames.KITCHEN, AgentNames.ADMIN,
                    MessageType.Ping, "Ping!");
            synchronized (administrator) {
                administrator.sendMessage(message);
            }
            System.out.println("Message sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Do the method that gets Client or any derived classes from Client. Add them
    // to ArrayList and do .startClient() on them.
    private static void startClient(Client client) {
        clients.add(client);
        client.startClient();
        logger.info(MessageFormat.format("Client {0} started", client.clientName));
    }

}
