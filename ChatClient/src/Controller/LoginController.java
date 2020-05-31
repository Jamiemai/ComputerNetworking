package Controller;

<<<<<<< HEAD
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

=======
import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
<<<<<<< HEAD

public class LoginController implements Initializable {

    final static int ServerPort = 1234;

    public JFXButton signup;
    @FXML
    private JFXTextField username;
=======

public
class LoginController {
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090

    @FXML
    private JFXTextField username;

    @FXML
    private JFXButton login;

    @FXML
<<<<<<< HEAD
    private JFXTextField alert;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
    }

    @FXML
    public void loginAction() throws IOException {
        progress.setVisible(true);

        InetAddress ip = null;
        ip = InetAddress.getByName("localhost");

        Socket s = null;
        s = new Socket(ip, ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        try {
            dos.writeUTF("LOGIN#" + username.getText() + "#" + password.getText());
            if (dis.readUTF().equals("CORRECT")) {
                chatUIDisplay();
            } else {
                alert.setVisible(true);
                progress.setVisible(false);
            }
        } catch (IOException e) {
            s.close();
        }
    }

    @FXML
    public void signUpDisplay() throws IOException {
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

        Stage  chatUI = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChatUI.fxml"));
        Scene  scene  = new Scene(loader.load());
        ChatController controller = loader.getController();
        controller.setUsername(username.getText());
=======
    public
    void loginAction(ActionEvent e) throws IOException {
        chatUIDisplay();
    }

    @FXML
    public
    void login(KeyEvent e) throws IOException {
        if (e.getCode() == KeyCode.ENTER)
            chatUIDisplay();
    }

    public
    void chatUIDisplay() throws IOException {
        login.getScene().getWindow().hide();

        Stage  chatUI = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChatUI.fxml"));

        Scene  scene  = new Scene(loader.load());

        ChatUIController controller = loader.getController();
        controller.setUsername(username.getText());

>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
        chatUI.setScene(scene);
        chatUI.show();
        chatUI.setResizable(false);

    }
}
