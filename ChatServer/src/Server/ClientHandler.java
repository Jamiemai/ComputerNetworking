package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
                switch (msgSplit[0]) {
                    case "CHAT_DISPLAY":
                        String[] tmpSplit = msgSplit[1].split("#");
                        String msg = DBconnection.GetChatData(tmpSplit[0], tmpSplit[1]);
                        if (msg != null)
                            this.dos.writeUTF("CHAT_DISPLAY#" + msg);
                        break;
                    case "GROUP_CREATE":

                        break;
                    default:
                        String[] strSplit = msgSplit[1].split("#");
                        if (strSplit[0].compareTo(strSplit[1]) > 0)
                            DBconnection.SaveChatData(strSplit[0], strSplit[1], strSplit[2]);
                        else
                            DBconnection.SaveChatData(strSplit[1], strSplit[0], strSplit[2]);
                        for (ClientHandler clientHandler : Server.clientHandlerVector) {
                            if (clientHandler.name.equals(strSplit[1])) {
                                clientHandler.dos.writeUTF(strSplit[0] + "#" + strSplit[2]);
                                break;
                            }
                        }
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
}
