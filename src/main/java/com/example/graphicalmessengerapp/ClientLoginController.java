package com.example.graphicalmessengerapp;

import com.example.graphicalmessengerapp.clientcomponents.ClientTransceiver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class ClientLoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private ClientTransceiver clientTransceiver;

    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;

    public ClientLoginController() throws IOException {
        System.out.println("Building Controller");
        Socket socket = new Socket("localhost", 1234);
        //client = new Client(socket);
        clientTransceiver = new ClientTransceiver(socket);
    }

    @FXML
    public void switchToMessenger(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
        root = loader.load();

        ClientUIController clientUiController = loader.getController();
        clientUiController.passClientTransceiver(clientTransceiver);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Messenger");
        stage.show();
    }

    @FXML
    public void loginButtonPressed(ActionEvent event) throws IOException {
        String username = username_field.getText();
        System.out.println(username);

        this.clientTransceiver.setClientUsername(username);
        System.out.println("Client Username set to: " + clientTransceiver.clientUsername);

        clientTransceiver.sendUsername();
        //clientTransceiver.receiveMessage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
        root = loader.load();

        ClientUIController clientUiController = loader.getController();
        clientUiController.passClientTransceiver(clientTransceiver);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Messenger (" + username + ")");
        stage.show();
    }

    @FXML
    public void createAccountButtonPressed(ActionEvent e) {
        System.out.println("Client Username: " + this.clientTransceiver.clientUsername);
    }
}
