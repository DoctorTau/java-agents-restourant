package com.agents.models;

import com.agents.Client;
import com.agents.Dish;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.Date;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Process extends Client {
    private final String orderName;
    private final Dish dish;
    private String cookerName;

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

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
