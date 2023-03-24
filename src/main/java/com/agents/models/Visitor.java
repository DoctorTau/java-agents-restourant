package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Visitor extends Client {

    public Visitor(Socket socket, String clientName) {
        super(socket, clientName);

        askForTheMenu();
    }

    @Override
    handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void askForTheMenu() {
        // TODO: asks administrator for the current menu
    }

    private void makeAnOrder(Message message) {
        // TODO: gets menu from the message and chooses some dishes -- sends that list to the administator
    }

    private void getAnOrder(Message message) {
        // TODO: gets and order from the administrator
    }
}
