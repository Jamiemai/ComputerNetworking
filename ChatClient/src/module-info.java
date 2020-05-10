module ChatClient {
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires mysql.connector.java;
    opens Controller to javafx.fxml;
    opens Client;
}