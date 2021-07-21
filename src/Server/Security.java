package Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Security Class implements hashing and salting methods for user authentication processes on both the
 * server and client side using SHA-512 algorithm.
 * @author bnuge
 * @version 1.0
 */

public class Security {
    /**
     *Generates a random byte of values used for password encryption on server side.
     *
     * @return salt - Salt used for password encryption
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] Salt = new byte[16];
        random.nextBytes(Salt);
        return Salt;
    }


    /**
     * Class handles client side one way hashing, this is then sent to the server.
     *
     * @param password - plain text password from user input
     * @return hashedPassword - password after it has has been hashed via client
     */
    public static String clientHash(String password){
        String hashedPassword = null;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            byte[] bytes = hasher.digest(password.getBytes());
            StringBuilder bytetoString = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                bytetoString.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = bytetoString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }


    /**
     * Class handles server side final hashing and salting this is then saved into
     * the database, authentication ensures the user input is the same to this result
     *
     * @param hashedPassword - client side hashed password
     * @param salt - salt from database used in initial user creation
     * @return encryptedPassword - password once it has been hashed and salted
     */
    public static String ServerEncryption(String hashedPassword, byte[] salt) {

        String encryptedPassword = null;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            hasher.update(salt);
            byte[] bytes = hasher.digest(hashedPassword.getBytes());
            StringBuilder bytetoString = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                bytetoString.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptedPassword = bytetoString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedPassword;
    }
}
