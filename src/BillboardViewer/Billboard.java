package BillboardViewer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import static Server.databaseCommands.ReadProperties;

public class Billboard implements Serializable {
        private Color Background;
        private String Message;
        private String Information;
        private String Picture;
        private Color messageColour;
        private Color informationColour;

        /**
         * Sets the Background Colour to be displayed on the billboard.
         * @param Colour Background Colour
         */
        public void setBackground(Color Colour){ Background = Colour; }

        /**
         * Sets the Message to be displayed on the billboard.
         * @param Text Message text to be set.
         */
        public void setMessage(String Text){ Message = Text; }

        /**
         * Sets the Message Colour to be displayed on the billboard.
         * @param Colour Message Colour.
         */
        public void setMessageColour(Color Colour){ messageColour = Colour; }

        /**
         * Sets the Information text to be displayed on the billboard.
         * @param Text Information text to be set.
         */
        public void setInformation(String Text){ Information = Text; }

        /**
         * Sets the Information colour to be displayed on the billboard.
         * @param Colour Information Colour to be set.
         */
        public void setInformationColour(Color Colour){ informationColour = Colour; }

        /**
         * Sets the Picture to be displayed on the billboard.
         * @param Image Picture to be set.
         */
        public void setPicture(String Image){ Picture = Image; }

        /**
         * Gets the Background Colour that is displayed on the billboard.
         * @return Background colour
         */
        public Color getBackground(){ return Background; }

        /**
         * Gets the Message text that is displayed on the billboard.
         * @return Message
         */
        public String getMessage(){ return Message;}

        /**
         * Gets the Message Colour that is displayed on the billboard.
         * @return Message Colour
         */
        public Color getMessageColour(){ return messageColour; }

        /**
         * Gets the Information text that is displayed on the billboard.
         * @return Information text
         */
        public String getInformation(){ return Information; }

        /**
         * Gets the Information Colour that is displayed on the billboard.
         * @return Information Colour
         */
        public Color getInformationColour(){ return informationColour;}

        /**
         * Gets the Picture that is displayed on the billboard.
         * @return Image
         */
        public String getPicture(){return Picture;}

        /**
         * Converts Color object to hex string for XML Files.
         * @param Background RGB Color value.
         * @return hex string.
         */
        public String ColourtoHex(Color Background){
                String hex = "#"+Integer.toHexString(Background.getRGB()).substring(2);
                return hex;
        }


        public static ArrayList<String[]> RequestBillboards() throws IOException, ClassNotFoundException {
                Properties networkProps = ReadProperties("./network.props");
                String address = networkProps.getProperty("network.address");
                int port = Integer.parseInt(networkProps.getProperty("network.port"));
                Socket socket = new Socket(address,port);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                outputStream.writeObject("GetBillboardList");
                outputStream.flush();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                ArrayList<String[]> BillboardList = (ArrayList<String[]>) inputStream.readObject();

                outputStream.close();
                inputStream.close();

                return BillboardList;
        }

        /**
         * Exports billboard attached billboard object to an XML File in the Root directory.
         * @param filename Billboard name to be saved into database
         */
        public void ExportBillboard(String filename) {
                File Test = new File(filename);

                try {
                        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                        Document document = documentBuilder.newDocument();

                        // Root XML element
                        Element root = document.createElement("billboard");
                        document.appendChild(root);

                        // Checks if values are not null
                        if(this.getBackground() != null) {
                                root.setAttribute("background", ColourtoHex(this.getBackground()));
                        }

                        if(this.getMessage() != null) {
                                Element message = document.createElement("message");
                                if(this.getMessageColour() != null) {
                                        message.setAttribute("colour", ColourtoHex(this.getMessageColour()));
                                }
                                message.appendChild(document.createTextNode(this.getMessage()));
                                root.appendChild(message);
                        }

                        if(this.getInformation() != null) {
                                Element information = document.createElement("information");
                                if(this.getInformationColour() != null) {
                                        information.setAttribute("colour", ColourtoHex(this.getInformationColour()));
                                }
                                information.appendChild(document.createTextNode(this.getInformation()));
                                root.appendChild(information);
                        }

                        if(this.getPicture() != null) {
                                Element picture = document.createElement("picture");
                                picture.setAttribute("url", this.getPicture());
                                root.appendChild(picture);
                        }


                        // create the xml file
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource domSource = new DOMSource(document);
                        StreamResult streamResult = new StreamResult(Test);

                        transformer.transform(domSource, streamResult);

                        System.out.println("Done creating XML File");

                } catch (ParserConfigurationException pce) {
                        pce.printStackTrace();
                } catch (TransformerException tfe) {
                        tfe.printStackTrace();
                }
        }
}


