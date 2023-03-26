package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;

public class Administrator extends Client {

    public Administrator(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        try {
            switch (message.getType()) {// TODO
                case MenuRequest:
                    requestMenuFromStorage(message);
                    break;
                case MenuRespond:
                    provideMenuToTheClient(message);
                    break;
                case OrderRequest:
                    createOrder(message);
                default:
                    break;
            }
        } catch (JsonProcessingException je) {
            System.out.println("Error while parsing json");
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestMenuFromStorage(Message message) {
        Message menuRequest = new Message(AgentNames.STORAGE, AgentNames.ADMIN, MessageType.MenuRequest,
                message.getSource());
        sendMessage(menuRequest);
    }

    private void provideMenuToTheClient(Message message) throws JsonProcessingException {
        VisitorMenu visitorMenu = VisitorMenu.fromJson(message.getData());
        Message menuResponse = new Message(visitorMenu.getVisitorname(), AgentNames.ADMIN,
                MessageType.MenuRespond,
                visitorMenu.getMenu().toJson());
        sendMessage(menuResponse);
    }

    private void createOrder(Message message) {
        try {
            String orderName = message.getSource() + "Order";
            Order order = new Order(this.socket, orderName, message.getSource());
            new Thread(order);

            Message orderRequest = new Message(orderName, this.clientName, MessageType.OrderRequest, message.getData());
            sendMessage(orderRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void giveOrderToTheClient(Message message) {
        // TODO: sends notification that order is ready to the client
    }
}
