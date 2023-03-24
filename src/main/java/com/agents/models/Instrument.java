package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Instrument extends Client {
    public Instrument(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
