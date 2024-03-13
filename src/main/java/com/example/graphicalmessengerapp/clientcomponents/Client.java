package com.example.graphicalmessengerapp.clientcomponents;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // Connection End-Point
    public String clientUsername;
    public ClientTransceiver clientTransceiver;

    public Client(Socket socket) {
        //clientUsername = username;
        clientTransceiver = new ClientTransceiver(socket);
    }

    public void setClientUsername(String username) {
        clientUsername = username;
        clientTransceiver.setClientUsername(username);
    }

    public void sendMessage(String message) {
        clientTransceiver.sendMessage(message);
    }

    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter your username: ");
            String username = scan.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket);
            client.setClientUsername(username);
            client.clientTransceiver.receiveMessage();
            client.clientTransceiver.sendMessageContinuous();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
