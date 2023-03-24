package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Visitor extends Client {

    public Visitor(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
