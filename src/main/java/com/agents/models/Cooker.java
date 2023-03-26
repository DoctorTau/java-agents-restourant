package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.Product;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Cooker extends Client {
    private static final Logger logger = Logger.getLogger(Cooker.class.getName());

    private String currentProcessName;
    private int countOfNeededProductsAndInstruments;

    private ArrayList<Product> products;

    public Cooker(Socket socket, String clientName) {
        super(socket, clientName);

        currentProcessName = "";
        countOfNeededProductsAndInstruments = 0;
        askForTheWork();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case WorkRespond:
                // Get the work from the message
                getAWork(message);
                break;
            case DishRequestRespond:
                // Ask for products and instruments
                askForProductsAndInstruments(message);
                break;
            case InstrumentsRespond:
                // Get a product or an instrument
                getAProductOrAnInstrument();
                break;
            case ProcessRespond:
                // Reset the current process name and the count of needed products and
                // instruments
                currentProcessName = "";
                countOfNeededProductsAndInstruments = 0;
                // Ask for the work
                askForTheWork();
                break;
            case ProductRespond:
                // Add the product to the inventory
                addProduct(message);
                break;
            default:
                break;
        }
    }

    private void getAWork(Message message) {
        try {
            currentProcessName = message.getData();
            Message neededDishRequest = new Message(currentProcessName, this.clientName,
                    MessageType.DishRequestRespond);

            sendMessage(neededDishRequest);

            logger.log(Level.INFO, this.clientName + ": Received work request from Kitchen and got assigned to work on "
                    + currentProcessName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while getting work from Kitchen", e);
        }
    }

    /**
     * Asks for products and instruments.
     * 
     * @param message Message with the dish's id.
     */
    private void askForProductsAndInstruments(Message message) {
        try {
            Dish dish = Dish.fromJson(message.getData());
            ArrayList<Product> products = dish.getProducts();
            ArrayList<String> instruments = dish.getInstruments();
            countOfNeededProductsAndInstruments = products.size() + instruments.size();

            for (Product product : products) {
                askForPoduct(product);
            }

            for (String instrument : instruments) {
                askForInstrument(instrument);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while asking for products and instruments", e);
        }
    }

    /**
     * Asks for an instrument from the kitchen.
     * 
     * @param instrument The name of the instrument.
     */
    private void askForInstrument(String instrument) {
        Message instrumentRequest = new Message(AgentNames.KITCHEN, this.clientName,
                MessageType.InstrumentsRequest, instrument);

        sendMessage(instrumentRequest);
        logger.log(Level.INFO, this.clientName + ": Sent instrument request to Kitchen for instrument: " + instrument);
    }

    /**
     * Decreases the number of needed products and instruments. If the number is 0,
     */
    private void getAProductOrAnInstrument() {
        --countOfNeededProductsAndInstruments;
        if (countOfNeededProductsAndInstruments == 0) {
            startWorking();
        }
    }

    /**
     * Asks for a product from the storage.
     * 
     * @param product The product.
     */
    private void askForPoduct(Product product) {
        try {
            // Sends a request for the product to the storage. In request body is the
            // product's id.
            Message message = new Message(AgentNames.STORAGE, this.clientName, MessageType.ProductRequest,
                    product.getId());
            sendMessage(message);
            logger.log(Level.INFO,
                    this.clientName + ": Sent product request to Storage for product with ID: " + product.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while asking for a product", e);
        }
    }

    /**
     * Adds a product from the storage.
     * 
     * @param message Message with the product's id.
     */
    private void addProduct(Message message) {
        products.add(new Product(message.getData()));
    }

    /**
     * Sends a message to the procces that the process has started.
     */
    private void startWorking() {
        try {
            Message processStart = new Message(currentProcessName, this.clientName, MessageType.ProcessRequest);

            sendMessage(processStart);

            logger.log(Level.INFO, this.clientName + ": All products and instruments have arrived, starting work on "
                    + currentProcessName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while starting work", e);
        }
    }

    /**
     * Asks for the work from the kitchen.
     */
    private void askForTheWork() {
        try {
            Message workRequest = new Message(AgentNames.KITCHEN, this.clientName, MessageType.WorkRequest);

            sendMessage(workRequest);

            logger.log(Level.INFO, this.clientName + ": Asking for the new work");
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while asking for the new work", e);
        }
    }
}
