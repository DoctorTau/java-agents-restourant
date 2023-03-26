package com.agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientName = bufferedReader.readLine();
            clients.add(this);
            // broadcastAll("SERVER " + clientName + " has entered the server.");

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void broadcastAll(String messageToSend) {
        try {
            Message message = Message.fromJson(messageToSend);
            for (ClientHandler clientHandler : clients) {
                try {
                    if (message.getDestination().equals("") && !clientHandler.clientName.equals(this.clientName)) {
                        writeToWriteredBuffer(messageToSend, clientHandler);
                    } else if (clientHandler.clientName.equals(message.getDestination())) {
                        writeToWriteredBuffer(messageToSend, clientHandler);
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void writeToWriteredBuffer(String messageToSend, ClientHandler clientHandler) throws IOException {
        clientHandler.bufferedWriter.write(messageToSend);
        clientHandler.bufferedWriter.newLine();
        clientHandler.bufferedWriter.flush();
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastAll(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void removeClientHandler() {
        clients.remove(this);
        broadcastAll("SERVER " + this.clientName + " has left the server.");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
