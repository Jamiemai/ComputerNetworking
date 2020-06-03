package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

import java.util.ResourceBundle;
import java.util.stream.Collectors;

public
class ChatController implements Initializable {

    final static int ServerPort = 1234;

    private String selectedUser = null;

    @FXML
    private JFXTextField message;

    @FXML
    private JFXTextField userName;

    @FXML
    private JFXListView<String> chatBox;

    @FXML
    private JFXListView<String> onlineList;

    private DataInputStream dis;
    private DataOutputStream dos;


    @Override
    public
    void initialize(URL arg0, ResourceBundle arg1) {
        onlineList.setOnMouseClicked(event -> {
            String str = onlineList.getSelectionModel().getSelectedItem();
            if (str != null && ! str.equals(selectedUser)) {
                selectedUser = str;
                try {
                    chatBox.getItems().clear();
                    if (selectedUser.indexOf(',') != -1) {
                        dos.writeUTF("GROUPCHAT_DISPLAY#" + str);
                    }
                    else {
                        String tmp = this.userName.getText();
                        if (tmp.compareTo(selectedUser) > 0) {
                            dos.writeUTF("CHAT_DISPLAY#" + tmp + "#" + selectedUser);
                        } else {
                            dos.writeUTF("CHAT_DISPLAY#" + selectedUser + "#" + tmp);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        InetAddress ip = null;

        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Socket s = null;
        try {
            s = new Socket(ip, ServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert s != null;
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket finalS = s;
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    // break the string into message and recipient part
                    String[] msgSplit     = msg.split("#", 2);
                    Platform.runLater(() -> { // for java.lang.IllegalStateException: Not on FX application thread
                    switch (msgSplit[0]) {
                        case "NEW_USER":
                            onlineList.getItems().add(msgSplit[1]);
                            break;
                        case "NEW_GROUP":
                            onlineList.getItems().add(msgSplit[1].replace("#", ", "));
                            break;
                        case "ALL_USER":
                            String[] userString = msgSplit[1].split("#");
                            for (String user : userString) {
                                onlineList.getItems().add(user);
                            }
                            break;
                        case "REMOVE_USER":
                           onlineList.getItems().remove(msgSplit[1]);
                            break;
                        case "CHAT_DISPLAY":
                            chatBox.getItems().clear();
                            String[] tmpArray = msgSplit[1].split("#");
                            for (String str : tmpArray)
                                chatBox.getItems().add(str);
                            break;
                        default:
                            if (selectedUser != null && selectedUser.equals(msgSplit[0]))
                                chatBox.getItems().add(msgSplit[0] + ": " + msgSplit[1]);
                    }
                    });
                } catch (IOException e) {
                    try {
                        finalS.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        readMessage.start();
    }

    @FXML
    void buttonSend() {
        sendMessage();
    }

    @FXML
    public
    void textBoxSend(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER)
            sendMessage();
    }

    private
    void sendMessage() {
        try {
            if (selectedUser != null) {
                if (selectedUser.indexOf(',') != -1) {
                    String from    = userName.getText();
                    String to      = selectedUser;
                    String yourMsg = from + ": " + message.getText();
                    dos.writeUTF("GROUP_CHAT#" + from + "#" + to + "#" + yourMsg);
                    message.clear();
                }
                else {
                    String from    = userName.getText();
                    String to      = selectedUser;
                    String yourMsg = from + ": " + message.getText();

                    dos.writeUTF("CHAT#" + from + "#" + to + "#" + yourMsg);
                    chatBox.getItems().add(yourMsg);
                    message.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public
    void setUsername(String username) throws IOException {
        userName.setText(username);
        dos.writeUTF("NEW_CLIENT#" + userName.getText());
    }

    public
    void createGroupChat() throws IOException {
        Stage          createGroup     = new Stage();
        FXMLLoader     loader     = new FXMLLoader(getClass().getResource("/FXML/CreateGroup.fxml"));
        Scene          scene      = new Scene(loader.load());
        CreateGroupController controller = loader.getController();
        controller.setUsername(userName.getText());
        String tmp = getOnlineUser();
        if (tmp != null && !tmp.trim().isEmpty()) {
            String[] onlineUser = tmp.split("#");
            for (String user : onlineUser) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(user);
                controller.groupMember.getItems().add(checkBox);
            }
        }
        createGroup.setScene(scene);
        createGroup.show();
        createGroup.setResizable(false);
    }

    public
    String getOnlineUser() {
        return onlineList.getItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining("#"));
    }
}
