package Server;

import ClientFunctions.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static Server.Server.createUser;

public class databaseCommands {

    /**
     * Method that reads the given properties file and returns
     * a Properties object.
     *
     * @param filepath to the target properties file.
     * @return A Property object.
     */
    public static Properties ReadProperties(String filepath) {
        // Creates InputStream object to read given file.
        try (InputStream data = new FileInputStream(filepath)) {
            // Creates new Property object and loads file contents into it.
            Properties db_prop = new Properties();
            db_prop.load(data);
            return db_prop;
        } catch (IOException e) {
            // Returns nothing if error occurs.
            System.out.println(e);
            return null;
        }
    }

    /**
     * Method to create connection to database based on the given properties.
     *
     * @return Connection object.
     * @throws SQLException
     */
    public static Connection getConnection(Properties dbProps) throws SQLException {

        // Empty Connection object.
        Connection myCon = null;

        // Creates connection or throws SQL error.
        try {
            String url = "jdbc:mariadb:" + dbProps.getProperty("jdbc.url") + "/" + dbProps.getProperty("jdbc.dbname");
            myCon = DriverManager.getConnection(url, dbProps.getProperty("jdbc.username"), dbProps.getProperty("jdbc.password"));
            return myCon;
        } catch (SQLException e) {
            System.err.println("Connection Failed: " + e);
            return null;
        }
    }




    /**
     * Method to create the user table in the database.
     */
    public static void createUserTable() {
        try {
            //Creates table if one does not already exist.
            Properties Props = ReadProperties("./db.props");
            Connection Con = getConnection(Props);
            Statement st = Con.createStatement();
            st.executeQuery("CREATE TABLE users (UserID int AUTO_INCREMENT PRIMARY KEY," +
                    "Username varchar(255), Password varchar(255), Permissions varchar(255), Salt Varbinary(50))");

            User admin = new User("admin","password","Administrator");
            createUser(admin);

        } catch (Exception e) {
            System.out.println("[SERVER] Users table already exists");
            //e.printStackTrace();
        }
    }

    /**
     * Method to create the billboards table in the database.
     */
    public static void createBillboardTable() {
        try {
            Properties Props = ReadProperties("./db.props");
            Connection Con = getConnection(Props);
            Statement st = Con.createStatement();

            st.executeQuery( "CREATE TABLE Billboards " +
                    "(BillboardID INT AUTO_INCREMENT PRIMARY KEY,Title VARCHAR(255)," +
                    " XML BLOB, " +
                    " Creator VARCHAR(255)) ");

        } catch(Exception e)
        {
            System.out.println("[SERVER] Billboards table already exists");

        }
    }

    /**
     * Method to create the schedule table in the database.
     */
    public static void createScheduleTable() {
        try {
            Properties Props = ReadProperties("./db.props");
            Connection Con = getConnection(Props);
            Statement st = Con.createStatement();

            st.executeQuery( "CREATE TABLE Schedule " +
                    "(Date DATE," +
                    " TimeStart TIME, " +
                    " TimeEnd TIME,"+
                    " Title VARCHAR(255)," +
                    " Duration INT)");

        } catch(Exception e)
        {
            System.out.println("[SERVER] Schedule table already exists");

        }
    }
}
