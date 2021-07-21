package ControlPanel;

import ClientFunctions.User;
import Server.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownServiceException;

/**
 * Home screen GUI for the billboard control panel from this screen the user may perform all actions of the
 * control panel such as user and billboard management. Requires login
 *
 * @author bnuge
 * @version 1.5
 */
public class Home extends JFrame {

    public Home(Session CurrentUser) {
        JDesktopPane DP_Home = new JDesktopPane();
        JPanel P_homeButtons = new JPanel();
        JButton B_List = new JButton();
        JButton B_Create = new JButton();
        JButton B_Users = new JButton();
        JButton B_Schedule = new JButton();
        JButton B_logout = new JButton("Logout");
        String Username = CurrentUser.getUsername();

        JLabel L_Welcome = new JLabel("WELCOME TO BILLBOARD CONTROL PANEL " + Username.toUpperCase());

        L_Welcome.setFont(new Font("Tahoma", Font.BOLD, 14));
        L_Welcome.setForeground(Color.white);

        setTitle("Control Panel");
        setSize(800, 800);
        add(DP_Home);
        BoxLayout homeLayout = new BoxLayout(DP_Home, BoxLayout.Y_AXIS);
        DP_Home.setLayout(homeLayout);
        P_homeButtons.setOpaque(false);
        P_homeButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        P_homeButtons.add(B_Create);
        P_homeButtons.add(B_Schedule);
        P_homeButtons.add(B_List);
        P_homeButtons.add(B_Users);
        B_List.setIcon(new ImageIcon("./Assets/list.png"));
        B_Create.setIcon(new ImageIcon("./Assets/Create.png"));
        B_Schedule.setIcon(new ImageIcon("./Assets/Schedule.png"));
        B_Users.setIcon(new ImageIcon("./Assets/Users.png"));

        B_logout.setSize(100,30);
        B_logout.setAlignmentX(Component.CENTER_ALIGNMENT);

        DP_Home.add(B_logout);
        DP_Home.add(Box.createRigidArea(new Dimension(0, 50)));
        L_Welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        P_homeButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        DP_Home.add(L_Welcome);
        DP_Home.add(Box.createRigidArea(new Dimension(0, 50)));
        DP_Home.add(P_homeButtons);

        B_Users.addActionListener(e -> {
            try {
                new EditUsers(CurrentUser).setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        B_Create.addActionListener(e -> {
            if (CurrentUser.getPermission().equals("Administrator") || CurrentUser.getPermission().equals("Create Billboards")) {
                new CreateBillboard(null, 0).setVisible(true);
            }
            else{
                JOptionPane.showMessageDialog(null, "You do not have permission to perform this action.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });

        B_logout.addActionListener(e -> {
           System.exit(0);
        });
        B_Schedule.addActionListener(e -> {
            try {
                new SchedulerGUI().setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        B_List.addActionListener(e ->   {
            try {
                new ListBillboards(CurrentUser).setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



}

