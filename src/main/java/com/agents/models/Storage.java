package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;
import java.util.ArrayList;

public class Storage extends Client {
    ArrayList<Product> products;
    public Storage(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void provideCurrentMenu(Message message) {
        // TODO: gets a menu from the message, checks if the dish creation is possible,
        //  sends a list of possible dishes back to the message sender

    }

    private void removeAProduct(Message message) {
        // TODO: gets a requisted product id from the message, removes it from the list
    }
}
