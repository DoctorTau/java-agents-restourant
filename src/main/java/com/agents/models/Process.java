package com.agents.models;

import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.MyLogger;

import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class Process extends Client {
    private final String orderName;
    private final Dish dish;
    private String cookerName;
    private final Logger logger;

    public Process(String clientName, int port, String orderName, Dish dish) {
        super(clientName, port);
        this.orderName = orderName;
        this.dish = dish;
        this.logger = Logger.getLogger(Process.class.getName());
    }

    /**
     * @param socket     a socket to communicate with the server
     * @param clientName a name of the client
     * @param orderName  a name of the order
     * @param dish       a dish to cook
     */
    public Process(Socket socket, String clientName, String orderName, Dish dish) {
        super(socket, clientName);
        this.orderName = orderName;
        this.dish = dish;
        this.logger = MyLogger.getLogger();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case DishRequestRespond:
                // Send a message to the cooker to inform him that the dish is ready to be
                // cooked
                nameDishToCooker(message.getSource());
                break;
            case ProcessRequest:
                // Send a message to the cooker to inform him that the process has started
                started(message.getSource());
                break;
            default:
                break;
        }
    }

    /**
     * Sends a message to the cooker to inform him that the dish is ready to be
     * cooked
     * 
     * @param cookerName a name of the cooker
     */
    private void nameDishToCooker(String cookerName) {
        this.cookerName = cookerName;
        try {
            Message dishRespond = new Message(cookerName, this.clientName, MessageType.DishRequestRespond,
                    dish.toJson());

            sendMessage(dishRespond);
            logger.log(Level.INFO, this.clientName + ": Sent dish request to " + cookerName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send dish request to " + cookerName, e);
        }
    }

    /**
     * Sends a message to the cooker to inform him that the process has started
     * 
     * @param cookerName a name of the cooker
     */
    private void started(String cookerName) {
        this.cookerName = cookerName;
        try {
            long time = dish.getTime();
            sleep(time);
            logger.log(Level.INFO,
                    this.clientName + ": Finished cooking dish " + dish.getName() + " for order " + orderName);
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    this.clientName + ": Failed to cook dish " + dish.getName() + " for order " + orderName, e);
        }
        ended();
    }

    /**
     * Sends a message to the order to inform him that the process has ended
     */
    private void ended() {
        try {
            Message processEndNotification = new Message(cookerName, this.clientName, MessageType.ProcessRespond);
            sendMessage(processEndNotification);
            logger.log(Level.INFO, this.clientName + ": Sent process end notification to " + cookerName);

            for (String instrument : dish.getInstruments()) {
                processEndNotification.setDestination(instrument);
                sendMessage(processEndNotification);
                logger.log(Level.INFO, this.clientName + ": Sent process end notification to " + instrument);
            }

            processEndNotification.setDestination(orderName);
            sendMessage(processEndNotification);
            logger.log(Level.INFO, this.clientName + ": Sent process end notification to order " + orderName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send process end notification", e);
        }
    }
}
