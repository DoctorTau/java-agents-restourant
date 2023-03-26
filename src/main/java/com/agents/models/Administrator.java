package com.agents.models;

import com.agents.AgentNames;
import com.agents.Client;
import com.agents.Message;
import com.agents.MessageType;
import com.agents.VisitorMenu;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.Socket;

public class Administrator extends Client {

    private static final Logger logger = Logger.getLogger(Administrator.class.getName());

    public Administrator(String clientName, int port) {
        super(clientName, port);
    }

    public Administrator(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        try {
            switch (message.getType()) {
                case Ping:
                    logger.log(Level.INFO, "Ping from " + message.getSource() + " received");
                    break;
                case MenuRequest:
                    requestMenuFromStorage(message);
                    break;
                case MenuRespond:
                    provideMenuToTheClient(message);
                    break;
                case OrderRequest:
                    createOrder(message);
                case OrderRespond:
                    giveOrderToTheClient(message);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while handling message", e);
        }
    }

    private void requestMenuFromStorage(Message message) {
        Message menuRequest = new Message(AgentNames.STORAGE, AgentNames.ADMIN, MessageType.MenuRequest,
                message.getSource());
        sendMessage(menuRequest);
    }

    /**
     * Provides menu to the client.
     * 
     * @param message
     * @throws JsonProcessingException
     */
    private void provideMenuToTheClient(Message message) {
        try {
            VisitorMenu visitorMenu = VisitorMenu.fromJson(message.getData());
            Message menuResponse = new Message(visitorMenu.getVisitorName(), AgentNames.ADMIN,
                    MessageType.MenuRespond,
                    visitorMenu.getMenu().toJson());
            sendMessage(menuResponse);
            logger.log(Level.INFO, "Sent menu response to the client " + visitorMenu.getVisitorName()
                    + " with the following menu: " + visitorMenu.getMenu().toJson());
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.SEVERE, "An error occurred while creating order for the visitor " + message.getSource(),
                    e);
        }
    }

    private void giveOrderToTheClient(Message message) {
        Message orderResponse = new Message(message.getData(), AgentNames.ADMIN, MessageType.OrderRespond,
                message.getSource());
        sendMessage(orderResponse);
    }
}
