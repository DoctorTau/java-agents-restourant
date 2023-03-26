package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.Objects;

public class Instrument extends Client {
    // private boolean isFree;
    private String name;
    public Instrument(Socket socket, String clientName, String name) {
        super(socket, clientName);

        // isFree = true;
        this.name = name;
        askForTheWork();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            /* case WorkRespond:
                getAWork(message);
                break; */
            case ProcessRespond:
                // isFree = true;
                askForTheWork();
                break;
            /* case InstrumentsRequest:
                respondRequest(message.getSource());
                break; */
            default:
                break;
        }
    }

    /* private void getAWork(Message message) {
        try {
            Message instrumentResponse = new Message(message.getData(), this.clientName, MessageType.InstrumentsRespond);

            sendMessage(instrumentResponse);
            // isFree = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void respondRequest(String asker) {
        try {
            Message respondRequest = new Message(asker, this.clientName, MessageType.InstrumentsRespond, isFree ? "true" : "false");

            sendMessage(respondRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    } */

    private void askForTheWork() {
        try {
            // isFree = true;
            Message workRequest = new Message(AgentNames.KITCHEN, this.clientName, MessageType.InstrumentsRespond, name);

            sendMessage(workRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }
}
