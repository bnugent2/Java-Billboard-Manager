package ControlPanel;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalTime;
import java.util.Properties;

import static Server.databaseCommands.ReadProperties;
import static Server.databaseCommands.getConnection;

public class Schedule implements Serializable {
    private Date date;
    private Time timestart;
    private Time timeend;
    private int duration;
    private String billboardTitle;

    public Schedule(Date date, Time time, int duration,String billboardTitle){
        this.date = date;
        this.timestart = time;
        this.duration = duration;
        this.billboardTitle = billboardTitle;
    }

    public Schedule(Date date, Time timestart, Time timeend, String billboardTitle){
        this.date = date;
        this.timestart = timestart;
        this.timeend = timeend;
        this.billboardTitle = billboardTitle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return timestart;
    }

    public void setTime(Time time) {
        this.timestart = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getBillboardTitle(){return billboardTitle;};

    public void setBillboardTitle(String billboardTitle) {this.billboardTitle = billboardTitle;}



}
