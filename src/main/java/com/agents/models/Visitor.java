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
                try {
                    Menu currentMenu = Menu.fromJson(message.getData());
                    makeAnOrder(currentMenu);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case OrderRespond:
                getAnOrder();
                break;
            default:
                break;
        }
    }

    private void askForTheMenu() {
        try {
            Message menuRequest = new Message(AgentNames.ADMIN, this.clientName, MessageType.MenuRequest);

            sendMessage(menuRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

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
        // TODO: gets and order from the administrator and write smth in log
    }
}
