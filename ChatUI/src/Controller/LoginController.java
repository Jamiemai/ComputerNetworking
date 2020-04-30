package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
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
    private JFXButton signup;

    @FXML
    private JFXCheckBox remember;

    @FXML
    private JFXButton forgotpassword;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        progress.setVisible(false);
    }

    @FXML
    public void loginAction(ActionEvent e) {
        progress.setVisible(true);
        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(3));
        pt.setOnFinished(ev -> {
            System.out.print("Login Successfully");
        });
        pt.play();
    }

    @FXML
    public void signUp(ActionEvent e1) throws IOException {
        login.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUpUI.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }
}
