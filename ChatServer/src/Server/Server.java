package Server;

import java.io.*;
<<<<<<< HEAD
import java.sql.SQLException;
=======
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
import java.util.*;
import java.net.*;
public
class Server {

<<<<<<< HEAD
    static Vector<ClientHandler> clientHandlerVector = new Vector<>();

    public static
    void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
=======
public
class Server {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    public static
    void main(String[] args) throws IOException {
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
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
                String[] msgSplit = received.split("#");
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

<<<<<<< HEAD
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
=======
            // Create a new handler object for handling this request.
            ClientHandler client = new ClientHandler(s, dis.readUTF(), dis, dos);

            for (ClientHandler clt : Server.ar) { //notify current users new user
                clt.AddOnlineClient(client.name);
            }

            client.AddAllOnlineClient();

            // add new client to active clients list
            ar.add(client);

            // Create a new Thread with this object.
            Thread t = new Thread(client);

            // start the thread.
            t.start();
        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable {
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    // constructor
    public
    ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
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
                String[] msgSplit     = received.split("#", 2);

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(msgSplit[0]))
                    {
                        mc.dos.writeUTF(this.name + "#" + msgSplit[1]);
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
                        break;
                }
            } catch (IOException e) {
<<<<<<< HEAD
                s.close();
=======
                try {
                    Server.ar.remove(this);
                    this.s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
            }
        }
    }
<<<<<<< HEAD
}

=======

    public
    void AddOnlineClient(String name) throws IOException {
        this.dos.writeUTF("NEW_USER#" + name);
    }

    public
    void AddAllOnlineClient() throws IOException {
        StringBuilder msg = new StringBuilder("ALL_USER#");
        for (ClientHandler clt : Server.ar) {
            msg.append(clt.name).append("#");
        }
        if(!msg.toString().equals("ALL_USER#")) { //first user
            this.dos.writeUTF(msg.toString());
        }
    }
}
>>>>>>> 9e0efa7f153e021534124382b935208db9d6a090
