package com.agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    protected int socketPort;
    protected Socket socket = null;
    protected String clientName;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Message messageToSend = new Message();

    private final Object messageToSendLock = new Object();
    protected Thread thread = new Thread(this);

    public Client(String clientName, int port) {
        this.clientName = clientName;
        this.socketPort = port;
    }

    /**
     * @param socket     - socket to connect to the server
     * @param clientName - name of the client
     */
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

    /**
     * Starts the thread of the client.
     */
    public void startClient() {
        thread.start();
    }

    /**
     * Stops the thread of the client.
     */
    public void finishClient() {
        thread.interrupt();
    }

    /**
     * @param destination - destination agent of the message
     * @param messageType - type of the message
     * @param data        - data of the message
     */
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

    /**
     * @param message - message to send
     */
    public void sendMessage(Message message) {
        this.messageToSend = message;
        try {
            synchronized (messageToSendLock) {
                messageToSendLock.notifyAll();
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
                messageToSendLock.notifyAll();
                messageToSendLock.wait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starting a message sending process in a new thread.
     */
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

    /**
     * Starting a message receiving process in a new thread.
     */
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

    /**
     * @param message - message received from the server
     *                Method to handle the received message.
     *                Should be overridden in the child class.
     */
    protected void handleMessage(Message message) {
        System.out.println(clientName + " received message from " + message.getSource() + ": " + message.getData());
    }

    /**
     * @param messageToSend - message to send
     * @throws IOException - exception thrown when writing to the BufferedWriter
     *                     fails
     *                     Method to write a string to the BufferedWriter.
     */
    private void writeStringToBufferedWriter(String messageToSend) throws IOException {
        bufferedWriter.write(messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    /**
     * @param socket         - socket to close
     * @param bufferedReader - bufferedReader to close
     * @param bufferedWriter - bufferedWriter to close
     */
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
        Scanner scanner = new Scanner(System.in);
        try {
            String clientName;
            if (args.length == 0) {
                System.out.println("Enter your name: ");
                clientName = scanner.nextLine();
            } else {
                clientName = args[0];
            }

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
        if (this.socket == null) {
            try {
                this.socket = new Socket("localhost", socketPort);
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.messageToSend.setSource(clientName);

                this.listenForMessages();
                this.sendMessage();

                Message ping = new Message("", clientName, MessageType.Ping, "");
                this.sendMessage(ping);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
}