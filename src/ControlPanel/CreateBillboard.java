package ControlPanel;


import BillboardViewer.Billboard;
import BillboardViewer.BillboardParser;
import BillboardViewer.ViewerDisplay;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import static Server.databaseCommands.ReadProperties;

/**
 * Create Billboard is a GUI that is used to create and edit Billboard XML files. The user is able to select
 * various billboard parameters and preview them. The user may then export the billboard to the server.
 *
 * @author bnuge
 * @version 1.0
 */
public class CreateBillboard extends JFrame {

    Billboard createdBillboard = new Billboard();

    public CreateBillboard(Billboard billboard, int BillboardID){
        if(billboard != null){
            createdBillboard = billboard;
        }
        JButton B_UpdateBillboard = new JButton("Update Billboard");
        JButton B_AddMessage = new JButton("Add Message");
        JButton B_AddInfo = new JButton("Add Information");
        JButton B_AddImage = new JButton("Add Image");
        JButton B_Import = new JButton("Import Billboard");
        JButton B_Export = new JButton("Export Billboard");
        JButton B_Background = new JButton("Set Background Colour");
        JButton B_Info_Colour = new JButton("Set Text Colour");
        JButton B_Message_Colour = new JButton("Set Text Colour");
        JButton B_Preview = new JButton("Preview Billboard");
        JButton B_New = new JButton("New Billboard");
        JButton B_deleteMessage = new JButton("Delete Message");
        JButton B_deleteInformation = new JButton("Delete Information");
        JButton B_deleteImage = new JButton("Delete Image");
        JButton B_deleteBackground = new JButton("Delete Background");
        JFileChooser F_import = new JFileChooser("C:");


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Billboard Editor");
        setLayout(new BorderLayout());
        setSize(1000,1000);

        JPanel P_Options = new JPanel();
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        P_Options.setBorder(raisedbevel);
        BoxLayout Options_Layout = new BoxLayout(P_Options, BoxLayout.Y_AXIS);
        P_Options.setLayout(Options_Layout);

        JPanel P_Information = new JPanel();
        P_Information.setBorder(new TitledBorder(new EtchedBorder(), "Information"));
        BoxLayout Info_Layout = new BoxLayout(P_Information, BoxLayout.Y_AXIS);
        P_Information.setLayout(Info_Layout);

        JPanel P_Message = new JPanel();
        P_Message.setBorder(new TitledBorder(new EtchedBorder(), "Message"));
        BoxLayout Message_Layout = new BoxLayout(P_Message, BoxLayout.Y_AXIS);
        P_Message.setLayout(Message_Layout);

        JPanel P_Export = new JPanel();
        P_Export.setBorder(new TitledBorder(new EtchedBorder(), "File"));
        BoxLayout Export_Layout = new BoxLayout(P_Export, BoxLayout.Y_AXIS);
        P_Export.setLayout(Export_Layout);

        JPanel P_Background = new JPanel();
        P_Background.setBorder(new TitledBorder(new EtchedBorder(), "Background"));
        BoxLayout Background_Layout = new BoxLayout(P_Background, BoxLayout.Y_AXIS);
        P_Background.setLayout(Background_Layout);

        JPanel P_Image = new JPanel();
        P_Image.setBorder(new TitledBorder(new EtchedBorder(), "Image"));
        BoxLayout Image_Layout = new BoxLayout(P_Image, BoxLayout.Y_AXIS);
        P_Image.setLayout(Image_Layout);

        JPanel P_Preview = new JPanel();
        P_Preview.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
        BoxLayout Preview_Layout = new BoxLayout(P_Preview, BoxLayout.Y_AXIS);
        P_Preview.setLayout(Preview_Layout);

        //Configure File
        if(billboard != null){
            P_Export.add(B_UpdateBillboard);
        }
        else{
            P_Export.add(B_New);
            P_Export.add(B_Import);
            P_Export.add(B_Export);
        }


        //Configure Background
        P_Background.add(B_Background);
        P_Background.add(B_deleteBackground);

        //Configure Image
        P_Image.add(B_AddImage);
        P_Image.add(B_deleteImage);

        //Configure Message
        P_Message.add(B_AddMessage);
        P_Message.add(B_Message_Colour);
        P_Message.add(B_deleteMessage);

        //Configure Info
        P_Information.add(B_AddInfo);
        P_Information.add(B_Info_Colour);
        P_Information.add(B_deleteInformation);

        P_Preview.add(B_Preview);

        //Configure Options Pane
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Export);
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Background);
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Image);
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Information);
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Message);
        P_Options.add(Box.createRigidArea(new Dimension(0, 10)));
        P_Options.add(P_Preview);
        P_Options.add(Box.createVerticalGlue());

        //Configure Main
        this.add(P_Options,BorderLayout.WEST);

        B_AddMessage.addActionListener(e -> {
            String result = (String) JOptionPane.showInputDialog(
                    this,
                    null,
                    "Add Text",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );
            if (result != null && result.length() > 0) {
                createdBillboard.setMessage(result);
            }
        });

        B_AddInfo.addActionListener(e -> {
            String result = (String) JOptionPane.showInputDialog(
                    this,
                    null,
                    "Add Text",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );
            if (result != null && result.length() > 0) {
                createdBillboard.setInformation(result);
            }
        });

        B_AddImage.addActionListener(ae -> {
            String result = (String) JOptionPane.showInputDialog(
                    this,
                    null,
                    "Add Text",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );
            if (result != null && result.length() > 0) {
                createdBillboard.setPicture(result);
            }
        });

        B_Background.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                    this,
                    "Choose Background Color",
                    null);
            if (newColor != null) {
                createdBillboard.setBackground(newColor);
            }
        });

        B_Info_Colour.addActionListener(e -> {
            Color TextColour = JColorChooser.showDialog(
                    this,
                    "Choose Text Color",
                    null);
            if (TextColour != null) {
                createdBillboard.setInformationColour(TextColour);
            }
        });

        B_Message_Colour.addActionListener(e -> {
            Color TextColour = JColorChooser.showDialog(
                    this,
                    "Choose Text Color",
                    null);
            if (TextColour != null) {
                createdBillboard.setMessageColour(TextColour);
            }
        });

        B_Preview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame preview = null;
                try {
                    preview = new ViewerDisplay(createdBillboard);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                preview.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });

        B_Import.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                F_import.showOpenDialog(null);
                if (F_import.getSelectedFile() != null) {
                    try {
                        createdBillboard = new BillboardParser().parse(F_import.getSelectedFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (SAXException saxException) {
                        saxException.printStackTrace();
                    } catch (ParserConfigurationException parserConfigurationException) {
                        parserConfigurationException.printStackTrace();
                    }
                }
            }
        });

        B_UpdateBillboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Properties networkProps = ReadProperties("./network.props");
                int port = Integer.parseInt(networkProps.getProperty("network.port"));
                String address = networkProps.getProperty("network.address");
                try {
                    Socket socket = new Socket(address,port);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    oos.writeObject("UpdateBillboardRequest");
                    oos.flush();

                    oos.writeObject(BillboardID);
                    oos.flush();

                    oos.writeObject(createdBillboard);
                    oos.flush();

                    oos.close();
                    socket.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        B_Export.addActionListener(e -> {
            String billboardName = (String) JOptionPane.showInputDialog(
                    this,
                    null,
                    "Export",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null
            );
            if (billboardName != null && billboardName.length() > 0) {

                Properties networkProps = ReadProperties("./network.props");
                String address = networkProps.getProperty("network.address");
                int port = Integer.parseInt(networkProps.getProperty("network.port"));

                try {
                        Socket socket = new Socket(address,port);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                        oos.writeObject("CreateBillboardRequest");
                        oos.flush();

                        oos.writeObject(billboardName);
                        oos.flush();

                        oos.writeObject(createdBillboard);
                        oos.flush();

                        oos.close();
                        socket.close();

                } catch (IOException ex) {
                     ex.printStackTrace();
        }
    }
});

        B_New.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdBillboard = new Billboard();
            }
            });

        B_deleteMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdBillboard.setMessage(null);
            }
        });

        B_deleteInformation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdBillboard.setInformation(null);
            }
        });

        B_deleteImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdBillboard.setPicture(null);
            }
        });

        B_deleteBackground.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createdBillboard.setBackground(null);
            }
        });

    }

}
