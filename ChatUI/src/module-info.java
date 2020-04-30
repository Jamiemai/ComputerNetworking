module ChatUI {
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.jfoenix;
    opens Controller to javafx.fxml;
    opens Application;
}