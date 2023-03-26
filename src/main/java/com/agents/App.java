package com.agents;

import java.io.File;
import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.agents.models.Administrator;
import com.agents.models.Cooker;
import com.agents.models.Instrument;
import com.agents.models.Kitchen;
import com.agents.models.Storage;
import com.agents.models.Visitor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

            Thread.sleep(1000);

            Message message = new Message(AgentNames.KITCHEN, AgentNames.ADMIN, MessageType.Ping, AgentNames.ADMIN);
            administrator.sendMessage(message);
            Thread.sleep(1000);

            Message message2 = new Message(AgentNames.ADMIN, AgentNames.KITCHEN, MessageType.Ping, AgentNames.KITCHEN);
            kitchen.sendMessage(message2);

            createEverythingFromJson();

            Thread.sleep(1000);
            startOrders();
            startCookersWorker();
            startInstrumentsWorker();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createEverythingFromJson() {
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

        try {
            Storage storage = new Storage(AgentNames.STORAGE, 5001, products,
                    fullMenu);
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
                Instrument instrument = new Instrument(instrumentObject.getId(), 5001,
                        instrumentObject.getName());
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
        for (Client client : clients) {
            if (client instanceof Visitor) {
                Visitor visitor = (Visitor) client;
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
