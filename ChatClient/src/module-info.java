module ChatClient {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.jfoenix;
<<<<<<< HEAD
    requires mysql.connector.java;
=======
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
    opens Controller to javafx.fxml;
    opens Client;
}