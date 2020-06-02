package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateGroupController implements Initializable  {

    final static int ServerPort = 1234;

    @FXML
    private JFXTextField groupName;

    @FXML
    private JFXTextField alert;

    @FXML
    public JFXListView<CheckBox> groupMember;

    private static final StringBuilder createGroup = new StringBuilder("GROUP_CREATE");
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
    }

    public
    void createGroup() throws IOException {
        if (groupName.getText() != null && !groupName.getText().trim().isEmpty()) {
            alert.setVisible(false);
            for (CheckBox checkBox : groupMember.getItems()) {
                if (checkBox.isSelected()) {
                    createGroup.append("#").append(checkBox.getText());
                }
            }

            InetAddress ip = InetAddress.getByName("localhost");

            Socket           s   = new Socket(ip, ServerPort);
            DataInputStream  dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            try {
                dos.writeUTF(createGroup.toString());
                if (dis.readUTF().equals("CORRECT")) {
                    s.close();
                }
            } catch (IOException e) {
                s.close();
            }
        }
        else {
            alert.setVisible(true);
        }
    }
}
