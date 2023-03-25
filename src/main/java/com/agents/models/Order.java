package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Order extends Client {
    private int countOfProcessesInWork;
    private String visitorName;

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
            case ProcessRespond:
                processIsDone();
            default:
                break;
        }
    }

    private void sendOrderToTheKitchen(Message message) {
        try {
            ArrayList<Dish> order = Menu.fromJson(message.getData()).getDishes();
            countOfProcessesInWork = order.size();

            for (Dish dish : order) {
                String processName = dish.getName() + "For" + visitorName;
                Process process = new Process(socket, processName, this.clientName, dish.getTime());
                new Thread(process);

                Message processAdd = new Message(AgentNames.KITCHEN, this.clientName, MessageType.ProcessRequest,
                        dish.toJson());

                sendMessage(processAdd);
            }

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
