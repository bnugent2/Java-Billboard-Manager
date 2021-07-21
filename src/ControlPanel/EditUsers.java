package ControlPanel;

import ClientFunctions.User;
import Server.Server;
import Server.Session;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import static ClientFunctions.User.requestUserlist;
import static Server.Server.getUsers;
import static Server.databaseCommands.ReadProperties;


public class EditUsers extends JFrame {

    private JTable T_List;
    private int UserID;
    private String SelectUser;
    //TODO: JavaDocs and code comment
    private void showUsers() throws Exception {
        ArrayList<User> list = requestUserlist();

        DefaultTableModel model = (DefaultTableModel)T_List.getModel();
        model.setRowCount(0);

        for(User users:list){
            //System.out.println(users.getUserName() + " " + users.getPermission());
            model.addRow(new Object[]{users.getUserID(),users.getUserName(),users.getPermission()});
        }
        T_List.setModel(model);
        model.fireTableDataChanged();
    }


    public EditUsers(Session CurrentSession) throws Exception {
        JButton B_New = new JButton("New User");
        JButton B_Edit = new JButton("Edit");
        JButton B_Delete = new JButton("Delete");

        T_List = new JTable();
        DefaultTableModel model = (DefaultTableModel)T_List.getModel();
        model.addColumn("UserID");
        model.addColumn("Username");
        model.addColumn("Permissions");

        showUsers();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800,800);

        JPanel P_Options = new JPanel();
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        P_Options.setBorder(raisedbevel);

        JPanel P_Create = new JPanel();
        P_Create.setBorder(new TitledBorder(new EtchedBorder(), "Create "));
        BoxLayout Create_Layout = new BoxLayout(P_Create, BoxLayout.Y_AXIS);
        P_Create.setLayout(Create_Layout);
        P_Create.add(B_New);

        JPanel P_Edit = new JPanel();
        P_Edit.setBorder(new TitledBorder(new EtchedBorder(), "Edit "));
        BoxLayout Edit_Layout = new BoxLayout(P_Edit, BoxLayout.Y_AXIS);
        P_Edit.setLayout(Edit_Layout);
        P_Edit.add(B_Edit);
        P_Edit.add(B_Delete);

        P_Options.add(Box.createRigidArea(new Dimension(0, 25)));
        BoxLayout Options_Layout = new BoxLayout(P_Options, BoxLayout.Y_AXIS);
        P_Options.setLayout(Options_Layout);
        P_Options.add(P_Create);
        P_Options.add(Box.createRigidArea(new Dimension(0, 25)));
        P_Options.add(P_Edit);

        JScrollPane TABLE = new JScrollPane(T_List);
        add(P_Options,BorderLayout.WEST);
        add(TABLE,BorderLayout.CENTER);

        String [] permissions = { "Create Billboards", "Edit All Billboards", "Schedule Billboards","Edit Users", "Administrator"};
        JComboBox C_Permissions = new JComboBox(permissions);
        JTextField TF_username = new JTextField();
        JPasswordField PW_password = new JPasswordField();
        JComponent[] inputs = new JComponent[] {
                new JLabel("Username"),
                TF_username,
                new JLabel("Password"),
                PW_password,
                new JLabel("Permission"),
                C_Permissions,
        };


        JComboBox C_EditPermissions = new JComboBox(permissions);
        JTextField TF_EditUsername = new JTextField();
        JPasswordField PW_EditPassword = new JPasswordField();
        JComponent[] EditInputs = new JComponent[] {
                new JLabel("Username"),
                TF_EditUsername,
                new JLabel("Password"),
                PW_EditPassword,
                new JLabel("Permission"),
                C_EditPermissions,
        };

        T_List.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {

                if(T_List.getSelectedRow() != -1){
                    UserID = (int)T_List.getValueAt(T_List.getSelectedRow(),0);
                    SelectUser = (String)T_List.getValueAt(T_List.getSelectedRow(),1);
                    TF_EditUsername.setText(SelectUser);
                    C_EditPermissions.setSelectedItem((String)T_List.getValueAt(T_List.getSelectedRow(),2));

                }

            }
        });

        B_Delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (CurrentSession.getPermission().equals("Administrator") || CurrentSession.getPermission().equals("Edit Users")) {
                    int result = JOptionPane.showConfirmDialog(EditUsers.this,
                            "Please confirm you wish to delete user: '" + SelectUser + "'.\n"
                                    + "There is no way to undo this action.\n",
                            "Confirm Delete User",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (result == JOptionPane.YES_OPTION) {

                        try {
                            Properties networkProps = ReadProperties("./network.props");
                            String address = networkProps.getProperty("network.address");
                            int port = Integer.parseInt(networkProps.getProperty("network.port"));
                            Socket socket = new Socket(address, port);

                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeObject("DeleteUserRequest");
                            outputStream.flush();

                            outputStream.writeObject(UserID);
                            outputStream.flush();

                            outputStream.close();
                            socket.close();


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            showUsers();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (result == JOptionPane.NO_OPTION) {
                        System.out.println("No");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You don't have permission to perform this action", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        B_New.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (CurrentSession.getPermission().equals("Administrator") || CurrentSession.getPermission().equals("Edit Users")) {
                    int result = JOptionPane.showConfirmDialog(
                            EditUsers.this,
                            EditInputs,
                            "Create New User",
                            JOptionPane.PLAIN_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        // Adds User to database.
                        try {
                            //TODO: JavaDocs and code comment
                            User newUser = new User(TF_EditUsername.getText(), new String(PW_EditPassword.getPassword()), C_EditPermissions.getSelectedItem().toString());
                            newUser.sendUser();
                            showUsers();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You don't have permission to perform this action", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        B_Edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (CurrentSession.getPermission().equals("Administrator") || CurrentSession.getPermission().equals("Edit Users")) {
                    int result = JOptionPane.showConfirmDialog(
                            EditUsers.this,
                            EditInputs,
                            "Edit Existing User",
                            JOptionPane.PLAIN_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            Properties networkProps = ReadProperties("./network.props");
                            String address = networkProps.getProperty("network.address");
                            int port = Integer.parseInt(networkProps.getProperty("network.port"));
                            Socket socket = new Socket(address, port);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                            outputStream.writeObject("EditUserRequest");
                            outputStream.flush();

                            User user = new User(TF_EditUsername.getText(), new String(PW_EditPassword.getPassword()), C_EditPermissions.getSelectedItem().toString(), UserID);
                            outputStream.writeObject(user);
                            outputStream.close();
                            showUsers();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                } else {
                    JOptionPane.showMessageDialog(null, "You don't have permission to perform this action", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


    }
}
