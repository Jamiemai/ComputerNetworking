package Server;

import DBConnection.DBHandler;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.net.*;

public class Server
{

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    public static void main(String[] args) throws IOException {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        Connection connection;
        DBHandler handler = null;
        PreparedStatement pst;

        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s,"client ", dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            // add this client to active clients list
            ar.add(mtch);

            // start the thread.
            t.start();
        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean online;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.online=true;
    }

    @Override
    public void run() {
        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.online)
                    {
                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {
                try {
                    this.s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }
    }
} 
