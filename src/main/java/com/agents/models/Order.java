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

                Message processAdd = new Message();
                processAdd.setDestination("Kichen"); // TODO: implement normal kitchen name
                processAdd.setSource(this.clientName);
                processAdd.setType(MessageType.ProcessRequest);
                processAdd.setData(dish.toJson());

                sendMessage(processAdd.toJson());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
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
            Message orderNotification = new Message();
            orderNotification.setSource(this.clientName);
            orderNotification.setDestination("Administrator"); // TODO: implement normal administrator name
            orderNotification.setType(MessageType.OrderRespond);
            orderNotification.setData(visitorName);

            sendMessage(orderNotification.toJson());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // TODO
        }
    }
}
