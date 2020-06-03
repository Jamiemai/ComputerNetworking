package Server;

import com.mysql.cj.xdevapi.Client;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.net.*;

public
class Server {

    static Vector<ClientHandler> clientHandlerVector = new Vector<>();
    static Vector<GroupHandler> groupHandlerVector = new Vector<>();
    public static
    void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        while (true) {
            // Accept the incoming request
            s = ss.accept();

            // obtain input and output streams
            DataInputStream  dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            try {
                String   received = dis.readUTF();
                String[] msgSplit = received.split("#", 3);
                switch (msgSplit[0]) {
                    case "LOGIN":
                        dos.writeUTF(DBconnection.GetUserData(msgSplit[1], msgSplit[2]));
                        break;
                    case "SIGNUP":
                        dos.writeUTF(DBconnection.SaveUserData(msgSplit[1], msgSplit[2]));
                        break;
                    case "GROUP_CREATE":
                        GroupHandler groupHandler = new GroupHandler(msgSplit[2]);
                        groupHandlerVector.add(groupHandler);
                        String[] tmpSplit = msgSplit[2].split(",");
                        for (String tmp : tmpSplit) {
                            for (ClientHandler clientHandler : clientHandlerVector) {
                                if (clientHandler.name.equals(tmp)) {
                                    groupHandler.clientHandlerVector.add(clientHandler);
                                    break;
                                }
                            }
                        }
                        for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                            clientHandler.AddGroup(groupHandler.groupName);
                        }
                        break;
                    case "NEW_CLIENT":
                        // Create a new handler object for handling this request.
                        ClientHandler client = new ClientHandler(s, msgSplit[1], dis, dos);

                        for (ClientHandler clientHandler : clientHandlerVector) {
                            clientHandler.AddOnlineClient(client.name);
                        }

                        client.AddAllOnlineClient();

                        // add new client to active clients list
                        clientHandlerVector.add(client);

                        // Create a new Thread with this object.
                        Thread t = new Thread(client);

                        // start the thread.
                        t.start();
                        break;
                }
            } catch (IOException e) {
                s.close();
            }
        }
    }
}
class GroupHandler {
    public Vector<ClientHandler> clientHandlerVector = new Vector<>();
    public String groupName;

    public
    GroupHandler(String groupName) {
        this.groupName = groupName;
    }
}
