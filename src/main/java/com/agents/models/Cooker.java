package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Cooker extends Client {
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
            case ProductRespond:
            case InstrumentsRespond:
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
            Message neededDishRequest = new Message(currentProcessName, this.clientName, MessageType.DishRequestRespond);

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
                Message productRequest = new Message(AgentNames.STORAGE, this.clientName, MessageType.ProductRequest, product.getName());

                sendMessage(productRequest);
            }

            for (String instrument : instruments) {
                Message instrumentRequest = new Message(AgentNames.KITCHEN, this.clientName, MessageType.InstrumentsRequest, instrument);

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
