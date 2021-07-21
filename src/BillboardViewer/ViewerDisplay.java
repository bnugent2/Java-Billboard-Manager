package BillboardViewer;

import org.xml.sax.SAXException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static Server.Server.getScheduledBillboard;
import static Server.databaseCommands.ReadProperties;
/**
 * ViewerDisplay contains methods to display a billboard to the screen in full screen.
 * This class will communicate directly with the Billboard Server to display a billboard
 * as per the schedule found in the database.
 */
public class ViewerDisplay extends JFrame {

    JFrame Frame = new JFrame();
    JLabel L_Information = new JLabel();
    JLabel L_Message = new JLabel();
    JLabel L_Picture = new JLabel();
    boolean Case1 = false;
    boolean Case2 = false;
    boolean Case3 = false;
    boolean Case4 = false;
    boolean Case5 = false;
    boolean Case6 = false;
    boolean Case7 = false;
    static String PrevBillboard = "fortniteisforkids";
    static String billboardTitle;
    static ViewerDisplay viewerDisplay;

    /**
     * Checks to see what 'Case' the given Billboard is. Each 'Case' has a different number of nodes present
     * and so different data needs to be displayed differently on the Billboard. Sets a boolean to true
     * to be used in ViewerDisplay method to correspond with the following cases:
     * Case 1 - Message Only.
     * Case 2 - Information Only.
     * Case 3 - Picture Only.
     * Case 4 - Message and Picture.
     * Case 5 - Information and Message.
     * Case 6 - Picture and Information.
     * Case 7 - Message, Information and Message
     * @param billboard A Billboard Object
     */
    public void testCase(Billboard billboard){

        if(billboard.getMessage() != null && billboard.getInformation() == null && billboard.getPicture() == null){
            Case1 = true;
        }
        if(billboard.getInformation() != null && billboard.getMessage() == null && billboard.getPicture() == null){
            Case2 = true;
        }
        if(billboard.getPicture() != null && billboard.getInformation() == null && billboard.getMessage() == null){
            Case3 = true;
        }
        if(billboard.getPicture() != null && billboard.getInformation() == null && billboard.getMessage() != null){
            Case4 = true;
        }
        if(billboard.getPicture() == null && billboard.getInformation() != null && billboard.getMessage() != null){
            Case5 = true;
        }
        if(billboard.getPicture() != null && billboard.getInformation() != null && billboard.getMessage() == null){
            Case6 = true;
        }
        if(billboard.getPicture() != null && billboard.getInformation() != null && billboard.getMessage() != null){
            Case7 = true;
        }
    }

    /**
     * A method that will display a 'message' tag from a given Billboard as text on the screen.
     * @param billboard A Billboard Object.
     * @param frameWidth Width of frame which Billboard is to be displayed in.
     * @param frameHeight Height of frame which Billboard is to be displayed in.
     */
    public void message(Billboard billboard,int frameWidth,int frameHeight){

        L_Message = new JLabel(billboard.getMessage());
        L_Message.setForeground(billboard.getMessageColour());

        setSize(frameWidth,frameHeight);
        L_Message.setBorder(new EmptyBorder(0,25,0,25));
        int width = getSize().width - 50;

        // Resize to maximise text on screen.
        Font msgFont = L_Message.getFont();
        float stringWidth2 = L_Message.getFontMetrics(msgFont).stringWidth(L_Message.getText());
        int fontsize = msgFont.getSize();
        float ratio = width/stringWidth2;
        float newmsgsize = (fontsize*ratio)*(float)0.96;
        L_Message.setFont(msgFont.deriveFont(newmsgsize));
    }

