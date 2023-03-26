package com.agents.models;

import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Kitchen extends Client {
    private final Logger logger = Logger.getLogger(Kitchen.class.getName());

    Queue<String> processQueue;
    Queue<String> cookerQueue;
    Map<String, Queue<String>> cookersToInstrumentsQueues;
    Map<String, Queue<String>> instrumentsQueues;

    public Kitchen(Socket socket, String clientName) {
        super(socket, clientName);

        cookersToInstrumentsQueues = new HashMap<>();
        processQueue = new LinkedList<>();
        cookerQueue = new LinkedList<>();
        instrumentsQueues = new HashMap<>();
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
            logger.log(Level.INFO, "Added process {0} to process queue", processName);
        } else {
            provideAProcess(processName, cookerQueue.remove());
        }
    }

    private void getACooker(String cookerName) {
        if (processQueue.isEmpty()) {
            cookerQueue.add(cookerName);
            logger.log(Level.INFO, "Added cooker {0} to cooker queue", cookerName);
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

            logger.log(Level.INFO, "Provided process {0} to cooker {1}", new Object[]{processName, cookerName});
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while providing process {0} to cooker {1}", new Object[]{processName, cookerName});
        }
    }

    private void getAnInstrument(String instrumentAgentName, String instrumentName) {
        if (cookersToInstrumentsQueues.get(instrumentName).isEmpty()) {
            Queue<String> instrumentAgents = instrumentsQueues.get(instrumentName);
            instrumentAgents.add(instrumentAgentName);
            instrumentsQueues.put(instrumentName, instrumentAgents);

            logger.log(Level.INFO, "Added instrument agent {0} to instrument queue {1}", new Object[]{instrumentAgentName, instrumentName});
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

            logger.log(Level.INFO, "Added cooker {0} to the queue for the instrument {1}", new Object[]{cookerName, instrumentName});
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
            logger.log(Level.INFO, "Provided an instrument " + instrumentName + " to " + cookerName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error providing an instrument " + instrumentName + " to " + cookerName, e);
        }
    }
}
