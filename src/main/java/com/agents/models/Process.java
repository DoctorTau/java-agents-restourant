package com.agents.models;

import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Process extends Client {
    private final String orderName;
    private final Dish dish;
    private String cookerName;

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
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

            for (String instrument : dish.getInstruments()) {
                processEndNotification.setDestination(instrument);
                sendMessage(processEndNotification);
            }

            processEndNotification.setDestination(orderName);
            sendMessage(processEndNotification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }
}
