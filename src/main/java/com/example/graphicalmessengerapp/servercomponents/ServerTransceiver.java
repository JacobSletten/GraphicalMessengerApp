package com.example.graphicalmessengerapp.servercomponents;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerTransceiver implements Runnable {

    public static ArrayList<ServerTransceiver> connections = new ArrayList<>();
    private Socket socket; //client socket
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public static DataAccessObject dao = new DataAccessObject();

    public ServerTransceiver(Socket socket) {
        try  {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void broadcastMessage(String message) {
        for (ServerTransceiver connection: connections) {
            try {
                connection.bufferedWriter.write(message);
                connection.bufferedWriter.newLine();
                connection.bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeConnection() {
        connections.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(
            Socket socket,
            BufferedReader bufferedReader,
            BufferedWriter bufferedWriter
    ) {
        removeConnection();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageLogin() {
        try {
            String user = bufferedReader.readLine();
            String pass = bufferedReader.readLine();
            System.out.println("User:" + user);
            String validation = dao.validateUser(user, pass);
            if (validation.equals("Valid")) {
                acknowledgeCredentials(user);
            } else {
                sendErrorMessage(validation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageAccountCreation() {
        try {
            String user = bufferedReader.readLine();
            String pass = bufferedReader.readLine();
            String validation = dao.createUser(user,pass);
            if (validation.equals("Valid")) {
                acknowledgeCredentials(user);
            } else {
                sendErrorMessage(validation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acknowledgeCredentials(String user) throws IOException {
        clientUsername = user;
        bufferedWriter.write("Valid Login");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        connections.add(this);
        broadcastMessage("SERVER: " + clientUsername + " had entered the chat!");
    }

    public void sendErrorMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()) {
            try { //BLOCKING OPERATION
                messageFromClient= bufferedReader.readLine();
                System.out.println("Received: " + messageFromClient);
                if (messageFromClient.equals("Login")) {
                    manageLogin();
                } else if (messageFromClient.equals("Create") ) {
                    manageAccountCreation();
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
