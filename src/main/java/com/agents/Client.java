package com.agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    protected String clientName;
    private Message messageToSend;

    private final Object messageToSendLock = new Object();

    public Client(Socket socket, String clientName) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientName = clientName;
            this.messageToSend.setSource(clientName);
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String destination, MessageType messageType, String data) {
        this.messageToSend.setDestination(destination);
        this.messageToSend.setSource(this.clientName);
        this.messageToSend.setType(messageType);
        this.messageToSend.setData(data);

        try {
            synchronized (messageToSendLock) {
                messageToSendLock.notify();
                messageToSendLock.wait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        this.messageToSend = message;
        try {
            synchronized (messageToSendLock) {
                messageToSendLock.notify();
                messageToSendLock.wait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void sendMessage(String message) {
        try {
            messageToSend.setData(message);
            synchronized (messageToSendLock) {
                messageToSendLock.notify();
                messageToSendLock.wait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (bufferedWriter) {
                        writeStringToBufferedWriter(clientName);

                        while (socket.isConnected()) {
                            synchronized (messageToSendLock) {
                                messageToSendLock.wait();
                                writeStringToBufferedWriter(messageToSend.toJson());
                                messageToSendLock.notify();
                            }
                        }
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                } catch (InterruptedException e) {
                    // handle InterruptedException
                }
            }
        }).start();
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        String messageReceived = bufferedReader.readLine();
                        Message message = Message.fromJson(messageReceived);
                        handleMessage(message);
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    protected void handleMessage(Message message) {
        System.out.println(clientName + " received message from " + message.getSource() + ": " + message.getData());
    }

    private void writeStringToBufferedWriter(String messageToSend) throws IOException {
        bufferedWriter.write(messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
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

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        String clientName;
        if (args.length == 1) {
            clientName = args[0];
        } else {
            clientName = scanner.nextLine();
        }
        try {
            System.out.println("Enter your name: ");
            Socket socket = new Socket("localhost", 5001);
            final Client client = new Client(socket, clientName);
            client.listenForMessages();
            client.sendMessage();
            String message = scanner.nextLine();
            while (!"exit".equals(message)) {
                client.sendMessage(message);
                message = scanner.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    @Override
    public void run() {
        main(new String[0]);
    }
}