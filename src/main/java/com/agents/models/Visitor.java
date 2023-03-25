package com.agents.models;

import com.agents.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Visitor extends Client {
    private String adminName = AgentNames.ADMIN;

    public Visitor(Socket socket, String clientName) {
        super(socket, clientName);

        askForTheMenu();
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {
            case MenuResponse:
                try {
                    Menu currentMenu = Menu.fromJson(message.getData());
                    makeAnOrder(currentMenu);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case OrderGiveAway:
                getAnOrder();
                break;
            default:
                break;
        }
    }

    private void askForTheMenu() {
        // TODO: asks administrator for the current menu
        Message menuRequest = new Message();
        menuRequest.setDestination(adminName);
        menuRequest.setSource(this.clientName);
        menuRequest.setType(MessageType.MenuRequest);

        try {
            sendMessage(menuRequest.toJson());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
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
            Message orderRequest = new Message();
            orderRequest.setSource(this.clientName);
            orderRequest.setDestination(adminName);
            orderRequest.setType(MessageType.OrderRequest);
            orderRequest.setData(order.toJson());

            sendMessage(orderRequest.toJson());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }

    private void getAnOrder() {
        // TODO: gets and order from the administrator and write smth in log
    }
}
