package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Cooker extends Client {
    public Cooker(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {// TODO
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

    private void askForTheInstrument(Message message) {
        // TODO: asks administrator for the instrument
    }

    private void askForTheWork() {
        // TODO: asks administrator for the work process
    }
}
