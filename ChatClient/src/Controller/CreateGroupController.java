package Controller;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public
class CreateGroupController {

    final static int ServerPort = 1234;

    private String userName;

    @FXML
    public JFXListView<CheckBox> groupMember;

    public
    void setUsername(String username) {
        userName = username;
    }

    public
    void createGroup() throws IOException {
        StringBuilder createGroup = new StringBuilder("GROUP_CREATE#");
        createGroup.append("GROUP_NAME#");
        createGroup.append(userName);
        for (CheckBox checkBox : groupMember.getItems()) {
            if (checkBox.isSelected()) {
                createGroup.append(",").append(checkBox.getText());
            }
        }

        InetAddress      ip  = InetAddress.getByName("localhost");
        Socket           s   = new Socket(ip, ServerPort);
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        try {
            dos.writeUTF(createGroup.toString());
            s.close();
            groupMember.getScene().getWindow().hide();
        } catch (IOException e) {
            s.close();
        }
    }
}
