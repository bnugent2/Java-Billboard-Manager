package Server;


import java.io.Serializable;
import java.util.UUID;

/**
 *
 * Class for Session management in Billboard Control Panel. Class used to create Session object containing
 * keys needed such as permissions and a Unique User ID.
 *
 * @author bnuge
 * @version 1.0
 */
public class Session implements Serializable {
    private String UUID;
    private String username;
    private String permission;

    /**
     *Sets the Unique User ID for this session.
     */
    public void setUUID(){ UUID = java.util.UUID.randomUUID().toString();}

    /**
     *Returns the Unique User ID for this session.
     * @return  Unique User ID for this session.
     */
    public String getUUID(){ return UUID;}

    /**
     *Sets the Username of user for this session.
     * @param Username Session User's Username.
     */
    public void setUsername(String Username){ username = Username;}

    /**
     *Returns the Username of user for this session.
     * @return Session User's Username.
     */
    public String getUsername(){ return username;}

    /**
     *Sets the Permission for user for this session.
     * @param Permission Session User's Permission.
     */
    public void setPermission(String Permission){ permission = Permission;}

    /**
     *Returns the Permission of user for this session.
     * @return Session User's Permission.
     */
    public String getPermission(){ return permission;}

}

