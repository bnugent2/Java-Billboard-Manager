package Server;

import BillboardViewer.Billboard;
import ClientFunctions.User;
import ControlPanel.Schedule;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static Server.Security.ServerEncryption;
import static Server.Server.*;
import static Server.databaseCommands.ReadProperties;
import static Server.databaseCommands.getConnection;

/**
 * The clientHandler class will take the given connection from a server and perform
 * the task that has been given by the client connection.
 *
 * @author Brendan Learoyd
 */
public class clientHandler extends Thread {

    private Socket clientSocket;

    public clientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            // Creates inputstream from the clientSocket provided
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            // Read incoming command from client.
            Object obj = in.readObject();

            System.out.println("[CLIENT REQUEST] " + obj.toString());
            // This block takes care of functions based on commands given.

            // Creates a new User in DB.
            if (obj.toString().contains("incominguser")) {
                User user = (User) in.readObject();
                createUser(user);
            }
            // Sends ArrayList of Users from DB to client.
            else if (obj.toString().contains("getUsers")) {
                clientHandler.this.sleep(100);
                ArrayList<User> usersdata = getUsers();

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                out.writeObject(usersdata);
                out.flush();
                out.close();

            }
            // Completes a login request by comparing the given password hash
            // to the hash stored in the DB.
            else if (obj.toString().contains("loginRequest")) {

                String username = (String) obj;
                username = username.substring(12);

                String clienthash = (String) in.readObject();

                Properties props = ReadProperties("./db.props");
                Connection conn = getConnection(props);

                Statement statement = conn.createStatement();
                String sql = "SELECT * FROM users WHERE username ='" + username + "';";
                ResultSet User = statement.executeQuery(sql);

                // Initialises variables.
                String user = null;
                String dbhash = null;
                String permission = null;
                byte[] dbsalt = null;

                // Extracts results from DB.
                while (User.next()) {
                    user = User.getString("Username");
                    dbhash = User.getString("Password");
                    dbsalt = User.getBytes("Salt");
                    permission = User.getString("Permissions");
                }

                String SE = ServerEncryption(clienthash, dbsalt);

                // Sends Session token if password is correct.
                if (dbhash.equals(SE)) {
                    Session loginUser = new Session();
                    loginUser.setUsername(user);
                    loginUser.setPermission(permission);
                    loginUser.setUUID();
                    ObjectOutputStream response = new ObjectOutputStream(clientSocket.getOutputStream());
                    response.writeObject(loginUser);
                    response.flush();
                    response.close();
                    clientSocket.close();
                    System.out.println("[SERVER] User: '"+user+"' Logged in.");
                }
            }
            // Gets billboard byte[] from server and sends to client.
            else if (obj.toString().equals("BillboardRequest")) {

                String title = (String) in.readObject();

                byte[] billboard = getBillboard(title);

                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(billboard);
                outputStream.flush();
                outputStream.close();
                System.out.println("[SERVER] Billboard: '" + title +"' sent to Client.");
            }
            // Adds user created billboard to DB
            else if (obj.toString().equals("CreateBillboardRequest")) {

                String title = (String) in.readObject();
                Billboard billboard = (Billboard) in.readObject();
                billboard.ExportBillboard("Export.xml");
                uploadBillboard(title, new File("Export.xml"), "admin");
                System.out.println("[SERVER] Billboard: '"+title+"' added to DB.");
            }
            // Gets and ArrayList of Schedules and sends to client.
            else if (obj.toString().equals("GetScheduleRequest")) {
                clientHandler.this.sleep(100);
                ArrayList<Schedule> ScheduleList = getSchedule();

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.writeObject(ScheduleList);
                outputStream.flush();
                outputStream.close();
                System.out.println("[SERVER] Schedules sent to client.");
            }
            // Gets a ArrayList of the billboard names and IDs and sends to client.
            else if (obj.toString().equals("GetBillboardList")) {
                clientHandler.this.sleep(100);
                ArrayList<String[]> BillboardList = getBillboards();

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.writeObject(BillboardList);
                outputStream.flush();
                outputStream.close();
                System.out.println("[SERVER] Billboard Titles sent to client.");
            }
            // Uploads the user schedule to the DB
            else if (obj.toString().equals("CreateScheduleRequest")) {
                Schedule schedule = (Schedule) in.readObject();
                scheduleBillboard(schedule);
                System.out.println("[SERVER] Billboard: '" + schedule.getBillboardTitle() + "' scheduled for the " + schedule.getDate() + " at " + schedule.getStartTime());
            }
            // Deletes user from DB
            else if (obj.toString().equals("DeleteUserRequest")) {
                int UserID = Integer.parseInt(in.readObject().toString());
                deleteUser(UserID);
                System.out.println("[SERVER] UserID: '"+UserID+"' Deleted from DB");
            }
            // Updates user in DB
            else if (obj.toString().equals("EditUserRequest")) {
                User user = (User) in.readObject();
                editUser(user);
                System.out.println("[SERVER] User: '"+user.getUserName()+"' Updated.");
            }
            // Updates one of the billboards in the DB
            else if (obj.toString().equals("UpdateBillboardRequest")) {
                int billboardID = (int) in.readObject();
                Billboard billboard = (Billboard) in.readObject();
                billboard.ExportBillboard("ExportTest.xml");
                updateBillboard(billboardID, new File("ExportTest.xml"));
                System.out.println("[SERVER] BillboardID: '"+billboardID+"' Updated");
            }
            // Deletes billboard from DB
            else if (obj.toString().equals("DeleteBillboardRequest")) {
                int billboardID = (int) in.readObject();
                deleteBillboard(billboardID);
                System.out.println("[SERVER] BillboardID: '"+billboardID+"' Deleted.");
            }

            in.close();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
