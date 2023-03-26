package com.agents.models;

import com.agents.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Order extends Client {
    private static final Logger logger = Logger.getLogger(Order.class.getName());

    private int countOfProcessesInWork;
    private final String visitorName;

    public Order(Socket socket, String clientName, String visitorName) {
        super(socket, clientName);

        this.visitorName = visitorName;
    }

    @Override
    protected void handleMessage(Message message) {
        if (!Objects.equals(message.getDestination(), this.clientName)) {
            return;
        }
        switch (message.getType()) {
            case OrderRequest:
                sendOrderToTheKitchen(message);
                sendOrderToStorage(message);
                break;
            case ProcessRespond:
                processIsDone();
                break;
            default:
                break;
        }
    }

    private void sendOrderToTheKitchen(Message message) {
        try {
            logger.log(Level.INFO, this.clientName + ": Sending order to kitchen");
            Menu menu = Menu.fromJson(message.getData());
            ArrayList<Dish> dishes = menu.getDishes();
            countOfProcessesInWork = dishes.size();

            for (Dish dish : dishes) {
                String processName = dish.getName() + "For" + visitorName;
                Process process = new Process(socket, processName, this.clientName, dish);
                new Thread(process).start();

                Message processAdd = new Message(AgentNames.KITCHEN, this.clientName, MessageType.ProcessRequest,
                        processName);

                sendMessage(processAdd);
                logger.log(Level.INFO, this.clientName + ": Sent process request for " + processName + " to kitchen");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error sending order to kitchen", e);
        }
    }

    private void sendOrderToStorage(Message message) {
        try {
            logger.log(Level.INFO, this.clientName + ": Sending order to storage");
            Message orderNotification = new Message(AgentNames.STORAGE, this.clientName, MessageType.OrderRequest,
                    message.getData());

            sendMessage(orderNotification);
            logger.log(Level.INFO, this.clientName + ": Sent order request to storage");
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error sending order to storage", e);
        }
    }

    private void processIsDone() {
        --countOfProcessesInWork;
        logger.log(Level.INFO, this.clientName + ": Received process completion notification, " + countOfProcessesInWork + " processes remaining");
        if (countOfProcessesInWork == 0) {
            orderIsReady();
        }
    }

    private void orderIsReady() {
        try {
            logger.log(Level.INFO, this.clientName + ": Order is ready");
            Message orderNotification = new Message(AgentNames.ADMIN, this.clientName, MessageType.OrderRespond,
                    visitorName);

            sendMessage(orderNotification);
            logger.log(Level.INFO, this.clientName + ": Sent order response to admin");
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.clientName + ": Error sending order response to admin", e);
        }
    }
}