    /**
     * A method that will display a 'picture' tag from a given Billboard as an image to the screen.
     * @param billboard A Billboard Object.
     * @param scalar A scalar factor that determines how the picture will be sized.
     * @param frameWidth Width of frame which Billboard is to be displayed in.
     * @param frameHeight Height of frame which Billboard is to be displayed in.
     * @throws IOException
     */
    public void picture(Billboard billboard,int scalar,int frameWidth,int frameHeight) throws IOException {

        L_Picture.setText(billboard.getPicture());

        // If image to be displayed is a url attribute.
        if(L_Picture.getText().contains("http")) {

            URL url = new URL(billboard.getPicture());
            BufferedImage image = ImageIO.read(url);

            double picWidth = image.getWidth();
            double picHeight = image.getHeight();
            double finHeight = picHeight;
            double finWidth = picWidth;
            double ratio = picWidth/picHeight;

            // Correctly sizes picture within bounds maintaining width:height ratio.
            if(ratio > 1){
                finWidth =  (frameWidth/scalar);
                finHeight = (int) (finWidth / ratio);
            }
            if(ratio < 1){
                finHeight =  (frameHeight/scalar);
                finWidth = (int) (finHeight * ratio);
            }
            if(ratio == 1){
                finHeight = frameHeight/scalar;
                finWidth = finHeight;
            }

            L_Picture = new JLabel(new ImageIcon(image.getScaledInstance((int) finWidth, (int) finHeight, 0)));
        }

        else {

            // If image to be displayed is a data attribute.
            byte[] bytes = L_Picture.getText().getBytes();
            byte[] bis = Base64.getDecoder().decode(bytes);

            InputStream targetStream = new ByteArrayInputStream(bis);
            BufferedImage image = ImageIO.read(targetStream);

            double picWidth = image.getWidth();
            double picHeight = image.getHeight();
            double finHeight = picHeight;
            double finWidth = picWidth;
            double ratio = picHeight / picWidth;

            // Correctly sizes picture within bounds maintaining width:height ratio.
            if(ratio == 1){
                finHeight = frameHeight/scalar;
                finWidth = finHeight;
            }
            else{
                finHeight =  (frameHeight/scalar);
                finWidth = (int) (finHeight / ratio);
            }

            L_Picture = new JLabel(new ImageIcon(image.getScaledInstance((int) finWidth, (int) finHeight, 0)));
        }
    }

