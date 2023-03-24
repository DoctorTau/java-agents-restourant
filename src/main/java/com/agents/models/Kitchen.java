package com.agents.models;

import com.agents.Client;
import com.agents.Message;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

public class Kitchen extends Client {
    Queue<String> processQueue;
    ArrayList<String> instruments;

    public Kitchen(Socket socket, String clientName) {
        super(socket, clientName);
    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.getType()) {// TODO
            default:
                break;
        }
    }

    private void getAProcess(Message message) {
        // TODO: adds a process to the queue
    }

    private void provideAProcess(Message message) {
        // TODO: gives the upper process from the queue to the asker
    }

    private void provideAnInstrument(Message message) {
        // TODO: provides an instrument to the asker and removes it from the array list
    }

    private void getAnInstrument(Message message) {
        // TODO: adds an instrument to the array list
    }
}
