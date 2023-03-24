package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Administrator extends Client {

    public Administrator(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void provideMenuToTheClient(Message message) {
        // TODO: asks Storage for the current menu and sends it to the message sender
    }

    private void createOrder(Message message) {
        // Order order = new Order(); // TODO: gets needed dishes from the message
        // new Thread(order);
    }

    private void giveOrderToTheClient(Message message) {
        // TODO: sends notification that order is ready to the client
    }
}
