package com.example.graphicalmessengerapp.servercomponents;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println("Server is up!");
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // Wait for a new connection
                System.out.println("A new client has connected!");
                // pass the connection to a connection handler
                ServerTransceiver serverTransceiver = new ServerTransceiver(socket);
                //Create a new thread to independently handle this connection:
                Thread thread = new Thread(serverTransceiver);
                thread.start(); //run the thread
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
