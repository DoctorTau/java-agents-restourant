package com.agents.models;

import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;

import java.net.Socket;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Process extends Client {
    private String orderName;
    private String cookerName;
    private int time;

    public Process(Socket socket, String clientName, String orderName, int time) {
        super(socket, clientName);

        this.orderName = orderName;
        this.time = time;
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case ProcessRequest:
                started(message.getData());
                // case ProcessRespond:
                // ended();
            default:
                break;
        }
    }

    private void started(String cookerName) {
        // TODO: write smth in the log
        this.cookerName = cookerName;
        // sleep(time); // TODO

        ended();
    }

    private void ended() {
        try {
            Message processEndNotification = new Message(cookerName, this.clientName, MessageType.ProcessRespond);

            sendMessage(processEndNotification);

            processEndNotification.setDestination(orderName);
            sendMessage(processEndNotification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }
}
