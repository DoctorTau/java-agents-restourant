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

    public Kitchen(String clientName, int port) {
        super(clientName, port);

        cookersToInstrumentsQueues = new HashMap<>();
        processQueue = new PriorityQueue<>();
        cookerQueue = new PriorityQueue<>();
        instrumentsQueues = new HashMap<>();
    }

    public Kitchen(Socket socket, String clientName) {
        super(socket, clientName);

        cookersToInstrumentsQueues = new HashMap<>();
        processQueue = new PriorityQueue<>();
        cookerQueue = new PriorityQueue<>();
        instrumentsQueues = new HashMap<>();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case Ping:
                logger.log(Level.INFO, "Ping from " + message.getSource() + " received");
                break;
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
                getAnInstrument(message.getSource(), message.getData());
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
            logger.log(Level.INFO, "Added process {0} to process queue", processName);
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
            logger.log(Level.INFO, "Added cooker {0} to cooker queue", cookerName);
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

            logger.log(Level.INFO, "Provided process {0} to cooker {1}", new Object[] { processName, cookerName });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred while providing process {0} to cooker {1}",
                    new Object[] { processName, cookerName });
        }
    }

    /**
     * Ask the cooker to get an instrument
     * 
     * @param instrumentName the name of the instrument
     */
    private void getAnInstrument(String instrumentAgentName, String instrumentName) {
        checkInstrument(instrumentName);
        if (cookersToInstrumentsQueues.get(instrumentName).isEmpty()) {
            Queue<String> instrumentAgents = instrumentsQueues.get(instrumentName);
            instrumentAgents.add(instrumentAgentName);
            instrumentsQueues.put(instrumentName, instrumentAgents);

            logger.log(Level.INFO, "Added instrument agent {0} to instrument queue {1}",
                    new Object[] { instrumentAgentName, instrumentName });
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
    private void getAnInstrumentRequestFromTheCooker(String cookerName, String instrumentName) {
        checkInstrument(instrumentName);
        if (instrumentsQueues.get(instrumentName).isEmpty()) {
            Queue<String> cookers = cookersToInstrumentsQueues.get(instrumentName);
            cookers.add(cookerName);
            cookersToInstrumentsQueues.put(instrumentName, cookers);

            logger.log(Level.INFO, "Added cooker {0} to the queue for the instrument {1}",
                    new Object[] { cookerName, instrumentName });
        } else {
            Queue<String> instrumentAgents = instrumentsQueues.get(instrumentName);
            String instrumentAgentName = instrumentAgents.remove();
            instrumentsQueues.put(instrumentName, instrumentAgents);
            provideAnInstrument(instrumentAgentName, cookerName);
        }
    }

    /**
     * Checks if the instrument was added before, and if so fixes it
     *
     * @param instrumentName the name of the instrument
     */
    private void checkInstrument(String instrumentName) {
        if (!instrumentsQueues.containsKey(instrumentName)) {
            Queue<String> queue = new PriorityQueue<>();
            instrumentsQueues.put(instrumentName, queue);
        }
        if (!cookersToInstrumentsQueues.containsKey(instrumentName)) {
            Queue<String> queue = new PriorityQueue<>();
            cookersToInstrumentsQueues.put(instrumentName, queue);
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
            logger.log(Level.INFO, "Provided an instrument " + instrumentName + " to " + cookerName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // log the error
            logger.log(Level.SEVERE, "Exception occurred while providing an instrument {0} to cooker {1}",
                    new Object[] { instrumentName, cookerName });
        }
    }
}