    /**
     * Method to display an information tag from a Billboard object as text to the screen.
     * @param billboard A Billboard object.
     * @param frameWidth Width of frame which Billboard is to be displayed in.
     * @param frameHeight Height of frame which Billboard is to be displayed in.
     * @param borderHeight Height of empty border to be used.
     */
    public void information(Billboard billboard,int frameWidth,int frameHeight,int borderHeight){

        //Uses html formatting to split information text into multiple lines.
        L_Information = new JLabel("<html><p overlap-wrap: break-word; style=text-align:center>"+billboard.getInformation()+"</p>");

        L_Information.setSize((int) (frameWidth*0.75),frameHeight/2);

        L_Information.setBorder(new EmptyBorder(borderHeight,frameWidth/8,borderHeight,frameWidth/8));

        int realLength = (L_Information.getText().length()-64);
        int newFontsize=12;

        // The following code calculates font size for different cases of information.
        if(Case2){
            newFontsize = (int) (543.48* Math.pow(realLength,-0.454));
        }
        if(Case5){
            newFontsize = (int) (400* Math.pow(realLength,-0.453));
        }
        if(Case6){
            newFontsize = (int) (334.5* Math.pow(realLength,-0.453));
        }
        if(Case7){
            newFontsize = (int) (312* Math.pow(realLength,-0.453));
        }

        L_Information.setFont(new Font(L_Information.getFont().getName(), Font.PLAIN, newFontsize));
        L_Information.setForeground(billboard.getInformationColour());

    }
     /**
     * Displays a given Billboard Object to the Screen. Each Case has its own loop depending on the output from the
     * billboardCase method.
     * @param billboard A Billboard Object.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws BadLocationException
     */
        public ViewerDisplay(Billboard billboard) throws IOException {

            // Checks to see what 'Case' the Billboard is. I.e what data needs to be displayed to the screen.
            testCase(billboard);
            Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            Frame.setUndecorated(true); //Uncomment to hide menubar
            Frame.setVisible(true);

            Dimension FrameSize = Frame.getSize();
            int frameWidth = (int) FrameSize.getWidth();
            int frameHeight = (int) FrameSize.getHeight();

            // Displays Message Only Case to the screen.
            if(Case1){

                message(billboard,frameWidth,frameHeight);

                Frame.add(L_Message);
            }

            // Displays Information Only Case to the screen.
            if(Case2) {

                information(billboard,frameWidth,frameHeight,frameHeight/4);

                Frame.add(L_Information,BorderLayout.CENTER);

            }

            // Displays Picture Only Case to the screen.
            if(Case3) {

               picture(billboard,2,frameWidth,frameHeight);
                Frame.add(L_Picture);
            }

            // Displays Message and Picture Case to the screen.
            if(Case4){

                picture(billboard,2,frameWidth,frameHeight);
                message(billboard,frameWidth,frameWidth);

                Frame.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();

                // Displays Picture in centre of screen in bottom 2/3 of screen with
                // Message in the remaining top section.

                c.gridx = 0;
                c.gridy = 0;
                c.weighty = 1;
                Frame.add(L_Message,c);

                c.gridx = 0;
                c.gridy = 1;
                c.weighty = 1;
                Frame.add(L_Picture,c);

            }

            // Displays Message and Information case to the screen.
            if(Case5){

                message(billboard,frameWidth,frameHeight);
                information(billboard,frameWidth,frameHeight,frameHeight/8);//4.5

                int messageSize = L_Message.getFont().getSize();
                int infoSize = L_Information.getFont().getSize();

                // If information font size is larger than message font size then the information font size will
                // be set smaller then then message font size.
                if(infoSize > messageSize){
                    infoSize = (messageSize-2);
                    L_Information.setFont(new Font(L_Information.getFont().getName(), Font.PLAIN, infoSize));
                }

                Frame.setLayout(new BorderLayout());
                Frame.add(L_Message,BorderLayout.CENTER);
                Frame.add(L_Information,BorderLayout.SOUTH);

            }

            // Displays Picture and Information case to the screen.
            if(Case6){

               picture(billboard,2,frameWidth,frameHeight);
               information(billboard,frameWidth,frameHeight,frameHeight/8); //3.9

                Frame.setLayout(new BorderLayout());

                // Displays Picture in centre of screen in bottom 2/3 of screen with
                // Message in the remaining top section.

                Frame.add(L_Picture,BorderLayout.CENTER);

                Frame.add(L_Information,BorderLayout.SOUTH);

            }

            // Displays Picture, Information and Message to the screen.
            if(Case7){

                picture(billboard,3,frameWidth,frameHeight);

                information(billboard,frameWidth,frameHeight,0); // 3

                message(billboard,frameWidth,frameHeight);


                int messageSize = L_Message.getFont().getSize();
                int infoSize = L_Information.getFont().getSize();

                // If information font size is larger than message font size then the information font size will
                // be set smaller then then message font size.
                if(infoSize > messageSize){
                    infoSize = (messageSize-2);
                    L_Information.setFont(new Font(L_Information.getFont().getName(), Font.PLAIN, infoSize));
                }

                Frame.setLayout(new GridBagLayout());
                GridBagConstraints g = new GridBagConstraints();

                // Displays Picture in centre of screen in bottom 2/3 of screen with
                // Message in the remaining top section.

                g.gridx = 0;
                g.gridy = 0;
                g.weighty = 1;
                Frame.add(L_Message,g);

                g.gridx = 0;
                g.gridy = 1;
                g.weighty = 1;
                Frame.add(L_Picture,g);

                g.gridx = 0;
                g.gridy = 2;
                g.fill = GridBagConstraints.HORIZONTAL;
                Frame.add(L_Information,g);

            }

            Frame.getContentPane().setBackground(billboard.getBackground());
            Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Frame.pack();
            Frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Adds key listener to 'ESC' key to close program if mouse is clicked
            Frame.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {  // handler
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        Frame.dispose();
                    }
                }
            });

            // Adds mouse listener to close program if mouse is clicked.
            Frame.addMouseListener(new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {
                    Frame.dispose();
                }
                @Override public void mousePressed(MouseEvent e) { }
                @Override public void mouseReleased(MouseEvent e) { }
                @Override public void mouseEntered(MouseEvent e) { }
                @Override public void mouseExited(MouseEvent e) {}
            });
        }

    /**
     * Method to display a billboard placeholder to the screen.
     * @throws IOException
     */
    public static void billboardPlaceholder() throws IOException {

        JFrame placeFrame= new JFrame();
        JLabel placeLabel = new JLabel("Made you look!");
        JLabel placeInfo = new JLabel("You could own this billboard. Contact Group 174.");

        URL url = new URL("https://pics.me.me/thumb_2x-made-you-look-decal-below-the-waist-ok-sign-53054040.png");
        BufferedImage image = ImageIO.read(url);
        JLabel placePic = new JLabel(new ImageIcon(image.getScaledInstance((int) image.getWidth()+100, image.getHeight()+100, 0)));

        placeFrame.getContentPane().setBackground(Color.WHITE);
        placeLabel.setForeground(Color.BLACK);

        placeLabel.setFont(placeLabel.getFont().deriveFont(80f));
        placeLabel.setBorder(new EmptyBorder(0,50,0,50));

        placeInfo.setFont(placeLabel.getFont().deriveFont(50f));


        placeFrame.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();

        // Displays Picture in centre of screen in bottom 2/3 of screen with
        // Message in the remaining top section.

        g.gridx = 0;
        g.gridy = 0;
        g.weighty = 1;
        placeFrame.add(placeLabel,g);

        g.gridx = 0;
        g.gridy = 1;
        g.weighty = 1;
        placeFrame.add(placePic,g);

        g.gridx = 0;
        g.gridy = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        placeFrame.add(placeInfo,g);


        placeFrame.setVisible(true);
        placeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        placeFrame.pack();
        placeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Adds key listener to 'ESC' key to close program if mouse is clicked.
        placeFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {  // handler
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

        // Adds mouse listener to close program if mouse is clicked.
        placeFrame.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            @Override public void mousePressed(MouseEvent e) { }
            @Override public void mouseReleased(MouseEvent e) { }
            @Override public void mouseEntered(MouseEvent e) { }
            @Override public void mouseExited(MouseEvent e) {}
        });

    }

    /**
     * Method to display an error to the screen if billboard cannot connect to server.
     */
    public static void errorBillboard(){

            JFrame errorFrame= new JFrame();
            JLabel errorLabel = new JLabel("<html><p style='text-align:center'>Error Server Not Responding.<br>Attempting To Find A Connection...</p></html>");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);


            errorFrame.getContentPane().setBackground(Color.RED);
            errorLabel.setForeground(Color.BLACK);

            errorLabel.setFont(errorLabel.getFont().deriveFont(60f));
            errorLabel.setBorder(new EmptyBorder(0,50,0,50));

            errorFrame.add(errorLabel);
            errorFrame.setVisible(true);
            errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            errorFrame.pack();
            errorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Adds key listener to 'ESC' key to close program if mouse is clicked.
            errorFrame.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {  // handler
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.exit(0);
                    }
                }
            });

        // Adds mouse listener to close program if mouse is clicked.
            errorFrame.addMouseListener(new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {
                    System.exit(0);
                }
                @Override public void mousePressed(MouseEvent e) { }
                @Override public void mouseReleased(MouseEvent e) { }
                @Override public void mouseEntered(MouseEvent e) { }
                @Override public void mouseExited(MouseEvent e) {}
            });
        }

    public static void LoadBillboardRequest(String title) throws IOException {

        // Read the specified network port to connect to.
        Properties networkProps = ReadProperties("./network.props");
        int port = Integer.parseInt(networkProps.getProperty("network.port"));

        // Open socket connection to server.
        Socket socket = new Socket("localhost", port);

        // Create object output stream to communicate to server.
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        // Write a "BillboardRequest" command to the server.
        outputStream.writeObject("BillboardRequest");
        outputStream.flush();

        // Then write the title of the billboard that you wish to retrieve.
        outputStream.writeObject(title);
        outputStream.flush();

        // Create new file "temp.xml" file to write to.
        FileOutputStream fr = new FileOutputStream("temp.xml");

        // Byte array to read incoming data from the server.
        byte[] buffer = new byte[1000];

        // Open inputstream from the server.
        InputStream inputStream = socket.getInputStream();

        // Read byte data into "buffer".
        inputStream.read(buffer,0,buffer.length);

        // Load "buffer" into string to filter out null values.
        String unfilteredByte = new String(buffer,"UTF-8");
        String filteredByte = unfilteredByte.replaceAll("\\x00","");

        // Write "filteredByte" back into a byte array for writing.
        byte[] output =filteredByte.getBytes("UTF-8");

        // Write "output" to file.
        fr.write(output,0,output.length);

        // Close all streams.
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {

        Timer timer = new Timer();
       TimerTask getUpdates = new TimerTask() {
           public void run() {
               try{
                   billboardTitle = getScheduledBillboard();
                   if(billboardTitle != null && !billboardTitle.equals(PrevBillboard)){
                       viewerDisplay.Frame.dispose();
                       LoadBillboardRequest(billboardTitle);
                       Billboard Test = BillboardParser.parse(new File("temp.xml"));
                       PrevBillboard = billboardTitle;
                       viewerDisplay = new ViewerDisplay(Test);

                   } else {
                       if(billboardTitle == null && PrevBillboard != null ){
                           viewerDisplay.Frame.dispose();
                           Billboard Test = BillboardParser.parse(new File("placeholder.xml"));
                           PrevBillboard = billboardTitle;
                           viewerDisplay = new ViewerDisplay(Test);
                       }
                   }
               } catch (Exception e){
                   e.printStackTrace();
               }
           }
       };

        Billboard Test = BillboardParser.parse(new File("placeholder.xml"));
        viewerDisplay = new ViewerDisplay(Test);

        timer.scheduleAtFixedRate(getUpdates,0,15000);
    }
}


