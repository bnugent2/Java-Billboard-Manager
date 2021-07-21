package Server;

import ClientFunctions.User;
import ControlPanel.Schedule;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Properties;

import static Server.Security.ServerEncryption;
import static Server.Security.generateSalt;
import static Server.databaseCommands.*;


public class Server {

    /**
     * Method to add created user from Control Panel to the database.
     * @param user Object from client
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void createUser(User user) throws SQLException, NoSuchAlgorithmException {

        Properties properties = ReadProperties("./db.props");
        String dbname = properties.getProperty("jdbc.dbname");

        byte[] salt = generateSalt();

        String hash = ServerEncryption(user.getHash(),salt);

        Connection conn = getConnection(properties);

        String sql = "INSERT INTO "+ dbname +".users(Username,Password,Permissions, Salt) VALUES(?,?,?,?)";


        PreparedStatement ps= conn.prepareStatement(sql);
        ps.setString(1,user.getUserName());
        ps.setString(2,hash);
        ps.setString(3,user.getPermission());
        //InputStream inputStream = new ByteArrayInputStream(salt);
        ps.setBytes(4,salt);
        ps.executeUpdate();

        System.out.println("[SERVER] User Created.");
        System.out.println("[SERVER] Username: " + user.getUserName());
        System.out.println("[SERVER] Permissions: " + user.getPermission());
        //System.out.println("[SERVER] UserCreatedSalt: "+salt);

        conn.close();
    }

    /**
     * Method to edit users in the database
     * @param user Object that holds updated information.
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public static void editUser(User user) throws SQLException, NoSuchAlgorithmException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");

        byte[] salt = generateSalt();

        String hash = ServerEncryption(user.getHash(),salt);
        // Create database connection.
        Connection Conn = getConnection(props);

        // Execute SQL statements.
        String sql = "UPDATE "+ dbname +".users SET Username = ?, Password = ?, Permissions = ?, Salt = ? WHERE UserID = ?";
        PreparedStatement statement = Conn.prepareStatement(sql);
        statement.setString(1,user.getUserName());
        statement.setString(2,hash);
        statement.setString(3,user.getPermission());
        statement.setBytes(4,salt);
        statement.setInt(5,user.getUserID());

        statement.executeUpdate();
    }

    /**
     * Method to extract the user information from the database and append it
     * to a 2D Object array.
     * @return UserData
     * @throws Exception
     */
    public static ArrayList getUsers() throws Exception {
        Properties properties = ReadProperties("./db.props");
        String dbname = properties.getProperty("jdbc.dbname");

        try {
            ArrayList<User> UserList = new ArrayList<User>();

            Properties props = ReadProperties("./db.props");
            // Create database connection.
            Connection Conn = getConnection(props);

            // Execute SQL statements.
            Statement st = Conn.createStatement();
            ResultSet User = st.executeQuery("SELECT * From "+ dbname +".users");

            // Adds users to List
            while(User.next()){
                User user = new User(User.getString("Username"),User.getString("Permissions"), User.getInt("UserID"));
                UserList.add(user);
            }

            System.out.println("[SERVER] User list retrieved...");
            return UserList;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Method to delete user with "UserID" from database.
     * @param UserID
     * @throws SQLException
     */
    public static void deleteUser(int UserID) throws SQLException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");
        // Create database connection.
        Connection Conn = getConnection(props);

        // Execute SQL statements.
        Statement st = Conn.createStatement();
        st.executeQuery("DELETE FROM "+ dbname +".users WHERE UserID ="+UserID);
    }

    /**
     * Method to upload XML file to database.
     * @param Title of billboard.
     * @param billboard file containing XML data
     * @param User
     */
    public static void uploadBillboard(String Title,File billboard, String User){
        Properties properties = ReadProperties("./db.props");
        String dbname = properties.getProperty("jdbc.dbname");
        try {

            Properties props = ReadProperties("./db.props");
            // Create database connection.
            Connection Conn = getConnection(props);

            String sql = "INSERT INTO "+ dbname +".billboards(Title, XML, Creator) VALUES(?,?,?)";
            PreparedStatement statement = Conn.prepareStatement(sql);

            statement.setString(1,Title);
            statement.setString(3,User);

            InputStream inputStream = new FileInputStream(billboard);

            statement.setBlob(2,inputStream);
            statement.executeUpdate();
            Conn.close();

        } catch (SQLException e) {

            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method that updates existing billboard in Database.
     * @param BillboardID of billboard to edit.
     * @param billboard XML file to upload.
     */
    public static void updateBillboard(int BillboardID,File billboard){
        Properties properties = ReadProperties("./db.props");
        String dbname = properties.getProperty("jdbc.dbname");
        try {

            Properties props = ReadProperties("./db.props");
            // Create database connection.
            Connection Conn = getConnection(props);

            String sql = "UPDATE "+ dbname +".billboards SET XML = ? WHERE BillboardID =?";
            PreparedStatement statement = Conn.prepareStatement(sql);

            statement.setInt(2,BillboardID);

            InputStream inputStream = new FileInputStream(billboard);

            // Uses Inputstream to write XML to SQL Blob for storage.
            statement.setBlob(1,inputStream);
            statement.executeUpdate();
            Conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to delete billboard from database with specified ID.
     * @param BillboardID
     * @throws SQLException
     */
    public static void deleteBillboard(int BillboardID) throws SQLException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");
        // Create database connection.
        Connection Conn = getConnection(props);

        // Execute SQL statements.
        Statement st = Conn.createStatement();
        st.executeQuery("DELETE FROM "+ dbname +".billboards WHERE BillboardID ="+BillboardID);
    }

    /**
     * Method that sends billboard data from database to client.
     * @param title
     * @return byte[] with XML content
     * @throws IOException
     */
    public static byte[] getBillboard(String title) throws IOException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");
        try{
            // Create database connection.
            Connection Conn = getConnection(props);

            Statement st = Conn.createStatement();
            String sql = "SELECT * FROM "+ dbname +".billboards WHERE title = '"+title+"'";

            ResultSet rs = st.executeQuery(sql);
            //File XML = new File("XMLFile.xml");
            byte[] buffer = new byte[1000];

            while (rs.next()){
                InputStream input = rs.getBinaryStream("XML");
                input.read(buffer);
            }

            return buffer;
        }
        catch (SQLException e){
            System.out.println("Billboard: '"+title+"' does not exist.");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Returns ArrayList with billboard titles and ID's
     * @return
     * @throws IOException
     */
    public static ArrayList<String[]> getBillboards() throws IOException {

        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");

        ArrayList<String[]> BillboardList = new ArrayList<>();
        try{
            // Create database connection.
            Connection Conn = getConnection(props);

            Statement st = Conn.createStatement();
            String sql = "SELECT * FROM "+ dbname +".billboards";

            ResultSet rs = st.executeQuery(sql);
            //File XML = new File("XMLFile.xml");

            while (rs.next()){
                String[] str = new String[]{String.valueOf(rs.getInt("BillboardID")),rs.getString("Title")};

               BillboardList.add(str);
            }

            return BillboardList;
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Schedules a billboard based on the info in the Schedule object.
     * @param schedule
     * @throws SQLException
     */
    public static void scheduleBillboard(Schedule schedule) throws SQLException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");

        // Create database connection.
        Connection Conn = getConnection(props);

        Statement st = Conn.createStatement();
        String sql = "INSERT INTO "+ dbname +".schedule(Date,TimeStart,TimeEnd,Title,Duration) VALUES(?,?,?,?,?)";
        PreparedStatement statement = Conn.prepareStatement(sql);

        LocalTime time = schedule.getStartTime().toLocalTime();
        Time EndTime = Time.valueOf(time.plusMinutes(schedule.getDuration()));

        statement.setDate(1,schedule.getDate());
        statement.setTime(2,schedule.getStartTime());
        statement.setTime(3,EndTime);
        statement.setString(4,schedule.getBillboardTitle());
        statement.setInt(5,schedule.getDuration());

        statement.executeUpdate();
    }

    /**
     * Gets the current scheduled billboard from database.
     * @return
     * @throws SQLException
     */
    public static String getScheduledBillboard() throws SQLException {
        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");
        // Create database connection.
        Connection Conn = getConnection(props);

        Statement st = Conn.createStatement();
        String sql = "SELECT * FROM "+ dbname +".schedule WHERE TimeStart <= ? AND TimeEnd >= ? LIMIT 1";
        PreparedStatement statement = Conn.prepareStatement(sql);

        LocalTime currentTime = LocalTime.now();

        statement.setTime(1,Time.valueOf(currentTime));
        statement.setTime(2,Time.valueOf(currentTime));

        ResultSet rs = statement.executeQuery();
        String billboardTitle = null;
        while(rs.next()){
            billboardTitle = rs.getString("Title");
        }
        return billboardTitle;
    }

    /**
     * Gets all scheduled billboards from database.
     * @return
     * @throws SQLException
     */
    public static ArrayList<Schedule> getSchedule() throws SQLException {

        ArrayList<Schedule> ScheduleList = new ArrayList<Schedule>();

        Properties props = ReadProperties("./db.props");
        String dbname = props.getProperty("jdbc.dbname");
        // Create database connection.
        Connection Conn = getConnection(props);

        // Execute SQL statements.
        Statement st = Conn.createStatement();
        ResultSet set = st.executeQuery("SELECT * From "+ dbname +".schedule");

        while (set.next()){
            ScheduleList.add(new Schedule(set.getDate("Date"),set.getTime("TimeStart"),set.getTime("TimeEnd"),set.getString("Title")));
            //System.out.println(set.getTime("TimeStart"));
        }
        return ScheduleList;
    }


    public static void main(String[] args) throws Exception {

        // Opens a server socket to listen for network connections.
        Properties networkProps = ReadProperties("./network.props");
        int port = Integer.parseInt(networkProps.getProperty("network.port"));
        ServerSocket serverSock = new ServerSocket(port);

        System.out.println("[SERVER] Initialising...");
        System.out.println("[SERVER] Creating DB tables...");

        // Create tables in DB if they don't exist already.
        createUserTable();
        createBillboardTable();
        createScheduleTable();

        System.out.println("[SERVER] Server Initialised.");

        // Continuous for loop to listen for connections.
        for (;;) {
            System.out.println("[SERVER] Listening on port: "+ port +"...");

            Socket socket = serverSock.accept();

            System.out.println("[SERVER] Client Connected...");

            clientHandler client = new clientHandler(socket);

            System.out.println("[SERVER] Client passed to client handler...");

            client.start();
        }
    }
}
