package Server;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.net.*;
public
class Server {

    static Vector<ClientHandler> clientHandlerVector = new Vector<>();

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

            String received = dis.readUTF();
            String[] msgSplit     = received.split("#");
            switch (msgSplit[0]) {
                case "LOGIN":
                    dos.writeUTF(DBconnection.RetriveData(msgSplit[1], msgSplit[2]));
                    break;
                case "SIGNUP":
                    dos.writeUTF(DBconnection.SavingData(msgSplit[1], msgSplit[2]));
                    break;
                default:
                    // Create a new handler object for handling this request.
                    ClientHandler client = new ClientHandler(s, dis.readUTF(), dis, dos);

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
        }
    }
}

