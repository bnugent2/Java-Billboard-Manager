package BillboardViewer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.awt.*;
import java.io.*;

import static javax.swing.text.html.CSS.getAttribute;

public class BillboardParser{

    /**
     * This Method takes an xml file and parses the contents into a Billboard object.
     * @param billboard An xml file that contains all of the data needed to dispaly a billboard.
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Billboard parse(File billboard) throws IOException, SAXException, ParserConfigurationException {


        Billboard temp = new Billboard();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(billboard);
        Element root = document.getDocumentElement();

        // Gets the main Node which of the file which will be called "billboard" to be used as an iterator.
        NodeList nList = document.getElementsByTagName("billboard");
        Node node = nList.item(0);

        // This for loop searches through element node and parses the information to to the corresponding sets.
        if (node.getNodeType() == Node.ELEMENT_NODE) {

            // Parses billboard background colour from file
            Element billboardElement = (Element) node;
            String hex = billboardElement.getAttribute("background");
            //Set Text color if attribute is present
            if (hex != "") {
                Color bg = Color.decode(hex);
                temp.setBackground(bg);
            }

            // Parses Message node from the xml file. Try-catch is used in-case Message node does not exist.
            try {
                NodeList message = billboardElement.getElementsByTagName("message").item(0).getChildNodes();
                temp.setMessage(message.item(0).getTextContent());
                String hex2 = ((Element) message).getAttribute("colour");
                //Set Text color if attribute is present
                if (hex2 != "") {
                    Color msg = Color.decode(hex2);
                    temp.setMessageColour(msg);
                }
            }
            catch(NullPointerException e){ }

            // // Parses Information node from the xml file. Try-catch is used in-case Message node does not exist.
            try{
                NodeList information = billboardElement.getElementsByTagName("information").item(0).getChildNodes();
                temp.setInformation(information.item(0).getTextContent());

                String hex3 = ((Element) information).getAttribute("colour");
                //Set Text color if attribute is present
                if (hex3 != "") {
                    Color info = Color.decode(hex3);
                    temp.setInformationColour(info);
                }
            }
            catch(NullPointerException e){ }

            // Parses Picture node from the xml file. Try-catch is used in-case Message node does not exist.
            try{
                NodeList picture = billboardElement.getElementsByTagName("picture").item(0).getChildNodes();

                // As picture can either be a url or data type both need to be checked and parsed.
                    String image = ((Element) picture).getAttribute("url");

                    if(image.isEmpty()){
                        image = ((Element) picture).getAttribute("data");
                        temp.setPicture(image);
                    }
                    else {
                        temp.setPicture(image);
                    }
            }
            catch(NullPointerException e) { }
        }

        // Returns temporary data value which represents the data parsed from the node.
        return temp;
    }

    public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException {
        Billboard Test = parse(new File("./xmlDocs/6.xml"));
        System.out.println(Test.getBackground());
        System.out.println(Test.getMessage());
        System.out.println(Test.getMessageColour());
        System.out.println(Test.getInformation());
        System.out.println(Test.getInformationColour());
        System.out.println(Test.getPicture());

    }
}

