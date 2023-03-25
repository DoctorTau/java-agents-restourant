package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.Product;

import java.net.Socket;
import java.util.ArrayList;

public class Cooker extends Client {
    private ArrayList<Product> products;

    public Cooker(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {// TODO
            case ProductResponse:
                getProduct(message);
                break;
            default:
                break;
        }
    }

    private void getAWork(Message message) {
        // TODO: gets work's length and name from the message
        // askForTheInstrument();
        // while (false) { // waits for the instrument

        // }
        // sleep(0);
        // TODO: returns instrument and ends the work process
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

    private void askForTheInstrument(Message message) {
        // TODO: asks administrator for the instrument
    }

    private void askForTheWork() {
        // TODO: asks administrator for the work process
    }
}
