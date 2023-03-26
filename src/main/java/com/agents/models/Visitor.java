package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Visitor extends Client {

    public Visitor(Socket socket, String clientName) {
        super(socket, clientName);

        askForTheMenu();
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case MenuRespond:
                // Make an order by the menu
                makeInOrderByMenu(message);
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
            makeAnOrder(currentMenu);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sends a request for the menu to the admin.
     */
    private void askForTheMenu() {
        try {
            Message menuRequest = new Message(AgentNames.ADMIN, this.clientName, MessageType.MenuRequest);

            sendMessage(menuRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        int countOfDishes = random.nextInt(Math.min(5, dishes.size()));
        Menu order = new Menu();

        for (int i = 0; i < countOfDishes; ++i) {
            int dishIndex = random.nextInt(dishes.size());
            order.addDish(dishes.get(dishIndex));
            dishes.remove(dishIndex);
        }

        try {
            Message orderRequest = new Message(AgentNames.ADMIN, this.clientName, MessageType.OrderRequest,
                    order.toJson());

            sendMessage(orderRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void getAnOrder() {
        // TODO log order ready.
    }
}
