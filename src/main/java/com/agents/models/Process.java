package com.agents.models;

import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;

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

    public Process(Socket socket, String clientName, String orderName, Dish dish) {
        super(socket, clientName);
        this.orderName = orderName;
        this.dish = dish;
        this.logger = Logger.getLogger(Process.class.getName());
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case DishRequestRespond:
                nameDishToCooker(message.getSource());
                break;
            case ProcessRequest:
                started(message.getSource());
                break;
            default:
                break;
        }
    }

    private void nameDishToCooker(String cookerName) {
        this.cookerName = cookerName;
        try {
            Message dishRespond = new Message(cookerName, this.clientName, MessageType.DishRequestRespond, dish.toJson());
            sendMessage(dishRespond);
            logger.log(Level.INFO, this.clientName + ": Sent dish request to " + cookerName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send dish request to " + cookerName, e);
        }
    }

    private void started(String cookerName) {
        this.cookerName = cookerName;
        try {
            long time = dish.getTime();
            sleep(time);
            logger.log(Level.INFO, this.clientName + ": Finished cooking dish " + dish.getName() + " for order " + orderName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to cook dish " + dish.getName() + " for order " + orderName, e);
        }
        ended();
    }

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
