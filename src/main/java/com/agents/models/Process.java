package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;

public class Process extends Client {
    public Process(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void started(Message message) {
        // TODO:
    }

    private void ended(Message message) {
        // TODO: notifies the order agent about
    }
}
