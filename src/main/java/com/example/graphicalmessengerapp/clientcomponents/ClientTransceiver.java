package com.example.graphicalmessengerapp.clientcomponents;

import com.example.graphicalmessengerapp.ExtractionFunction;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTransceiver {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String clientUsername;
    public Boolean status = false;

    public ClientTransceiver(Socket socket) {
        try {
            this.clientSocket = socket;
            //this.clientUsername = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdownClient();
        }
    }

    public boolean getStatus() {
        return status;
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

    public void sendCredentials(String user, String pass) throws IOException {
        clientUsername = user;
        bufferedWriter.write("Login");
        bufferedWriter.newLine();
        bufferedWriter.write(user);
        bufferedWriter.newLine();
        bufferedWriter.write(pass);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendNewAccount(String user, String pass) throws IOException {
        clientUsername = user;
        bufferedWriter.write("Create");
        bufferedWriter.newLine();
        bufferedWriter.write(user);
        bufferedWriter.newLine();
        bufferedWriter.write(pass);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public boolean waitForResponse() {
        boolean flag = false;
        try {
            System.out.println("Waiting for response...");
            String msgFromSvr = bufferedReader.readLine();
            System.out.println("Response from Server: " + msgFromSvr);
            if (msgFromSvr.equals("LOG:good")) { flag = true; }
        } catch (IOException e) {
            shutdownClient();
        }
        return flag;
    }

    public void sendUsername() {
        try {
            System.out.println("Sending client username:" + clientUsername);
            bufferedWriter.write(clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            shutdownClient();
        }
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
}
