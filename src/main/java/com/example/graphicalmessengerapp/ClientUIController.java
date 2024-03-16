package com.example.graphicalmessengerapp;

import com.example.graphicalmessengerapp.clientcomponents.Client;
import com.example.graphicalmessengerapp.clientcomponents.ClientTransceiver;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientUIController implements Initializable {

    private final ObservableList<String> messages = FXCollections.observableArrayList();

    private ClientTransceiver clientTransceiver;

    private ExtractionFunction getData = (s) -> messages.add(s);

    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;

    @FXML // Messenger Widgets
    private Button send_button;
    @FXML
    private TextField message_field;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;

    public void passClientTransceiver(ClientTransceiver transceiver) {
        System.out.println("Passing Transceiver");
        this.clientTransceiver = transceiver;
        clientTransceiver.receiveMessageWithHook(getData);
    }

    @FXML
    public void sendButtonPressed(ActionEvent event) {
        String message = message_field.getText();
        clientTransceiver.sendMessage(message);
        message_field.clear();
    }
    @FXML
    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            String message = message_field.getText();
            clientTransceiver.sendMessage(message);
            message_field.clear();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messages.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Text text = new Text(messages.get(messages.size() - 1));
                        TextFlow textFlow = new TextFlow(text);
                        HBox hbox = new HBox();
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setPadding(new Insets(2,2,2,2));
                        hbox.getChildren().add(textFlow);
                        vbox_messages.getChildren().add(hbox);

                        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                                sp_main.setVvalue((Double) newValue);
                            }
                        });
                    }
                });
            }
        });

    }
}