package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Visitor extends Client {
    private static final Logger logger = Logger.getLogger(Visitor.class.getName());

    public Visitor(String clientName, int port) {
        super(clientName, port);

        logger.log(Level.INFO, "Visitor object created with client name " + clientName);
    }

    public Visitor(Socket socket, String clientName) {
        super(socket, clientName);

        logger.log(Level.INFO, "Visitor object created with client name " + clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case MenuRespond:
                try {
                    makeInOrderByMenu(message);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, this.clientName + ": Failed to make an order", e);
                }
                break;
            case OrderRespond:
                // Get an order for the customer
                getAnOrder();
                break;
            default:
                break;
        }
    }

    /**
     * Sends gets a menu from message and makes an order by it.
     * 
     * @param message a message with a menu
     */
    private void makeInOrderByMenu(Message message) {
        try {
            Menu currentMenu = Menu.fromJson(message.getData());
            logger.log(Level.INFO, this.clientName + ": Menu response received with menu " + currentMenu);
            makeAnOrder(currentMenu);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to make an order", e);
        }
    }

    /**
     * Sends a request for the menu to the admin.
     */
    public void askForTheMenu() {
        try {
            Message menuRequest = new Message(AgentNames.ADMIN, this.clientName, MessageType.MenuRequest);
            sendMessage(menuRequest);
            logger.log(Level.INFO, this.clientName + ": Menu request message sent to " + AgentNames.ADMIN);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send message", e);
        }
    }

    /**
     * Makes an order by the menu.
     * 
     * @param menu a menu
     */
    private void makeAnOrder(Menu menu) {
        Random random = new Random();
        ArrayList<Dish> dishes = menu.getDishes();
        if (dishes.size() == 0) {
            logger.log(Level.INFO, this.clientName + ": No dishes in the menu. Client leaving.");
            this.finishClient();
            return;
        }
        int countOfDishes = random.nextInt(0, Math.min(5, dishes.size() + 1));
        Menu order = new Menu();

        for (int i = 0; i < countOfDishes; ++i) {
            int dishIndex = random.nextInt(dishes.size());
            order.addDish(dishes.get(dishIndex));
            dishes.remove(dishIndex);
        }

        if (order.getDishes().size() == 0) {
            logger.log(Level.INFO, this.clientName + ": No dishes in the order. Client leaving.");
            this.finishClient();
            return;
        }

        try {
            Message orderRequest = new Message(AgentNames.ADMIN, this.clientName, MessageType.OrderRequest,
                    order.toJson());

            sendMessage(orderRequest);

            logger.log(Level.INFO,
                    this.clientName + ": Order request message sent to " + AgentNames.ADMIN + " with order "
                            + order.toJson());
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Failed to send message", e);
        }
    }

    private void getAnOrder() {
        logger.log(Level.INFO, this.clientName + ": Order response received \n Client is happy and leaving!!!");
    }
}
