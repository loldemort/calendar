package client;

import database.Database;
import notification.AppointmentCanceledNotification;
import notification.AppointmentUpdateNotification;
import notification.InviteNotification;
import notification.Notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * Created by oysteinhauan on 24/02/15.
 */
public class Appointment {


    Timestamp start, end;

    int size;
    public ArrayList<String> attendingPeople = new ArrayList<String>();
    public ArrayList<String> invitedUsers = new ArrayList<String>();
    String subject;
    String description;
    Room room;
    public Integer roomId;
    int appointmentId;
    int attendingGroup;
    String owner;

    public Appointment(){

    }



    public Appointment(Timestamp start, Timestamp end,
                       String subject, String description, int size, String owner) {

        this.start = start;
        this.end = end;
        this.subject = subject;
        this.description = description;
        this.size = size;
        this.owner = owner;



    }

    public Appointment(Timestamp start, Timestamp end, String subject, String description, String owner){

        this.start = start;
        this.end = end;
        this.subject = subject;
        this.description = description;
        this.size = -1;
        this.owner = owner;
        this.roomId = null;

    }

    public static Appointment createAppointment(Timestamp start, Timestamp end,
                                                String subject, String description, int size,
                                                String owner, boolean useSystem ) {


        Database db = new Database();
        db.connectDb("all_s_gruppe40", "qwerty");
        Appointment appointment = null;
        if(useSystem) {
            try {
                appointment = new Appointment(start, end, subject, description, size, owner);
                appointment.findRoomId();
                appointment.setRoom(Room.getRoom(appointment.getRoomId()));
                appointment.createAppointmentInDB(appointment, db);


                ResultSet rs = db.readQuery("select last_insert_id();");
                int id = -1;
                while (rs.next()) {
                    id = rs.getInt("last_insert_id()");
                }
                appointment.setAppointmentId(id);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                db.closeConnection();
            }
        } else {


            appointment = new Appointment(start, end, subject, description, owner);

            appointment.createAppointmentInDB(appointment, db);

            ResultSet rs = db.readQuery("select last_insert_id();");
            int id = -1;
            try {
                while (rs.next()) {
                    id = rs.getInt("last_insert_id()");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            appointment.setAppointmentId(id);

        }

        return appointment;
    }

    public static Appointment createAppointment(Timestamp start, Timestamp end,
                                                String subject, String description, int size,
                                                String owner, boolean useSystem, Database db ) {


        //Database db = new Database();
        //db.connectDb("all_s_gruppe40", "qwerty");
        Appointment appointment = null;
        if(useSystem) {
            try {
                appointment = new Appointment(start, end, subject, description, size, owner);
                appointment.findRoomId();
                appointment.setRoom(Room.getRoom(appointment.getRoomId()));
                appointment.createAppointmentInDB(appointment, db);


                ResultSet rs = db.readQuery("select last_insert_id();");
                int id = -1;
                while (rs.next()) {
                    id = rs.getInt("last_insert_id()");
                }
                appointment.setAppointmentId(id);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                //db.closeConnection();
            }
        } else {


            appointment = new Appointment(start, end, subject, description, owner);

            appointment.createAppointmentInDB(appointment, db);

            ResultSet rs = db.readQuery("select last_insert_id();");
            int id = -1;
            try {
                while (rs.next()) {
                    id = rs.getInt("last_insert_id()");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            appointment.setAppointmentId(id);

        }

        return appointment;
    }


    @Override
    public String toString() {

        return ("\nSubject: " + subject + "\nDescription: " + description + "\nRoom: " + roomId + "\nStart: " + start + "\nEnd: " + end +"");
    }

    public static boolean checkIfOwner(String owner, Appointment appointment, int id){

        return(owner.equalsIgnoreCase(appointment.getOwner()) && appointment.getAppointmentId() == id );
    }



    public static Appointment getAppointment(int appointmentId){
        Database db = new Database();
        Appointment appointment = new Appointment();
        try {

            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select * from appointment where appointmentId = " + appointmentId +";";
            String sql2 = "select username from userAppointment where appointmentId = " + appointmentId +";";
            ResultSet rs = db.readQuery(sql);
            ResultSet rs2 = db.readQuery(sql2);
            while (rs.next()){
                appointment.setSubject(rs.getString("subject"));
                appointment.setAppointmentId(appointmentId);
                appointment.setDescription(rs.getString("description"));
                appointment.setStart(rs.getTimestamp("start"));
                appointment.setEnd(rs.getTimestamp("end"));
                appointment.setRoomId(rs.getInt("roomId"));
                appointment.setOwner(rs.getString("owner"));
            }
            rs.close();
            while (rs2.next()){
                appointment.attendingPeople.add(rs2.getString("username"));
            }
            rs2.close();
            db.closeConnection();
            return appointment;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        throw new IllegalArgumentException("Something went haywire!");


    }

    public static Appointment getAppointment(int appointmentId, Database db){
        //Database db = new Database();
        Appointment appointment = new Appointment();
        try {

            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select * from appointment where appointmentId = " + appointmentId +";";
            String sql2 = "select username from userAppointment where appointmentId = " + appointmentId +";";
            ResultSet rs = db.readQuery(sql);
            ResultSet rs2 = db.readQuery(sql2);
            while (rs.next()){
                appointment.setSubject(rs.getString("subject"));
                appointment.setAppointmentId(appointmentId);
                appointment.setDescription(rs.getString("description"));
                appointment.setStart(rs.getTimestamp("start"));
                appointment.setEnd(rs.getTimestamp("end"));
                appointment.setRoomId(rs.getInt("roomId"));
                appointment.setOwner(rs.getString("owner"));
            }
            rs.close();
            while (rs2.next()){
                appointment.attendingPeople.add(rs2.getString("username"));
            }
            rs2.close();
            //db.closeConnection();
            return appointment;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        throw new IllegalArgumentException("Something went haywire!");


    }



    public static boolean hasRecord(int id) throws SQLException {
        Database db = new Database();
        db.connectDb();
        String sql = "select appointmentId from appointment where appointmentId = " + id +";";
        ResultSet rs = db.readQuery(sql);
        if(!rs.next()){
            throw new IllegalArgumentException("Appointment doesnt exist.");
        }
        db.closeConnection();
        return true;
    }

    public static boolean hasRecord(int id, Database db) throws SQLException {
        //Database db = new Database();
        //db.connectDb();
        String sql = "select appointmentId from appointment where appointmentId = " + id +";";
        ResultSet rs = db.readQuery(sql);
        if(!rs.next()){
            throw new IllegalArgumentException("Appointment doesnt exist.");
        }
        //db.closeConnection();
        return true;
    }

    public void createAppointmentInDB(Appointment appointment, Database db) {


        //tar en appointment og legger til i databasen
        String sql = "insert into appointment (start, end, subject, description, roomId, owner) values( '"+ String.valueOf(appointment.getStart()) + "', '" +
                String.valueOf(appointment.getEnd()) + "', '" + appointment.getSubject() + "', '"
                + appointment.getDescription() + "', "
                + (appointment.getRoomId() + "") + ", '" + appointment.owner  + "');";

        //System.out.println(sql);
        db.updateQuery(sql);



    }



    public static void removeAppointmentInDB(int appointmentID) {
        Database db = new Database("all_s_gruppe40_calendar");
        db.connectDb("all_s_gruppe40", "qwerty");
        try {
            if (hasRecord(appointmentID)) {
                String sql1 = "DELETE from userAppointment where appointmentId = " + appointmentID + ";";
                String sql2 = "DELETE from appointment where appointmentId = " + appointmentID + ";";
                db.updateQuery(sql1);
                db.updateQuery(sql2);
                db.closeConnection();
            } else
                throw new IllegalArgumentException("denne avtalen finnes ikke");
        }   catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void removeAppointmentInDB(int appointmentID, Database db) {
        //Database db = new Database("all_s_gruppe40_calendar");
        //db.connectDb("all_s_gruppe40", "qwerty");
        getAppointment(appointmentID).sendAppointmentCanceledNotification();
        try {
            if (hasRecord(appointmentID)) {
                String sql1 = "DELETE from userAppointment where appointmentId = " + appointmentID + ";";
                String sql2 = "DELETE from appointment where appointmentId = " + appointmentID + ";";
                db.updateQuery(sql1);
                db.updateQuery(sql2);
               // db.closeConnection();
            } else
                throw new IllegalArgumentException("denne avtalen finnes ikke");
        }   catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateAppointmentInDB(String columnToUpdate, String updatedInfo, Database db){

        //Database db = new Database();
        //db.connectDb("all_s_gruppe40", "qwerty");

        //sjekker om ny slutt ikke er før nåværende start eller omvendt
        if (columnToUpdate == "slutt"){
            String sql = "Select start from appointment where appointmentId ='" + this.appointmentId + "';";
            ResultSet rs = db.readQuery(sql);
            Timestamp currentStart = null;
            try {
                while (rs.next()){
                    currentStart = rs.getTimestamp("start");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (Timestamp.valueOf(updatedInfo).before(currentStart)){
                throw new IllegalArgumentException("sluttid må være etter starttid!!");
            }
            Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());

            if( Timestamp.valueOf(updatedInfo).before(timeNow)){

                throw new IllegalArgumentException("Du må velge et fremtidig tidspunkt!!");
            }

            sendAppointmenUpdateNotification();

        }

        if (columnToUpdate  == "start"){

            String sql = "Select end from appointment where appointmentId ='" + this.appointmentId + "';";
            ResultSet rs = db.readQuery(sql);
            Timestamp currentEnd = null;

            try {
                while (rs.next()){
                    currentEnd = rs.getTimestamp("end");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (Timestamp.valueOf(updatedInfo).after(currentEnd)){
                throw new IllegalArgumentException("Starttid må være før sluttid!!!!");
            }

            Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());
            if( Timestamp.valueOf(updatedInfo).before(timeNow)){

                throw new IllegalArgumentException("Du må velge et fremtidig tidspunkt!!");
            }
            sendAppointmenUpdateNotification();
        }
        String sql =  "UPDATE appointment SET " + columnToUpdate + "='" + updatedInfo + "' WHERE appointmentId = '" + this.appointmentId + "';";
        db.updateQuery(sql);
       // db.closeConnection();


    }

    public void updateTimeInDB(Timestamp newStart, Timestamp newEnd, Database db){

        //Database db = new Database();
        //db.connectDb("all_s_gruppe40", "qwerty");

        //sjekker om ny slutt ikke er før nåværende start eller omvendt
        Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());
        if(newEnd.before(newStart) || newEnd.before(timeNow) || newStart.before(timeNow)){
            throw new IllegalArgumentException("Ugyldige tidspunkter.");
        }


        sendAppointmenUpdateNotification();
        String sql =  "UPDATE appointment SET start ='" + newStart + "' WHERE appointmentId = '" + this.appointmentId + "';";
        String sql2 =  "UPDATE appointment SET end ='" + newEnd + "' WHERE appointmentId = '" + this.appointmentId + "';";
        db.updateQuery(sql);
        db.updateQuery(sql2);
        // db.closeConnection();


    }





    public void updateAppointmentInDB(String columnToUpdate, String updatedInfo){

        Database db = new Database();
        db.connectDb("all_s_gruppe40", "qwerty");

        //sjekker om ny slutt ikke er før nåværende start eller omvendt
        if (columnToUpdate == "slutt"){
            String sql = "Select start from appointment where appointmentId ='" + this.appointmentId + "';";
            ResultSet rs = db.readQuery(sql);
            Timestamp currentStart = null;
            try {
                while (rs.next()){
                    currentStart = rs.getTimestamp("start");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (Timestamp.valueOf(updatedInfo).before(currentStart)){
                throw new IllegalArgumentException("sluttid må være etter starttid!!");
            }
            Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());

            if( Timestamp.valueOf(updatedInfo).before(timeNow)){

                throw new IllegalArgumentException("Du må velge et fremtidig tidspunkt!!");
            }

            sendAppointmenUpdateNotification();

        }

        if (columnToUpdate  == "start"){

            String sql = "Select end from appointment where appointmentId ='" + this.appointmentId + "';";
            ResultSet rs = db.readQuery(sql);
            Timestamp currentEnd = null;

            try {
                while (rs.next()){
                    currentEnd = rs.getTimestamp("end");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (Timestamp.valueOf(updatedInfo).after(currentEnd)){
                throw new IllegalArgumentException("Starttid må være før sluttid!!!!");
            }

            Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());
            if( Timestamp.valueOf(updatedInfo).before(timeNow)){

                throw new IllegalArgumentException("Du må velge et fremtidig tidspunkt!!");
            }
            sendAppointmenUpdateNotification();
        }
        String sql =  "UPDATE appointment SET " + columnToUpdate + "='" + updatedInfo + "' WHERE appointmentId = '" + this.appointmentId + "';";
        db.updateQuery(sql);
        db.closeConnection();


    }



    public void findRoomId() {


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
                throw new IllegalAccessError("No available roomz");
            }


            //System.out.println(sql);
            db.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void findRoomId(Database db) {


        try {
            //Database db = new Database("all_s_gruppe40_calendar");
            //db.connectDb("all_s_gruppe40", "qwerty");
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
                throw new IllegalAccessError("No available roomz");
            }


            //System.out.println(sql);
            //db.closeConnection();

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

    public String getOwner(){return owner;};

    public void setOwner(String owner){ this.owner = owner; }

    public ArrayList<String> getAttendingPeople() {
        return attendingPeople;
    }

    public void fetchAttendingPeopleFromDB(){
        Database db = new Database();
        String sql = "SELECT username FROM userAppointment WHERE appointmentId = " + this.appointmentId + "" ;
        db.connectDb();
        ResultSet rs = db.readQuery(sql);
        try {
            while (rs.next()) {
                attendingPeople.add(rs.getString("username"));
            }
        }catch (SQLException e){
        }
    }

    public void fetchAttendingPeopleFromDB(Database db){
        //Database db = new Database();
        String sql = "SELECT username FROM userAppointment WHERE appointmentId = " + this.appointmentId + "" ;
        //db.connectDb();
        ResultSet rs = db.readQuery(sql);
        try {
            while (rs.next()) {
                attendingPeople.add(rs.getString("username"));
            }
        }catch (SQLException e){
        }
    }

    public void fetchInvitedUsersFromDB(){
        Database db = new Database();
        String sql = "SELECT notificationId, recipient FROM notification WHERE appointmentId = " + this.appointmentId + " AND handled = 0;";
        db.connectDb();
        ResultSet rs = db.readQuery(sql);
        try {
            while (rs.next()){
                String user = rs.getString("recipient");
                Notification not = Notification.getNotificationFromDB(rs.getInt("notificationId"));
                if (!not.isHandled())
                    invitedUsers.add(user);
                    not.handle(db);

            }
        }catch (SQLException e){

        }
    }

    public void fetchInvitedUsersFromDB(Database db){
        //Database db = new Database();
        String sql = "SELECT notificationId, recipient FROM notification WHERE appointmentId = " + this.appointmentId + " AND handled = 0;";
        //db.connectDb();
        ResultSet rs = db.readQuery(sql);
        try {
            while (rs.next()){
                invitedUsers.add(rs.getString("recipient"));
                Notification not = Notification.getNotificationFromDB(rs.getInt("notificationId"));
                not.handle(db);
            }
        }catch (SQLException e){

        }
    }

    public void sendAppointmentCanceledNotification() {
        ArrayList<String> newInvitees = new ArrayList<String>();
        attendingPeople.clear();
        invitedUsers.clear();
        fetchInvitedUsersFromDB();
        fetchAttendingPeopleFromDB();

        for (String username: attendingPeople) {
            if (!username.equals(this.owner)) {
                newInvitees.add(username);
                removeAttendant(username);
                System.out.println("the attendants added to notification list");
            }
         }
        for (String username: invitedUsers) {
            newInvitees.add(username);;
            System.out.println("the invited added notification list list");
        }
        for (String username: newInvitees) {
            Notification cancelNot = new AppointmentCanceledNotification(username, this.owner, this.appointmentId);
            cancelNot.createNotificationInDB();
            System.out.println("the invited and attending people got a cancellation notification");
        }
    }

    public void sendAppointmenUpdateNotification(){
        ArrayList<String> recievers = new ArrayList<String>();
        attendingPeople.clear();
        invitedUsers.clear();
        fetchInvitedUsersFromDB();
        fetchAttendingPeopleFromDB();

        for (String username: attendingPeople) {
            if (!username.equals(this.owner)) {
                recievers.add(username);
                removeAttendant(username);
                System.out.println("the attendants added to invite list");
            }
        }
        for (String username: invitedUsers) {
            recievers.add(username);;
            System.out.println("the invited added invite list");
        }
        for (String username: recievers) {
            Notification updateNot = new AppointmentUpdateNotification(username, this.owner, this.appointmentId);
            updateNot.createNotificationInDB();
            System.out.println("the invited and attending people got a new invite");
        }
    }

    public void addAttendant(String username) {
        Database db = new Database();
        if(!User.existsCheck(username, db)){
            throw new IllegalArgumentException("User doesnt exist.");
        }

        db.connectDb();
        String sql1 = "select count(*) as no_of_attendants from userAppointment where appointmentId = " + this.appointmentId + ";";
        String sql2 = "select username from userAppointment where username = '" + username + "' and appointmentId = " + this.appointmentId + ";";
        String sql3 = "select size from room, appointment where room.roomId = appointment.roomId" +
                " and appointmentId = " + appointmentId +";";
        ResultSet rs1 = db.readQuery(sql1);
        ResultSet rs2 = db.readQuery(sql2);
        ResultSet rs3 = db.readQuery(sql3);
        int attendants = -1;

        int roomsize = 0;
        try {
            while (rs1.next()) {
                attendants = rs1.getInt("no_of_attendants");
            }
            //rs1.close();
            if(rs2.next()){
                throw new IllegalArgumentException("User is already registered.");
            }
            while(rs3.next()) {
                if (rs3.getInt("size") <= attendants) {
                    throw new IllegalArgumentException("Room is full, you must book a new room if you wish to add attendants.");
                }
            }
            rs2.close();
            rs3.close();
            rs1.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        attendingPeople.add(username);
        db.updateQuery("insert into userAppointment values('" + username + "', " + this.appointmentId + ");");
        db.closeConnection();
        System.out.println("");
        System.out.println("User added to event.");
        System.out.println("");

    }

    public void addAttendant(String username, Database db) {
        //Database db = new Database();
        if(!User.existsCheck(username, db)){
            throw new IllegalArgumentException("User doesnt exist.");
        }

        //db.connectDb();
        String sql1 = "select count(*) as no_of_attendants from userAppointment where appointmentId = " + this.appointmentId + ";";
        String sql2 = "select username from userAppointment where username = '" + username + "' and appointmentId = " + this.appointmentId + ";";
        String sql3 = "select size from room, appointment where room.roomId = appointment.roomId" +
                " and appointmentId = " + appointmentId +";";
        ResultSet rs1 = db.readQuery(sql1);
        ResultSet rs2 = db.readQuery(sql2);
        ResultSet rs3 = db.readQuery(sql3);
        int attendants = -1;

        int roomsize = 0;
        try {
            while (rs1.next()) {
                attendants = rs1.getInt("no_of_attendants");
            }
            //rs1.close();
            if(rs2.next()){
                throw new IllegalArgumentException("User is already registered.");
            }
            while(rs3.next()) {
                if (rs3.getInt("size") <= attendants) {
                    throw new IllegalArgumentException("Room is full, you must book a new room if you wish to add attendants.");
                }
            }
            rs2.close();
            rs3.close();
            rs1.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        attendingPeople.add(username);
        db.updateQuery("insert into userAppointment values('" + username + "', " + this.appointmentId + ");");
        //db.closeConnection();
        System.out.println("");
        System.out.println("User added to event.");
        System.out.println("");

    }

    public void removeAttendant(String username){
        Database db = new Database();
        db.connectDb();
        try{
            String sql = "delete from userAppointment where username = '" + username +"' and appointmentId = " + this.getAppointmentId()+";";
            db.updateQuery(sql);
        } catch(RuntimeException e){
            System.out.println("User not in event");
        }
        this.attendingPeople.remove(username);
    }

    public void removeAttendant(String username, Database db){
        //Database db = new Database();
        //db.connectDb();
        try{
            String sql = "delete from userAppointment where username = '" + username +"' and appointmentId = " + this.getAppointmentId()+";";
            db.updateQuery(sql);
        } catch(RuntimeException e){
            System.out.println("User not in event");
        }
        this.attendingPeople.remove(username);
    }

    //NOTIFICATION


    public void inviteAttendant(String username, Database db){
        invitedUsers.add(username);
        Notification invite = new InviteNotification(username, this.getOwner(), getAppointmentId());
        invite.createNotificationInDB(db);
    }





    //GET N' SET

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

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    // GROUP: Metoder for å knytte grupper til avtaler

    public void addAttendingGroup(Group group) {
        Database db = new Database();
        db.connectDb();

        //ArrayList<String> members = group.getMembers(group.getGroupID());
        //int no_of_members = members.size();

        String sql1 = "select count(*) as no_of_attendants from userGroup where groupId = " + group.getGroupID() + ";";
        String sql2 = "select groupId from groupAppointment where groupId = " + group.getGroupID() + " and appointmentId = " + this.appointmentId + ";";
        String sql3 = "select size from room, appointment where room.roomId = appointment.roomId" +
                " and appointmentId = " + appointmentId +";";
        String sql4 = "select username from userGroup where groupId = " + group.getGroupID();
        ResultSet rs1 = db.readQuery(sql1);
        ResultSet rs2 = db.readQuery(sql2);
        ResultSet rs3 = db.readQuery(sql3);
        ResultSet rs4 = db.readQuery(sql4);


        int attendants = -1;
        boolean alreadyRegistered = false;
        int roomsize = 0;
        try {
            while (rs1.next()) {
                attendants = rs1.getInt("no_of_attendants");
            }
            if(rs2.next()){
                alreadyRegistered = true;
            }
            while(rs3.next()){
                roomsize = rs3.getInt("size");
            }
            while(rs4.next()) {
                String username = rs4.getString("username");
                String sql5 = "select * from user where username = '" + username + "';";
                ResultSet rs5 = db.readQuery(sql5);
                if (!(rs5.next())) {
                    addAttendant(username);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {


            if (attendants >= roomsize /*|| attendingPeople.size() >= attendants*/){
                throw new IllegalArgumentException("Meeting is full or rom is too small.");
            }
            if (attendingGroup == group.getGroupID() || alreadyRegistered) {
                throw new IllegalArgumentException("Group is already partaking in this event.");

                }
            }


            //attendingPeople.add(username);
            attendingGroup = group.getGroupID();
            db.updateQuery("INSERT INTO groupAppointment (groupId, appointmentId) values (" + group.getGroupID() + ", " + this.appointmentId + ");");

            db.closeConnection();
        }



}
