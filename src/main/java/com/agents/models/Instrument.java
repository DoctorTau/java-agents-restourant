package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Instrument extends Client {
    public Instrument(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void getAWork(Message message) {
        // TODO: gets a work process
    }

    private void askForTheWork() {
        // TODO: asks administrator for the work process
    }
}
