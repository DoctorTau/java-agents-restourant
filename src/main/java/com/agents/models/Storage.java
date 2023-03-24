package com.agents.models;

import com.agents.Client;

import java.net.Socket;

public class Storage extends Client {
    public Storage(Socket socket, String clientName) {
        super(socket, clientName);
    }
}
