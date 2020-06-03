package Server;

import com.mysql.cj.xdevapi.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

// ClientHandler class
class ClientHandler implements Runnable {
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    // constructor
    public
    ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    @Override
    public
    void run() {
        String received;
        while (true) {
            try {
                // receive the string
                received = dis.readUTF();
                // break the string into message and recipient part
                String[] msgSplit = received.split("#", 2);
                String[] tmpSplit = msgSplit[1].split("#");
                switch (msgSplit[0]) {
                    case "CHAT_DISPLAY":
                        String msg = DBconnection.GetChatData(tmpSplit[0], tmpSplit[1]);
                        if (msg != null)
                            this.dos.writeUTF("CHAT_DISPLAY#" + msg);
                        break;
                    case "GROUPCHAT_DISPLAY":
                        String temp = DBconnection.GetChatData(msgSplit[1], null);
                        if (temp != null)
                            this.dos.writeUTF("CHAT_DISPLAY#" + temp);
                        break;
                    case "GROUP_CHAT":
                        DBconnection.SaveChatData(tmpSplit[1], "null", tmpSplit[2]);
                        for (GroupHandler groupHandler : Server.groupHandlerVector) {
                            for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                                String[] tmpsplit = groupHandler.groupName.split("#");
                                for (String tmp : tmpsplit) {
                                    if (clientHandler.name.equals(tmp)) {
                                        clientHandler.dos.writeUTF(tmpSplit[0] + "#" + tmpSplit[2]);
                                    }
                                }
                            }
                        }
                        break;
                    case "CHAT":
                        if (tmpSplit[0].compareTo(tmpSplit[1]) > 0)
                            DBconnection.SaveChatData(tmpSplit[0], tmpSplit[1], tmpSplit[2]);
                        else
                            DBconnection.SaveChatData(tmpSplit[1], tmpSplit[0], tmpSplit[2]);
                        for (ClientHandler clientHandler : Server.clientHandlerVector) {
                            if (clientHandler.name.equals(tmpSplit[1])) {
                                clientHandler.dos.writeUTF(tmpSplit[0] + "#" + tmpSplit[2]);
                                break;
                            }
                        }
                        break;
                }
            } catch (IOException e) {
                try {
                    this.s.close();
                    Server.clientHandlerVector.remove(this);
                    RemoveClient(this.name);
                    break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    void RemoveClient(String name) throws IOException {
        for (ClientHandler clientHandler : Server.clientHandlerVector) {
            clientHandler.dos.writeUTF("REMOVE_USER#" + name);
        }
    }

    public
    void AddOnlineClient(String name) throws IOException {
        this.dos.writeUTF("NEW_USER#" + name);
    }

    public
    void AddAllOnlineClient() throws IOException {
        StringBuilder msg = new StringBuilder("ALL_USER#");

        for (ClientHandler clientHandler : Server.clientHandlerVector) {
            msg.append(clientHandler.name).append("#");
        }

        if (! msg.toString().equals("ALL_USER#")) { //first user
            this.dos.writeUTF(msg.toString());
        }
    }

    public
    void AddGroup(String groupName) throws IOException {
        this.dos.writeUTF("NEW_GROUP#" + groupName);
    }
}
