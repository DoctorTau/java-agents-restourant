package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.Product;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Cooker extends Client {
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
                getAWork(message);
                break;
            case DishRequestRespond:
                askForProductsAndInstruments(message);
                break;
            case InstrumentsRespond:
                getAProductOrAnInstrument();
                break;
            case ProcessRespond:
                currentProcessName = "";
                countOfNeededProductsAndInstruments = 0;
                askForTheWork();
                break;
            case ProductRespond:
                getProduct(message);
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

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
                Message instrumentRequest = new Message(AgentNames.KITCHEN, this.clientName,
                        MessageType.InstrumentsRequest, instrument);

                sendMessage(instrumentRequest);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void getAProductOrAnInstrument() {
        --countOfNeededProductsAndInstruments;
        if (countOfNeededProductsAndInstruments == 0) {
            startWorking();
        }
    }

    private void askForPoduct(Product product) {
        try {
            // Sends a request for the product to the storage. In request body is the
            // product's id.
            Message message = new Message(AgentNames.STORAGE, this.clientName, MessageType.ProductRequest,
                    product.getId());
            sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProduct(Message message) {
        products.add(new Product(message.getData()));
    }

    private void startWorking() {
        try {
            Message processStart = new Message(currentProcessName, this.clientName, MessageType.ProcessRequest);

            sendMessage(processStart);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void askForTheWork() {
        try {
            Message workRequest = new Message(AgentNames.KITCHEN, this.clientName, MessageType.WorkRequest);

            sendMessage(workRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }
}
