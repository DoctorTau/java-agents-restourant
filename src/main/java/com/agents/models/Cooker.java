package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Cooker extends Client {
    public Cooker(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
