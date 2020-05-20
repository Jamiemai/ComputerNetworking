package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import DBConnection.DBHandler;
public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private ImageView progress;

    @FXML
    private JFXButton login;

    @FXML
    private JFXTextField alert;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
        handler = new DBHandler();
    }

    @FXML
    public void loginAction(ActionEvent e) throws SQLException, ClassNotFoundException {
        progress.setVisible(true);

        //Retrive Data from Database
        connection = handler.getConnection();
        String q1 = "SELECT * from chatDB where username= ? and password= ?";
        try {
            pst = connection.prepareStatement(q1);
            pst.setString(1, username.getText());
            pst.setString(2, password.getText());
            ResultSet rs = pst.executeQuery();

            int count = 0;

            while (rs.next()) {
                count = count + 1;
            }

            if (count == 1) {
                PauseTransition pt = new PauseTransition();
                pt.setDuration(Duration.seconds(1));
                pt.setOnFinished(ev -> {
                    try {
                        chatUIDisplay();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
                pt.play();
            } else {
                alert.setVisible(true);
                progress.setVisible(false);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally{
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    @FXML
    public void signUpDisplay(ActionEvent e1) throws IOException {
        login.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUpUI.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }
    
    public void chatUIDisplay() throws IOException {
        login.getScene().getWindow().hide();

        Stage chatUI = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/ChatUI.fxml"));
        Scene scene = new Scene(root);
        chatUI.setScene(scene);
        chatUI.show();
        chatUI.setResizable(false);
    }
}
