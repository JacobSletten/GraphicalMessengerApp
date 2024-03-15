module com.example.graphicalmessengerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.graphicalmessengerapp to javafx.fxml;
    exports com.example.graphicalmessengerapp;
    exports com.example.graphicalmessengerapp.servercomponents;
    opens com.example.graphicalmessengerapp.servercomponents to javafx.fxml;
    exports com.example.graphicalmessengerapp.clientcomponents;
    opens com.example.graphicalmessengerapp.clientcomponents to javafx.fxml;
}