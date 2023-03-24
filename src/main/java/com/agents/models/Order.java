package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Order extends Client {
    private int countOfProcessesInWork;
    public Order(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void sendOrderToTheKitchen(Message message) {
        // TODO: creates processes for each dish (and changes the count var) in the order and sends their name to the kitchen
    }

    private void processIsDone() {
        --countOfProcessesInWork;
        if (countOfProcessesInWork == 0) {
            orderIsReady();
        }
    }

    private void orderIsReady() {
        // TODO: notifies administrator
    }
}
