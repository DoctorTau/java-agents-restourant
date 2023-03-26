package com.agents.models;

import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.*;

public class Kitchen extends Client {
    Queue<String> processQueue;
    Queue<String> cookerQueue;
    Map<String, Queue<String>> cookersToInstrumentsQueues;
    Map<String, Queue<String>> instrumentsQueues;

    public Kitchen(Socket socket, String clientName) {
        super(socket, clientName);

        cookersToInstrumentsQueues = new HashMap<>();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case ProcessRequest:
                getAProcess(message.getData());
                break;
            case WorkRequest:
                getACooker(message.getSource());
                break;
            case InstrumentsRequest:
                getAnInstrumentRequestFromTheCooker(message.getSource(), message.getData());
                break;
            case InstrumentsRespond:
                getAnInstrument(message.getSource(), message.getData());
                break;
            default:
                break;
        }
    }

    private void getAProcess(String processName) {
        if (cookerQueue.isEmpty()) {
            processQueue.add(processName);
        } else {
            provideAProcess(processName, cookerQueue.remove());
        }
    }

    private void getACooker(String cookerName) {
        if (processQueue.isEmpty()) {
            cookerQueue.add(cookerName);
        } else {
            provideAProcess(processQueue.remove(), cookerName);
        }
    }

    private void provideAProcess(String processName, String cookerName) {
        try {
            Message workRespond = new Message(cookerName, this.clientName, MessageType.WorkRespond,
                    processName);

            Message processLink = new Message(processName, this.clientName, MessageType.ProcessRequest,
                    cookerName);

            sendMessage(workRespond);
            sendMessage(processLink);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void getAnInstrument(String instrumentAgentName, String instrumentName) {
        if (cookersToInstrumentsQueues.get(instrumentName).isEmpty()) {
            Queue<String> instrumentAgents = instrumentsQueues.get(instrumentName);
            instrumentAgents.add(instrumentAgentName);
            instrumentsQueues.put(instrumentName, instrumentAgents);
        } else {
            Queue<String> cookers = cookersToInstrumentsQueues.get(instrumentName);
            String cookerName = cookers.remove();
            cookersToInstrumentsQueues.put(instrumentName, cookers);
            provideAnInstrument(instrumentAgentName, cookerName);
        }
    }

    private void getAnInstrumentRequestFromTheCooker(String cookerName, String instrumentName) {
        if (instrumentsQueues.get(instrumentName).isEmpty()) {
            Queue<String> cookers = cookersToInstrumentsQueues.get(instrumentName);
            cookers.add(cookerName);
            cookersToInstrumentsQueues.put(instrumentName, cookers);
        } else {
            Queue<String> instrumentAgents = instrumentsQueues.get(instrumentName);
            String instrumentAgentName = instrumentAgents.remove();
            instrumentsQueues.put(instrumentName, instrumentAgents);
            provideAnInstrument(instrumentAgentName, cookerName);
        }
    }

    private void provideAnInstrument(String instrumentName, String cookerName) {
        try {
            Message instrumentRespond = new Message(cookerName, this.clientName, MessageType.InstrumentsRespond);
            sendMessage(instrumentRespond);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    /* private void provideAnInstrument(Message message) {
        try {
            CookersToInstrumentsQueues.get(message.getData()).add(message.getSource());

            Message instrumentRequest = new Message(message.getData(), this.clientName, MessageType.InstrumentsRequest);
            sendMessage(instrumentRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getRespondFromAnInstrument(Message message) {
        /* try {
            if (Objects.equals(message.getData(), "true")) {
                Message instrumentGiveAway = new Message(CookersToInstrumentsQueues.get(message.getSource()).remove(), this.clientName, MessageType.InstrumentsRespond);

                sendMessage(instrumentGiveAway);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    } */
}
