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
    private String clientName;
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
                        System.out.println(messageReceived);
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
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

// public void sendMessage() {
// try {
// writeStringToBufferedWriter(clientName);

// Scanner scanner = new Scanner(System.in);
// while (socket.isConnected()) {
// String messageToSend = scanner.nextLine();
// writeStringToBufferedWriter(messageToSend);
// }
// scanner.close();
// } catch (IOException e) {
// closeEverything(socket, bufferedReader, bufferedWriter);
// }

// }

// public static void main(String[] args) {
// Scanner scanner = new Scanner(System.in);
// try {
// System.out.println("Enter your name: ");
// String clientName = scanner.nextLine();
// Socket socket = new Socket("localhost", 5001);
// Client client = new Client(socket, clientName);
// client.listenForMessages();
// String input = scanner.nextLine();
// while (!input.equals("exit")) {
// client.sendMessage(input);
// }
// } catch (IOException e) {
// e.printStackTrace();
// }
// scanner.close();
// }