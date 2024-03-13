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

    public void setClientUsername(String username) {
        clientUsername = username;
    }

    private void shutdownClient(){
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

    public void recieveMessageWithHook(ExtractionFunction func) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromSvr;
                while (clientSocket.isConnected()) {
                    try {
                        msgFromSvr = bufferedReader.readLine();
                        func.getData(msgFromSvr);
                    } catch (IOException e) {
                        shutdownClient();
                    }
                }
            }
        }).start();
    }


    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromSvr;
                while (clientSocket.isConnected()) {
                    try {
                        msgFromSvr = bufferedReader.readLine();
                        System.out.println(msgFromSvr);
                    } catch (IOException e) {
                        shutdownClient();
                    }
                }
            }
        }).start();
    }
}
