package ControlPanel;

import BillboardViewer.Billboard;
import BillboardViewer.BillboardParser;
import BillboardViewer.ViewerDisplay;
import ClientFunctions.User;
import Server.Session;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import static BillboardViewer.Billboard.RequestBillboards;
import static BillboardViewer.ViewerDisplay.LoadBillboardRequest;
import static ClientFunctions.User.requestUserlist;
import static Server.databaseCommands.ReadProperties;

public class ListBillboards extends JFrame{

    private String SelectedTitle;
    private int BillboardID;
    private JTable T_List;

    /**
     * Loads scheduled billboards into T_List
     * @throws Exception
     */
    private void showBillboards() throws Exception {

        // Gets T_List values and clears them.
        DefaultTableModel model = (DefaultTableModel)T_List.getModel();
        model.setRowCount(0);

        // Loads billboards into list
        ArrayList<String[]> Titles = RequestBillboards();

        // Adds them back into table.
        for(String[] title:Titles){
            String[] str = title;
            model.addRow(str);
        }
        //Updates table with current Billboards
        T_List.setModel(model);
        model.fireTableDataChanged();
    }


    /**
     * Creates GUI to show billboards and allows user to edit, delete and preview billboards
     * @throws Exception
     */

    public  ListBillboards(Session CurrentSession) throws Exception {

        JButton B_Edit = new JButton("Edit");
        JButton B_Delete = new JButton("Delete");
        JButton B_Preview = new JButton("Preview");

        T_List = new JTable();
        DefaultTableModel model = (DefaultTableModel)T_List.getModel();

        model.addColumn("BillboardID");
        model.addColumn("Title");

        showBillboards();

        setSize(800,800);
        setLayout(new BorderLayout());

        JPanel P_Options = new JPanel();
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        P_Options.setBorder(raisedbevel);

        JPanel P_Actions = new JPanel();
        P_Actions.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        BoxLayout Actions_Layout = new BoxLayout(P_Actions, BoxLayout.Y_AXIS);
        P_Actions.setLayout(Actions_Layout);

        BoxLayout Options_Layout = new BoxLayout(P_Options, BoxLayout.Y_AXIS);
        P_Options.setLayout(Options_Layout);

        P_Actions.add(B_Preview);
        P_Actions.add(B_Edit);
        P_Actions.add(B_Delete);
        P_Options.add(P_Actions);

        add(P_Options,BorderLayout.WEST);
        add(new JScrollPane(T_List),BorderLayout.CENTER);

        // Sends delete request to Server along with ID of Billboard to be deleted.
        B_Delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(ListBillboards.this,
                        "Please confirm you wish to delete this Billboard.\n"
                                + "There is no way to undo this action.\n",
                        "Confirm Delete Billboard",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    if (CurrentSession.getPermission().equals("Administrator") || CurrentSession.getPermission().equals("Edit All Billboards")) {
                        Properties networkProps = ReadProperties("./network.props");
                        String address = networkProps.getProperty("network.address");
                        int port = Integer.parseInt(networkProps.getProperty("network.port"));
                        BillboardID = Integer.parseInt((String) T_List.getValueAt(T_List.getSelectedRow(), 0));

                        try {
                            Socket socket = new Socket(address, port);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeObject("DeleteBillboardRequest");
                            outputStream.flush();

                            outputStream.writeObject(BillboardID);
                            outputStream.flush();
                            outputStream.close();
                            socket.close();

                            showBillboards();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "You do not have permission to perform this action.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Launches preview of selected billboard.
        B_Preview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectedTitle = (String) T_List.getValueAt(T_List.getSelectedRow(),1);
                System.out.println(SelectedTitle);
                Billboard Test = null;
                try {
                    LoadBillboardRequest(SelectedTitle);
                    Test = BillboardParser.parse(new File("temp.xml"));
                    new ViewerDisplay(Test);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // Launches billboard editor to Update selected billboard.
        B_Edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    SelectedTitle = (String) T_List.getValueAt(T_List.getSelectedRow(),1);
                    BillboardID = Integer.parseInt((String) T_List.getValueAt(T_List.getSelectedRow(),0));
                    LoadBillboardRequest(SelectedTitle);
                    Billboard billboard = BillboardParser.parse(new File("temp.xml"));
                    new CreateBillboard(billboard,BillboardID).setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

}
