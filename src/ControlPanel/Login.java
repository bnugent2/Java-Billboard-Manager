package ControlPanel;

import ClientFunctions.User;
import Server.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import static Server.Security.clientHash;
import static Server.databaseCommands.ReadProperties;

/**
 * Class creates an instance of a login GUI, the user may input their username and password. This will
 * send a login request to the server for authentication. If authenticated a new instance of control panel will start.
 *
 * @author bnuge
 * @version 1.3
 */

public class Login extends JFrame {

    public Login() {

        JTextField TF_username = new JTextField();
        JPasswordField PW_password = new JPasswordField();
        JLabel L_Username = new JLabel("Username:");
        JLabel L_password = new JLabel("Password:");
        JPanel P_Input = new JPanel();
        JButton B_Login = new JButton("Login");

        JLabel L_Filler = new JLabel();

        L_Username.setBorder(new EmptyBorder(0,15,0,0));
        L_password.setBorder(new EmptyBorder(0,15,0,0));

        setTitle("Control Panel Login");
        setSize(300, 140);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GridLayout P_InputLayout = new GridLayout(3,2);
        P_InputLayout.setHgap(50);
        P_InputLayout.setVgap(5);
        P_Input.setLayout(P_InputLayout);

        P_Input.add(L_Username);
        P_Input.add(TF_username);
        P_Input.add(L_password);
        P_Input.add(PW_password);
        P_Input.add(L_Filler);
        P_Input.add(B_Login);

        add(P_Input, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        B_Login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Properties networkProps = ReadProperties("./network.props");
                    String address = networkProps.getProperty("network.address");
                    int port = Integer.parseInt(networkProps.getProperty("network.port"));
                    Socket socket = new Socket(address,port);

                    String passText = new String(PW_password.getPassword());
                    String hash = clientHash(passText);

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject("loginRequest"+TF_username.getText());
                    oos.flush();

                    oos.writeObject(hash);
                    oos.flush();

                    ObjectInputStream response = new ObjectInputStream(socket.getInputStream());
                    Session SessionUser = (Session)response.readObject();
                    response.close();
                    oos.close();

                    if(SessionUser != null) {

                        setVisible(false);
                        new Home(SessionUser).setVisible(true);
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    public static void main(String [] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        new Login().setVisible(true);
    }
}
