package com.agents.models;

import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;

public class Kitchen extends Client {
    Queue<String> processQueue;
    ArrayList<String> instruments;

    public Kitchen(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case ProcessRequest:
                getAProcess(message.getData());
            case WorkRequestRespond:
                provideAProcess(message.getSource());
            default:
                break;
        }
    }

    private void getAProcess(String processName) {
        processQueue.add(processName);
    }

    private void provideAProcess(String askerForTheWork) {
        try {
            Message workRespond = new Message(askerForTheWork, this.clientName, MessageType.WorkRequestRespond,
                    processQueue.peek());

            // TODO: provide info about process' time to the asker

            Message processLink = new Message(processQueue.remove(), this.clientName, MessageType.ProcessRequest,
                    askerForTheWork);

            sendMessage(workRespond);
            sendMessage(processLink);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void provideAnInstrument(Message message) {
        // TODO: provides an instrument to the asker and removes it from the array list
    }

    private void getAnInstrument(Message message) {
        // TODO: adds an instrument to the array list
    }
}
