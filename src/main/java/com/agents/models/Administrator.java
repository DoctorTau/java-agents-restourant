package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Administrator extends Client {

    private static final Logger logger = Logger.getLogger(Administrator.class.getName());

    public Administrator(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {
            case MenuRequest:
                requestMenuFromStorage(message);
                break;
            case MenuRespond:
                provideMenuToTheClient(message);
                break;
            case OrderRequest:
                createOrder(message);
                break;
            default:
                break;
        }
    }

    private void requestMenuFromStorage(Message message) {
        Message menuRequest = new Message(AgentNames.STORAGE, AgentNames.ADMIN, MessageType.MenuRequest,
                message.getSource());
        sendMessage(menuRequest);
        logger.log(Level.INFO, "Sent menu request to storage agent for visitor " + message.getSource());
    }

    private void provideMenuToTheClient(Message message) {
        try {
            VisitorMenu visitorMenu = VisitorMenu.fromJson(message.getData());
            Message menuResponse = new Message(visitorMenu.getVisitorname(), AgentNames.ADMIN,
                    MessageType.MenuRespond,
                    visitorMenu.getMenu().toJson());
            sendMessage(menuResponse);
            logger.log(Level.INFO, "Sent menu response to the client " + visitorMenu.getVisitorname() + " with the following menu: " + visitorMenu.getMenu().toJson());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while providing menu to the visitor " + message.getSource(), e);
        }
    }

    /**
     * @param message - message with order data
     *                Creates the order agent from visitor's message.
     */
    private void createOrder(Message message) {
        try {
            String orderName = message.getSource() + "Order";
            Order order = new Order(this.socket, orderName, message.getSource());
            order.startClient();

            Message orderRequest = new Message(orderName, this.clientName, MessageType.OrderRequest, message.getData());
            sendMessage(orderRequest);

            logger.log(Level.INFO, "Created and started order agent " + orderName + " for visitor " + message.getSource() + ", and sent order request to it with data: " + message.getData());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while creating the order agent for visitor " + message.getSource(), e);
        }
    }

    private void giveOrderToTheClient(Message message) {
        // TODO: sends notification that order is ready to the client
    }
}
