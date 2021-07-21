package ClientFunctions;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static Server.Security.clientHash;
import static Server.databaseCommands.ReadProperties;
import static Server.databaseCommands.getConnection;

public class User implements Serializable {
    private String userName;
    private String password;
    private String hashedPW;
    private String permission;
    private int UserID;

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermission() {
        return permission;
    }

    public String getHash() { return hashedPW; }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getUserID(){return UserID;}

    public void setUserID(int UserID){this.UserID = UserID;}

    /**
     * Constructor to create a User object with specified permissions.
     * @param userName
     * @param password
     * @param permission defines users ability to access and change elements of the billboards.
     * @throws Exception When permission is not a valid permission.
     */
    public User(String userName, String password, String permission) throws Exception {
        this.userName = userName;
        this.password = password;
        this.hashedPW = clientHash(password);

        if(permission == "Create Billboards" || permission == "Edit All Billboards"
        || permission == "Schedule Billboards" || permission == "Edit Users" || permission == "Administrator"){
            this.permission = permission;
        } else {
            throw new Exception("Invalid permission type: " + permission);
        }
    }

    //TODO: JavaDocs
    public User(String userName, String permission,int UserID){
        this.userName = userName;
        this.permission = permission;
        this.UserID = UserID;
    }

    public User(String userName, String password, String permission,int UserID){
        this.userName = userName;
        this.password = password;
        this.hashedPW = clientHash(password);
        this.permission = permission;
        this.UserID = UserID;
    }

    //TODO: JavaDocs and code comment
    public void sendUser() throws Exception {


        Properties networkProps = ReadProperties("./network.props");
        String address = networkProps.getProperty("network.address");
        int port = Integer.parseInt(networkProps.getProperty("network.port"));
        Socket socket = new Socket(address,port);

        OutputStream outputStream = socket.getOutputStream();

        ObjectOutputStream oos = new ObjectOutputStream(outputStream);

        oos.writeObject("incominguser");
        oos.flush();

        oos.writeObject(this);
        oos.flush();

        oos.close();
        socket.close();
    }

    //TODO: JavaDocs code comment
    public static ArrayList<User> requestUserlist() throws Exception {

        Properties networkProps = ReadProperties("./network.props");
        String address = networkProps.getProperty("network.address");
        int port = Integer.parseInt(networkProps.getProperty("network.port"));
        Socket socket = new Socket(address,port);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        oos.writeObject("getUsers");
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        ArrayList<User> Userlist = (ArrayList<User>) ois.readObject();

        oos.close();
        ois.close();
        socket.close();

        return Userlist;
    }


}
