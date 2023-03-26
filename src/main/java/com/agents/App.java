package com.agents;

import com.agents.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

    private void createEverythingFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> visitors = new ArrayList<>();
        Menu fullMenu = new Menu();
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<InstrumentObject> instrumentObjects = new ArrayList<>();
        ArrayList<String> cookers = new ArrayList<>();
        try {
            File visitorsFile = new File("src/main/java/com/agents/jsons/visitors.json");
            File fullMenuFile = new File("src/main/java/com/agents/jsons/fullmenu.json");
            File productsFile = new File("src/main/java/com/agents/jsons/products.json");
            File instrumentsFile = new File("src/main/java/com/agents/jsons/instruments.json");
            File cookersFile = new File("src/main/java/com/agents/jsons/cookers.json");
            visitors = objectMapper.readValue(visitorsFile, new TypeReference<ArrayList<String>>() {
            });
            fullMenu = objectMapper.readValue(fullMenuFile, Menu.class);
            products = objectMapper.readValue(productsFile, new TypeReference<ArrayList<Product>>() {
            });
            instrumentObjects = objectMapper.readValue(instrumentsFile,
                    new TypeReference<ArrayList<InstrumentObject>>() {
                    });
            cookers = objectMapper.readValue(cookersFile, new TypeReference<ArrayList<String>>() {
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /*
         * try {
         * ServerSocket serverSocket = new ServerSocket(5001);
         * Socket socket = new Socket(); // TODO: Create sockets properly
         * Server server = new Server(serverSocket);
         * new Thread(server).start();
         * Administrator administrator = new Administrator(socket, AgentNames.ADMIN);
         * new Thread(administrator).start();
         * Storage storage = new Storage(socket, AgentNames.STORAGE, products,
         * fullMenu);
         * new Thread(storage).start();
         * for (String visitorName : visitors) {
         * Visitor visitor = new Visitor(socket, visitorName);
         * new Thread(visitor).start();
         * }
         * for (String cookerName : cookers) {
         * Cooker cooker = new Cooker(socket, cookerName);
         * new Thread(cooker).start();
         * }
         * for (InstrumentObject instrumentObject : instrumentObjects) {
         * Instrument instrument = new Instrument(socket, instrumentObject.getId(),
         * instrumentObject.getName());
         * new Thread(instrument).start();
         * }
         * } catch (Exception e) {
         * System.out.println(e.getMessage());
         * }
         */
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
