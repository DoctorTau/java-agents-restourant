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
                getAWork(message);
                break;
            case DishRequestRespond:
                askForProductsAndInstruments(message);
                break;
            case InstrumentsRespond:
            case ProductRespond:
                getAProductOrAnInstrument();
                break;
            case ProcessRespond:
                currentProcessName = "";
                countOfNeededProductsAndInstruments = 0;
                askForTheWork();
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

            logger.log(Level.INFO, this.clientName + ": Received work request from Kitchen and got assigned to work on " + currentProcessName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while getting work from Kitchen", e);
        }
    }

    private void askForProductsAndInstruments(Message message) {
        try {
            Dish dish = Dish.fromJson(message.getData());
            ArrayList<Product> products = dish.getProducts();
            ArrayList<String> instruments = dish.getInstruments();
            countOfNeededProductsAndInstruments = products.size() + instruments.size();

            for (Product product : products) {
                Message productRequest = new Message(AgentNames.STORAGE, this.clientName, MessageType.ProductRequest,
                        product.getId());
                sendMessage(productRequest);

                logger.log(Level.INFO,this.clientName + ": Sent product request to Storage for product with ID: " + product.getId());
            }

            for (String instrument : instruments) {
                Message instrumentRequest = new Message(AgentNames.KITCHEN, this.clientName,
                        MessageType.InstrumentsRequest, instrument);

                sendMessage(instrumentRequest);

                logger.log(Level.INFO,this.clientName + ": Sent instrument request to Kitchen for instrument: " + instrument);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while asking for products and instruments", e);
        }
    }

    private void getAProductOrAnInstrument() {
        --countOfNeededProductsAndInstruments;
        if (countOfNeededProductsAndInstruments == 0) {
            startWorking();
        }
    }

    private void startWorking() {
        try {
            Message processStart = new Message(currentProcessName, this.clientName, MessageType.ProcessRequest);

            sendMessage(processStart);

            logger.log(Level.INFO,this.clientName + ": All products and instruments have arrived, starting work on " + currentProcessName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error while starting work", e);
        }
    }

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
