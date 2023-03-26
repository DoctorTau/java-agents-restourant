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
    ArrayList<String> instrumentsList;

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
                // Ask the cooker to get a process
                getAProcess(message.getData());
                break;
            case WorkRequest:
                // Ask the cooker to get a cooker
                getACooker(message.getSource());
                break;
            case InstrumentsRequest:
                // Ask the cooker to get an instrument
                getAnInstrumentRequestFromTheCooker(message.getData(), message.getSource());
                break;
            case InstrumentsRespond:
                // Ask the cooker to get an instrument
                getAnInstrument(message.getSource());
                break;
            default:
                break;
        }
    }

    /**
     * Ask the cooker to get a process
     * 
     * @param processName the name of the process
     */
    private void getAProcess(String processName) {
        if (cookerQueue.isEmpty()) {
            processQueue.add(processName);
        } else {
            provideAProcess(processName, cookerQueue.remove());
        }
    }

    /**
     * Ask the cooker to get a cooker
     * 
     * @param cookerName the name of the cooker
     */
    private void getACooker(String cookerName) {
        if (processQueue.isEmpty()) {
            cookerQueue.add(cookerName);
        } else {
            provideAProcess(processQueue.remove(), cookerName);
        }
    }

    /**
     * Provide a process to the cooker
     * 
     * @param processName the name of the process
     * @param cookerName  the name of the cooker
     */
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

    /**
     * Ask the cooker to get an instrument
     * 
     * @param instrumentName the name of the instrument
     */
    private void getAnInstrument(String instrumentName) {
        if (cookersToInstrumentsQueues.get(instrumentName).isEmpty()) {
            instrumentsList.add(instrumentName);
        } else {
            Queue<String> cookers = cookersToInstrumentsQueues.get(instrumentName);
            String cookerName = cookers.remove();
            cookersToInstrumentsQueues.put(instrumentName, cookers);
            provideAnInstrument(instrumentName, cookerName);
        }
    }

    /**
     * Ask the cooker to get an instrument
     * 
     * @param instrumentName the name of the instrument
     * @param cookerName     the name of the cooker
     */
    private void getAnInstrumentRequestFromTheCooker(String instrumentName, String cookerName) {
        if (!instrumentsList.contains(instrumentName)) {
            Queue<String> cookers = cookersToInstrumentsQueues.get(instrumentName);
            cookers.add(cookerName);
            cookersToInstrumentsQueues.put(instrumentName, cookers);
        } else {
            provideAnInstrument(instrumentName, cookerName);
        }
    }

    /**
     * Provide an instrument to the cooker
     * 
     * @param instrumentName the name of the instrument
     * @param cookerName     the name of the cooker
     */
    private void provideAnInstrument(String instrumentName, String cookerName) {
        try {
            Message instrumentRespond = new Message(cookerName, this.clientName, MessageType.InstrumentsRespond);
            sendMessage(instrumentRespond);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    /*
     * private void provideAnInstrument(Message message) {
     * try {
     * CookersToInstrumentsQueues.get(message.getData()).add(message.getSource());
     * 
     * Message instrumentRequest = new Message(message.getData(), this.clientName,
     * MessageType.InstrumentsRequest);
     * sendMessage(instrumentRequest);
     * } catch (Exception e) {
     * System.out.println(e.getMessage());
     * }
     * }
     * 
     * private void getRespondFromAnInstrument(Message message) {
     * /* try {
     * if (Objects.equals(message.getData(), "true")) {
     * Message instrumentGiveAway = new
     * Message(CookersToInstrumentsQueues.get(message.getSource()).remove(),
     * this.clientName, MessageType.InstrumentsRespond);
     * 
     * sendMessage(instrumentGiveAway);
     * }
     * } catch (Exception e) {
     * System.out.println(e.getMessage());
     * }
     * }
     */
}
