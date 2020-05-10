package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

// Client class
public class ChatUIController implements Initializable {
    final static int ServerPort = 1234;
    @FXML
    private JFXTextField message;

    @FXML
    private JFXListView<String> chatBox;

    private DataInputStream dis;
    private DataOutputStream dos;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)  {
        // getting localhost ip
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // establish the connection
        Socket s = null;
        try {
            s = new Socket(ip, ServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // obtaining input and out streams
        try {
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        chatBox.getItems().add(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        readMessage.start();
    }

    @FXML
    void buttonSend(MouseEvent event) throws IOException {
        sendMessage();
    }

    @FXML
    public void textBoxSend(KeyEvent e) throws IOException {
        if (e.getCode() == KeyCode.ENTER)
            sendMessage();
    }

    private void sendMessage() throws IOException {
        try {
            // write on the output stream
            dos.writeUTF(message.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
