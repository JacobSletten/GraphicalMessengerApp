package com.example.graphicalmessengerapp;

import com.example.graphicalmessengerapp.clientcomponents.ClientTransceiver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientLoginController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private ClientTransceiver clientTransceiver;

    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;

    /*
    public ClientLoginController() throws IOException {
        System.out.println("Building Controller");
        Socket socket = new Socket("localhost", 1234);
        //client = new Client(socket);
        clientTransceiver = new ClientTransceiver(socket);
        stage = (Stage)loginAnchor.getScene().getWindow();

        stage.setOnCloseRequest(event -> {
            clientTransceiver.shutdownClient();
        });

    }*/
    public void setStage(Stage stage) {
        this.stage = stage;
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
        String password = password_field.getText();
        boolean status = false;

        clientTransceiver.sendCredentials(username, password);
        System.out.println("Credentials sent!");
        status = clientTransceiver.waitForResponse();
        if (status) {
            System.out.println("Status is true");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
            root = loader.load();

            ClientUIController clientUiController = loader.getController();
            clientUiController.passClientTransceiver(clientTransceiver);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Messenger (" + username + ")");
            stage.show();
        } else {
            System.out.println("status is not true");
        }
    }

    @FXML
    public void createAccountButtonPressed(ActionEvent event) throws IOException {
        String username = username_field.getText();
        String password = password_field.getText();
        boolean status = false;

        clientTransceiver.sendNewAccount(username, password);
        System.out.println("Credentials sent!");
        status = clientTransceiver.waitForResponse();
        if (status) {
            System.out.println("Status is true");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
            root = loader.load();

            ClientUIController clientUiController = loader.getController();
            clientUiController.passClientTransceiver(clientTransceiver);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Messenger (" + username + ")");
            stage.show();
        } else {
            System.out.println("status is not true");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Building Controller");
            Socket socket = new Socket("192.168.38.175", 1234);
            clientTransceiver = new ClientTransceiver(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
