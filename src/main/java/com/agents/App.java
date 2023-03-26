package com.agents;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;

import com.agents.models.Administrator;
import com.agents.models.Kitchen;

public class App {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            Server server = new Server(serverSocket);
            Thread serverThread = new Thread(server);
            serverThread.start();
            System.out.println("Server started");

            Administrator administrator = new Administrator(AgentNames.ADMIN, 5001);
            Thread clientThread = new Thread(administrator);
            clientThread.start();
            System.out.println("Client 1 started");

            Kitchen kitchen = new Kitchen(AgentNames.KITCHEN, 5001);
            Thread kitcThread = new Thread(kitchen);
            kitcThread.start();
            System.out.println("Client 2 started");

            Thread.sleep(1000);

            Message message = new Message(AgentNames.KITCHEN, AgentNames.ADMIN,
                    MessageType.Ping, "Ping!");
            synchronized (administrator) {
                administrator.sendMessage(message);
            }
            System.out.println("Message sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
