package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public
class ChatController implements Initializable {

    final static int ServerPort = 1234;

    private String selectedUser = null;

    @FXML
    private JFXTextField message;

    @FXML
    private ImageView add;
    @FXML
    private ImageView remove;

    @FXML
    private JFXTextField userName;

    @FXML
    private JFXListView<String> chatBox;

    @FXML
    private JFXListView<String> onlineList;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Object[] onlineArray;


    @Override
    public
    void initialize(URL arg0, ResourceBundle arg1) {
        GroupAddRemoveOff();
        onlineList.setOnMouseClicked(event -> {
            String str = onlineList.getSelectionModel().getSelectedItem();
            if (str != null && ! str.equals(selectedUser)) {
                selectedUser = str;
                try {
                    chatBox.getItems().clear();
                    if (selectedUser.indexOf(',') != -1) {
                        GroupAddRemoveOn();
                        dos.writeUTF("GROUP_CHAT_DISPLAY#" + str);
                    }
                    else {
                        GroupAddRemoveOff();
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
                        case "GROUP_CHAT":
                            String[] tmpSplit = msgSplit[1].split("#");
                            if (selectedUser != null && selectedUser.equals(tmpSplit[1]))
                                chatBox.getItems().add(tmpSplit[2]);
                            break;
                        case "GROUP_NAME_CHANGE":
                            String[] temp = msgSplit[1].split("#");
                            Object[] onlineArray = onlineList.getItems().toArray();
                            for (int i = 0; i < onlineArray.length; i++) {
                                if (onlineArray[i].toString().equals(temp[2])) {
                                    onlineList.getItems().set(i, temp[1]);
                                    break;
                                }
                            }
                            break;
                        default:
                            if (selectedUser != null && selectedUser.equals(msgSplit[0]))
                                chatBox.getItems().add(msgSplit[1]);
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
        FXMLLoader     loader     = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene      = new Scene(loader.load());
        GroupController controller = loader.getController();
        controller.setAction("CREATE_GROUP");
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
        String str =  onlineList.getItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining("#"));
        String[] strSplit = str.split("#");
        for (String tmp : strSplit) {
            if (tmp.indexOf(',') != -1) {
                str = str.replace("#" + tmp, "");
            }
        }
        return str;
    }

    public
    void fileSend() {

    }

    public
    void groupAdd() throws IOException {
        Stage          createGroup     = new Stage();
        FXMLLoader     loader     = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene      = new Scene(loader.load());
        GroupController controller = loader.getController();
        controller.setAction("ADD_CLIENT");
        controller.setGroupName(selectedUser);
        String          tmp        = getOnlineUser();
        String[] tmpSplit = tmp.split("#"); // User online
        String[] strSplit = selectedUser.split(","); // User already in group
        System.out.println(tmp);
        System.out.println(selectedUser);
        for (String temp : tmpSplit) {
            if (temp.indexOf(',') == -1) {
                for (String str : strSplit) {
                    if (temp.equals(str)) {
                        tmp = tmp.replace(temp + "#", "");
                        break;
                    }
                }
            }
        }
        tmpSplit = tmp.split("#");
        for (String user : tmpSplit) {
            if (user != null && !user.trim().isEmpty()) {
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
    void groupRemove() throws IOException {
        Stage          createGroup     = new Stage();
        FXMLLoader     loader     = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene      = new Scene(loader.load());
        GroupController controller = loader.getController();
        controller.setAction("REMOVE_CLIENT");
        controller.setGroupName(selectedUser);
        String[]        strSplit   = selectedUser.split(",");
        for (String user : strSplit) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(user);
            controller.groupMember.getItems().add(checkBox);
        }
        createGroup.setScene(scene);
        createGroup.show();
        createGroup.setResizable(false);
    }

    void GroupAddRemoveOn(){
        add.setVisible(true);
        add.setDisable(false);
        remove.setVisible(true);
        remove.setDisable(false);
    }

    void GroupAddRemoveOff(){
        add.setVisible(false);
        add.setDisable(true);
        remove.setVisible(false);
        remove.setDisable(true);
    }
}
