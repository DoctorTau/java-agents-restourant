package com.agents;

import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import com.agents.models.Administrator;
import com.agents.models.Cooker;
import com.agents.models.Instrument;
import com.agents.models.Kitchen;
import com.agents.models.Storage;
import com.agents.models.Visitor;

public class App {
    private static ArrayList<Client> clients = new ArrayList<>();
    private static final Logger logger = MyLogger.getLogger();

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

            Thread.sleep(1000);

            Message message = new Message(AgentNames.KITCHEN, AgentNames.ADMIN, MessageType.Ping, AgentNames.ADMIN);
            administrator.sendMessage(message);
            Thread.sleep(1000);

            Message message2 = new Message(AgentNames.ADMIN, AgentNames.KITCHEN, MessageType.Ping, AgentNames.KITCHEN);
            kitchen.sendMessage(message2);

            createEverythingFromJson();

            Thread.sleep(1000);
            startCookersWorker();
            startInstrumentsWorker();

            startOrders();

        } catch (Exception e) {
            logger.severe("Error on program run.");

            stopClients();
        }
    }

    private static void createEverythingFromJson() {
        try {
            JsonReader jsonReader = new JsonReader();
            ArrayList<String> visitors = jsonReader.readVisitors("src/main/java/com/agents/jsons/visitors.json");
            Menu fullMenu = jsonReader.readMenu("src/main/java/com/agents/jsons/fullmenu.json");
            ArrayList<Product> products = jsonReader.readProducts("src/main/java/com/agents/jsons/products.json");
            ArrayList<InstrumentObject> instrumentObjects = jsonReader
                    .readInstrumentObjects("src/main/java/com/agents/jsons/instruments.json");
            ArrayList<String> cookers = jsonReader.readCookers("src/main/java/com/agents/jsons/cookers.json");
            createClients(visitors, cookers, instrumentObjects, fullMenu, products);
        } catch (Exception e) {
            logger.severe("Error while parsing a JSON" + e.getMessage());
        }
    }

    private static void createClients(ArrayList<String> visitors, ArrayList<String> cookers,
            ArrayList<InstrumentObject> instrumentObjects, Menu fullMenu, ArrayList<Product> products) {
        try {
            Storage storage = new Storage(AgentNames.STORAGE, 5001, products, fullMenu);
            startClient(storage);
            for (String visitorName : visitors) {
                Visitor visitor = new Visitor(visitorName, 5001);
                startClient(visitor);
            }
            for (String cookerName : cookers) {
                Cooker cooker = new Cooker(cookerName, 5001);
                startClient(cooker);
            }
            for (InstrumentObject instrumentObject : instrumentObjects) {
                Instrument instrument = new Instrument(instrumentObject.getId(), 5001, instrumentObject.getName());
                startClient(instrument);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    private static void startOrders() {
        Random random = new Random();
        for (Client client : clients) {
            if (client instanceof Visitor) {
                Visitor visitor = (Visitor) client;
                try {
                    Thread.sleep(random.nextInt(1000, 10000));
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                }
                visitor.askForTheMenu();
            }
        }
    }

    private static void startCookersWorker() {
        for (Client client : clients) {
            if (client instanceof Cooker) {
                Cooker cooker = (Cooker) client;
                cooker.askForTheWork();
            }
        }
    }

    private static void startInstrumentsWorker() {
        for (Client client : clients) {
            if (client instanceof Instrument) {
                Instrument instrument = (Instrument) client;
                instrument.askForTheWork();
            }
        }
    }
}
