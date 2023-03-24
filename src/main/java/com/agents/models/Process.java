package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Process extends Client {
    public Process(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
