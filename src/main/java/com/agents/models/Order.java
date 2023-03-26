package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Order extends Client {
    private int countOfProcessesInWork;
    private String visitorName;
    private Menu menu;

    public Order(Socket socket, String clientName, String visitorName) {
        super(socket, clientName);

        this.visitorName = visitorName;
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {// TODO
            case OrderRequest:
                sendOrderToTheKitchen(message);
                sendOrderToStorage(message);
            case ProcessRespond:
                processIsDone();
            default:
                break;
        }
    }

    private void sendOrderToTheKitchen(Message message) {
        try {
            menu = Menu.fromJson(message.getData());
            ArrayList<Dish> dishes = menu.getDishes();
            countOfProcessesInWork = dishes.size();

            for (Dish dish : dishes) {
                String processName = dish.getName() + "For" + visitorName;
                Process process = new Process(socket, processName, this.clientName, dish);
                new Thread(process);

                Message processAdd = new Message(AgentNames.KITCHEN, this.clientName, MessageType.ProcessRequest,
                        processName);

                sendMessage(processAdd);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendOrderToStorage(Message message) {
        try {
            Message orderNotification = new Message(AgentNames.STORAGE, this.clientName, MessageType.OrderRequest,
                    message.getData());

            sendMessage(orderNotification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processIsDone() {
        --countOfProcessesInWork;
        if (countOfProcessesInWork == 0) {
            orderIsReady();
        }
    }

    private void orderIsReady() {
        try {
            Message orderNotification = new Message(AgentNames.ADMIN, this.clientName, MessageType.OrderRespond,
                    visitorName);

            sendMessage(orderNotification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
