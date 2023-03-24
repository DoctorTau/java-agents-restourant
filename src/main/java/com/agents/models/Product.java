package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Product extends Client {
    public Product(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
