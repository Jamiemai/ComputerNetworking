package Server;

import DBConnection.DBHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public
class DBconnection {

    public static
    String GetUserData(String username, String password) throws SQLException, ClassNotFoundException {
        //Retrive Data from Database
        Connection        connection = DBHandler.getConnection();
        String q1 = "SELECT * FROM chatDB where username= ? and password= ?";
        PreparedStatement pst = connection.prepareStatement(q1);
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            connection.close();
            return "CORRECT";
        }
        connection.close();
        return "FALSE";
    }

    public static
    String SaveUserData(String username, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DBHandler.getConnection();
        PreparedStatement pst;
        // Check Same Username
        String q1 = "SELECT * FROM chatDB where username= ?";
        pst = connection.prepareStatement(q1);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return "FALSE";
        }

        else {
            // Saving Data
            String insert = "INSERT INTO chatDB(username,password)" + "VALUES (?,?)";
            pst = connection.prepareStatement(insert);

            pst.setString(1, username);
            pst.setString(2, password);

            pst.executeUpdate();
            connection.close();

            return "CORRECT";
        }
    }

    public static
    void SaveChatData(String from, String to, String messages) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();

        String q1 = "SELECT * FROM chatHistory where fromClient= ? and toClient= ?";
        PreparedStatement pst = connection.prepareStatement(q1);
        pst.setString(1, from);
        pst.setString(2, to);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            q1 = "UPDATE chatHistory set messages= ? where fromClient= ? and toClient= ?";
            pst = connection.prepareStatement(q1);
            pst.setString(1, from);
            pst.setString(2, to);
            pst.setString(3, messages);

            pst.executeUpdate();

            connection.close();
        }
        else {
            String insert = "INSERT INTO chatHistory(fromClient,toClient,messages)" + "VALUES (?,?,?)";
            pst = connection.prepareStatement(insert);

            pst.setString(1, from);
            pst.setString(2, to);
            pst.setString(3, messages);

            pst.executeUpdate();
            connection.close();
        }
    }
    public static
    String GetChatData(String from, String to) throws SQLException, ClassNotFoundException {
        Connection        connection = DBHandler.getConnection();
        String q1 = "SELECT messages from chatHistory where from= ? and to= ?";
        PreparedStatement pst = connection.prepareStatement(q1);
        pst.setString(1, from);
        pst.setString(2, to);
        ResultSet rs = pst.executeQuery();

        int count = 0;

        while (rs.next()) {
            count = count + 1;
        }
        connection.close();
        if (count == 1) {
            System.out.println(rs.toString());
            return rs.toString();
        }
        return "";
    }
}