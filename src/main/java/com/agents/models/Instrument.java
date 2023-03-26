package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Instrument extends Client {
    private static final Logger logger = Logger.getLogger(Instrument.class.getName());

    private final String name;

    public Instrument(Socket socket, String clientName, String name) {
        super(socket, clientName);
        this.name = name;
        askForTheWork();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case ProcessRespond:
                askForTheWork();
                break;
            default:
                break;
        }
    }

    private void askForTheWork() {
        try {
            Message workRequest = new Message(AgentNames.KITCHEN, this.clientName, MessageType.InstrumentsRespond, name);
            sendMessage(workRequest);
            logger.log(Level.INFO, this.clientName + ": Sent work request from " + " for " + name);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send work request from " + " for " + name, e);
        }
    }
}
