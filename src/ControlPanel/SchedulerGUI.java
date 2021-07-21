package ControlPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import static BillboardViewer.Billboard.RequestBillboards;
import static Server.databaseCommands.ReadProperties;


public class SchedulerGUI extends JFrame {

    JPanel CalendarPanel;

    /**
     * Method that will load calendar to display in GUI
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadCalendar() throws IOException, ClassNotFoundException {

        ArrayList<JPanel> DOW = new ArrayList<>();
        Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEE dd-MMM");
        SimpleDateFormat compareFormat = new SimpleDateFormat("yyyy-MM-dd");

        Properties networkProps = ReadProperties("./network.props");
        String address = networkProps.getProperty("network.address");
        int port = Integer.parseInt(networkProps.getProperty("network.port"));
        Socket socket = new Socket(address,port);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject("GetScheduleRequest");
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        ArrayList<Schedule> ScheduleList = (ArrayList<Schedule>) ois.readObject();

        oos.close();
        socket.close();


        for(int i = 0; i <= 7; i++){
            JPanel Day = new JPanel();
            Day.setLayout(new BoxLayout(Day, BoxLayout.Y_AXIS));
            Day.setSize(100, 100);
            Day.setBorder(new TitledBorder(new EtchedBorder(), sdf.format(calendar.getTime())));
           for (Schedule schedules : ScheduleList) {

                if (compareFormat.format(schedules.getDate()).equals(compareFormat.format(calendar.getTime()))) {

                    JLabel Lab = new JLabel("Tilte: '"+ schedules.getBillboardTitle() + "' Start Time: " + schedules.getStartTime());
                    Day.add(Lab);
                }
           }

            DOW.add(Day);
            calendar.add(java.util.Calendar.DAY_OF_WEEK,1);
        }


        GridBagLayout layout = new GridBagLayout();
        CalendarPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 150;
        gbc.ipady = 300;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for(int i =0;i < 7;i++){
            CalendarPanel.add(DOW.get(i), gbc);
            gbc.gridx += 1;
            gbc.gridy = 0;
        }
    }

    public SchedulerGUI() throws IOException, ClassNotFoundException {

        setSize(1500, 1500);

        setLayout(new BorderLayout());
        JButton B_Schedule = new JButton("Schedule Billboard");

        CalendarPanel = new JPanel();

        loadCalendar();

        JPanel P_Options = new JPanel();
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        P_Options.setBorder(raisedbevel);
        P_Options.add(B_Schedule);


        add(P_Options, BorderLayout.WEST);
        add(CalendarPanel, BorderLayout.CENTER);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SimpleDateFormat compareFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar CAL = java.util.Calendar.getInstance();

        String datelab1 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab2 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab3 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab4 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab5 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab6 = compareFormat.format(CAL.getTime());
        CAL.add(java.util.Calendar.DAY_OF_WEEK, 1);
        String datelab7 = compareFormat.format(CAL.getTime());

        String[] dates = new String[]{
                datelab1,
                datelab2,
                datelab3,
                datelab4,
                datelab5,
                datelab6,
                datelab7
        };

        java.util.Calendar timeStart = java.util.Calendar.getInstance();
        timeStart.set(java.util.Calendar.HOUR_OF_DAY, 0);
        timeStart.set(java.util.Calendar.MINUTE, 0);

        java.util.Calendar timeEnd = java.util.Calendar.getInstance();
        timeEnd.set(java.util.Calendar.HOUR_OF_DAY, 23);
        timeEnd.set(java.util.Calendar.MINUTE, 59);

        SimpleDateFormat timefortmat = new SimpleDateFormat("HH:mm");

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        do {
            model.addElement(timefortmat.format(timeStart.getTime()));
            timeStart.add(java.util.Calendar.MINUTE, 5);
        } while (timeStart.getTime().before(timeEnd.getTime()));

        JComboBox timeCombo = new JComboBox(model);


        JComboBox dateCombo = new JComboBox(dates);


        JTextField durationInput = new JTextField();

        ArrayList<String> BillboardList = null;

        ArrayList<String[]> titles = RequestBillboards();

        DefaultComboBoxModel<String> titlestrings = new DefaultComboBoxModel<>();

        for(String[] str:titles){
            titlestrings.addElement(str[1]);
        }



        JComboBox Titles = new JComboBox(titlestrings);

        JComponent[] inputs = new JComponent[]{
                new JLabel("Select Billboard"),
                Titles,
                new JLabel("Select Date"),
                dateCombo,
                new JLabel("Select Start Time"),
                timeCombo,
                new JLabel("Duration(Mins)"),
                durationInput

        };

        // Sends Schedule request to server along with Schedule Object.
        B_Schedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(SchedulerGUI.this, inputs, "Schedule Billboard", JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {

                    Date DATE = Date.valueOf(dateCombo.getSelectedItem().toString());
                    int duration = Integer.parseInt(durationInput.getText());
                    LocalTime t = LocalTime.parse(timeCombo.getSelectedItem().toString());
                    Time time = Time.valueOf(t);
                    String title = Titles.getSelectedItem().toString();

                    Schedule schedule = new Schedule(DATE,time,duration,title);

                    try {
                        Properties networkProps = ReadProperties("./network.props");
                        String address = networkProps.getProperty("network.address");
                        int port = Integer.parseInt(networkProps.getProperty("network.port"));
                        Socket socket = new Socket(address,port);

                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject("CreateScheduleRequest");
                        outputStream.flush();

                        outputStream.writeObject(schedule);
                        outputStream.flush();

                        outputStream.close();
                        socket.close();

                        CalendarPanel.removeAll();
                        CalendarPanel.revalidate();

                        loadCalendar();
                        CalendarPanel.revalidate();
                        CalendarPanel.repaint();


                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }


                }

            }
        });
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
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
        new SchedulerGUI().setVisible(true);
    }

}
