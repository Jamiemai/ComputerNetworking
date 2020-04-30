package Controller;

import java.io.IOException;
import java.net.URL;
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

public class SignUpController implements Initializable {
    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private ImageView progress;

    @FXML
    private JFXButton signup;

    @FXML
    private JFXButton login;

    @FXML
    private JFXTextField repassword;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        progress.setVisible(false);
    }

    @FXML
    public void signUpAction(ActionEvent e) {
        progress.setVisible(true);
        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(3));
        pt.setOnFinished(ev -> {
            System.out.print("Sign Up Successfully");
        });
        pt.play();
    }

    @FXML
    public void login(ActionEvent e1) throws IOException {
        signup.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginUI.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
        login.setResizable(false);
    }
}
