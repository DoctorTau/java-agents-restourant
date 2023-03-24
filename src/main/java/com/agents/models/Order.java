package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Order extends Client {
    public Order(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
