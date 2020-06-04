package Server;

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
        getGroup();
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
                    case "GROUP_ADD_CLIENT":
                        for (GroupHandler groupHandler : groupHandlerVector) {
                            if(groupHandler.groupName.equals(msgSplit[1])) {
                                String   newClient   = msgSplit[2].replace(msgSplit[1] + ',', "");
                                String[] clientSplit = newClient.split(",");
                                for (ClientHandler clientHandler : clientHandlerVector) {
                                    for (String tmp : clientSplit) {
                                        if(clientHandler.name.equals(tmp)) {
                                            clientHandler.AddGroup(msgSplit[2]);
                                            break;
                                        }
                                    }
                                }
                                groupHandler.groupName = msgSplit[2];
                                DBconnection.changeGroupName(groupHandler.groupName, msgSplit[1]);
                                for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                                    clientHandler.changeGroupName(groupHandler.groupName, msgSplit[1]);
                                }
                                break;
                            }
                        }
                        break;
                    case "GROUP_REMOVE_CLIENT":
                        String[] tempSplit = msgSplit[2].split("#");
                        for (GroupHandler groupHandler : groupHandlerVector) {
                            if(groupHandler.groupName.equals(msgSplit[1])) {
                                groupHandler.groupName = msgSplit[1].replace(tempSplit[1], "");
                                DBconnection.changeGroupName(groupHandler.groupName, msgSplit[1]);
                                String[] removeClient = tempSplit[1].split(",");
                                for (ClientHandler clientHandler : clientHandlerVector) {
                                    for (String tmp : removeClient) {
                                        if(clientHandler.name.equals(tmp)) {
                                            clientHandler.RemoveGroup(msgSplit[1]);
                                            break;
                                        }
                                        else if (clientHandler.name.equals(tempSplit[0])) {
                                            clientHandler.changeGroupName(groupHandler.groupName, msgSplit[1]);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "GROUP_CREATE":
                        GroupHandler groupHandler = new GroupHandler(msgSplit[2]);
                        groupHandlerVector.add(groupHandler);
                        String[] tmpSplit = msgSplit[2].split(",");
                        for (String tmp : tmpSplit) {
                            for (ClientHandler clientHandler : clientHandlerVector) {
                                if(clientHandler.name.equals(tmp)) {
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
                        client.AddJoinedGroup();
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

    private static
    void getGroup() throws SQLException, ClassNotFoundException {
        String[] groupArray = DBconnection.GetGroupData().split("#");
        for (String group : groupArray) {
            GroupHandler groupHandler = new GroupHandler(group);
            groupHandlerVector.add(groupHandler);
        }
    }
}
