package client;

import database.Database;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Timestamp;
import java.util.ArrayList;

import java.sql.Date;
import java.util.Collections;


/**
 * Created by oysteinhauan on 24/02/15.
 */
public class Appointment {


    Timestamp start, end;

    int size;
    ArrayList<User> attendingPeople;
    String subject;
    String description;
    Room room;
    int roomId;
    int appointmentId;



    public Appointment(Timestamp start, Timestamp end,
                       String subject, String description, int size) {

        this.start = start;
        this.end = end;
        this.subject = subject;
        this.description = description;
        this.size = size;



    }

    public static Appointment createAppointment(Timestamp start, Timestamp end, String subject, String description, int size) {


        Database db = new Database();
        db.connectDb("all_s_gruppe40", "qwerty");

        try {
            Appointment appointment = new Appointment(start, end, subject, description, size);
            appointment.findRoom();
            appointment.createAppointmentInDB(appointment, db);
            return appointment;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            db.closeConnection();
        }
        return null;
    }


    @Override
    public String toString() {

        return "Appointment{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                //", room=" + room +
                ", roomId=" + roomId +
                ", appointmentId=" + appointmentId +
                '}';
    }

    public void createAppointmentInDB(Appointment appointment, Database db) {


        //tar en appointment og legger til i databasen
        String sql = "insert into appointment (start, end, subject, description, roomId) values( '"+ String.valueOf(appointment.getStart()) + "', '" +
                String.valueOf(appointment.getEnd()) + "', '" + appointment.getSubject() + "', '"
                + appointment.getDescription() + "', "
                + (appointment.getRoomId() + "") + ");";

        System.out.println(sql);
        db.updateQuery(sql);
        db.closeConnection();


    }

    public void findRoom() {

        /*
        søke gjennom alle rom og avtaler for å finne ledig rom til møtet
        antar all dataen i databasen er ferdig uthentet og generert som objekter
        returnerer en liste med alle ledige rom
        */

        try {
            Database db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select roomId, size from room where size >= " + this.size +
                    " and roomId not in (select roomId from appointment where start between '" +
                    start + "' and '" + end + "' or end between '" + start + "' and '" + end + "');";

            ResultSet rs = db.readQuery(sql);
            int actualroom = -1;
            int tempsize = 0;
            int index = 0;

            while (rs.next()) {
                if (index == 0){
                    tempsize = rs.getInt("size");
                    actualroom = rs.getInt("roomId");
                    index++;
                }
                if(rs.getInt("size") < tempsize){
                    actualroom = rs.getInt("roomId");
                }

            }
            this.roomId = actualroom;
            if(this.roomId == -1){
                throw new IllegalAccessError("what");
            }


            System.out.println(sql);
            db.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void setId(int id) {

        this.appointmentId = id;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }


    public ArrayList<User> getAttendingPeople() {
        return attendingPeople;
    }

    public void addAttendant(User attendant) {
        //Må være mer sjekk før man kan legge til folk
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

}
