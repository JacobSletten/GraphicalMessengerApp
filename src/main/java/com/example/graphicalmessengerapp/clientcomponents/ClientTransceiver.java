package com.example.graphicalmessengerapp.clientcomponents;

import com.example.graphicalmessengerapp.ExtractionFunction;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * ClientTransceiver handles all Client Side Networking Elements
 */
public class ClientTransceiver {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Boolean status = false;

    /**
     * Initializes the client socket and
     * establishes input and output buffers.
     *
     * @param socket the client's socket
     */
    public ClientTransceiver(Socket socket) {
        try {
            this.clientSocket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public boolean getStatus() {
        return status;
    }
    public String getClientUsername() {
        return clientUsername;
    }
    public void setClientUsername(String username) {
        clientUsername = username;
    }

    public void shutdownClient(){
        try {
            if (clientSocket != null) {
                clientSocket.close();
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

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(clientUsername + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public void sendLoginCredentials(String user, String pass) throws IOException {
        sendCredentials(user, pass, "Login");
    }

    public void sendNewAccountCredentials(String user, String pass) throws IOException {
        sendCredentials(user, pass, "Create");
    }

    public String waitForResponse() {
        String msgFromSvr = "Invalid";
        try {
            System.out.println("Waiting for response...");
            msgFromSvr = bufferedReader.readLine();
            System.out.println("Response from Server: " + msgFromSvr);
        } catch (IOException e) {
            shutdownClient();
        }
        return msgFromSvr;
    }

    public void sendMessageContinuous(){
        try {
            bufferedWriter.write(clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scan = new Scanner(System.in);
            while (clientSocket.isConnected()) {
                String message = scan.nextLine();
                bufferedWriter.write(clientUsername+ ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            shutdownClient();
        }

    }

    public void receiveMessageWithHook(ExtractionFunction func) {
        new Thread(() -> {
            String msgFromSvr;
            while (clientSocket.isConnected()) {
                try {
                    msgFromSvr = bufferedReader.readLine();
                    func.getData(msgFromSvr);
                } catch (IOException e) {
                    shutdownClient();
                }
            }
        }).start();
    }

    public void receiveMessage() {
        new Thread(() -> {
                String msgFromSvr;
                while (clientSocket.isConnected()) {
                    try {
                        msgFromSvr = bufferedReader.readLine();
                        System.out.println(msgFromSvr);
                    } catch (IOException e) {
                        shutdownClient();
                    }
                }
        }).start();
    }

    private void sendCredentials(String user, String pass, String header) throws IOException {
        clientUsername = user;
        bufferedWriter.write(header);
        bufferedWriter.newLine();
        bufferedWriter.write(user);
        bufferedWriter.newLine();
        bufferedWriter.write(pass);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
