package client;

import database.Database;
import notification.Notification;
import notification.ReplyFromInvitedUserNotification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by oysteinhauan on 24/02/15.
 */
public class User{

    public String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String position;
    database.Database db;
    String sql;
    public ArrayList<Notification> notifications = new ArrayList<Notification>();

    public User(){

    }

    public User(String username, String password, String firstname,
                String lastname, String email, String position) {
        setUsername(username);
        setPassword(password);
        setFirstname(firstname);
        setLastname(lastname);
        setEmail(email);
        setPosition(position);

    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                '}';
    }


    public void addUserToDB(){
        db = new Database("all_s_gruppe40_calendar");
        sql = "INSERT INTO user (username, password, firstname, lastname, position, email) VALUES( '" + getUsername() + "', '" + getPassword() + "', '" + getFirstname() + "', '"
                + getLastname() + "', '" + getPosition() + "', '" + getEmail() + "');";

        db.connectDb("all_s_gruppe40", "qwerty");
        try {
            db.updateQuery(sql);
        } catch(RuntimeException e){
            System.out.println("Something went haywire!");
        } finally {
            db.closeConnection();
        }
    }

    public void addUserToDB(Database db){
        //db = new Database("all_s_gruppe40_calendar");
        sql = "INSERT INTO user (username, password, firstname, lastname, position, email) VALUES( '" + getUsername() + "', '" + getPassword() + "', '" + getFirstname() + "', '"
                + getLastname() + "', '" + getPosition() + "', '" + getEmail() + "');";

        //db.connectDb("all_s_gruppe40", "qwerty");
        try {
            db.updateQuery(sql);
        } catch(RuntimeException e){
            System.out.println("Something went haywire!");
        } finally {
            //db.closeConnection();
        }
    }


    public ArrayList<Appointment> getAppointmentsForUser(User user){

        try {
            ArrayList<Integer> appIdList = new ArrayList<Integer>();
            ArrayList<Appointment> appList = new ArrayList<Appointment>();

            Database db = new Database();
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select appointment.appointmentId, start from userAppointment, appointment " +
                    "where username = '" + user.getUsername() + "' and appointment.appointmentId = userAppointment.appointmentId " +
                            "order by start;";
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {
                appIdList.add(rs.getInt("appointmentId"));
            }
            db.closeConnection();

            for (Integer id: appIdList){
                appList.add(Appointment.getAppointment(id));
            }
            return appList;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public ArrayList<Appointment> getAppointmentsForUser(User user, Database db){

        try {
            ArrayList<Integer> appIdList = new ArrayList<Integer>();
            ArrayList<Appointment> appList = new ArrayList<Appointment>();

            //Database db = new Database();
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select appointment.appointmentId, start from userAppointment, appointment " +
                    "where username = '" + user.getUsername() + "' and appointment.appointmentId = userAppointment.appointmentId " +
                    "order by start;";
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {
                appIdList.add(rs.getInt("appointmentId"));
            }
            //db.closeConnection();

            for (Integer id: appIdList){
                appList.add(Appointment.getAppointment(id));
            }
            return appList;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public ArrayList<Integer> getAppointmentsForOwner(User owner, Database db){


        ArrayList<Integer> appIdList = new ArrayList<Integer>();
        String sql = "select appointment.appointmentId from appointment " +
                "where owner = '" + owner.getUsername() + "';";

        try {
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {
                appIdList.add(rs.getInt("appointmentId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appIdList;
    }

//    public User getUserFromDB(String username){
//        //henter ut informasjonen om en bruker fra databasen, basert på burkernavnet som skrives
//        try {
//            db = new Database("all_s_gruppe40_calendar");
//            db.connectDb("all_s_gruppe40", "qwerty");
//            sql = "SELECT * FROM user WHERE username='" + username + "';";
//            ResultSet rs = db.readQuery(sql);
//
//            while (rs.next()){
//                setUsername(username);
//                this.firstname = rs.getString("firstname");
//                this.lastname = rs.getString("lastname");
//                this.position = rs.getString("position");
//                this.email =rs.getString("email");
//            }
//            db.closeConnection();
//            rs.close();
//
//        } catch (SQLException e){
//        }
//        return this;
//    }

    public static boolean existsCheck(String username, Database db){
       User user = getUserFromDB(username, db);
        return (user != null);
    }

    public static User getUserFromDB(String username){
        //henter ut informasjonen om en bruker fra databasen, basert på burkernavnet som skrives

        Database db;
        User user = null;
        try {
            db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "SELECT * FROM user WHERE username='" + username + "';";
            ResultSet rs = db.readQuery(sql);

            while (rs.next()){
                user = new User(username, rs.getString("password"), rs.getString("firstname"), rs.getString("lastname"),
                        rs.getString("email"), rs.getString("position"));
            }
            db.closeConnection();
            rs.close();

        } catch (SQLException e){
        }
        return user;
    }

    public static User getUserFromDB(String username, Database db){
        //henter ut informasjonen om en bruker fra databasen, basert på burkernavnet som skrives

        //Database db;
        User user = null;
        try {
            //db = new Database("all_s_gruppe40_calendar");
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "SELECT * FROM user WHERE username='" + username + "';";
            ResultSet rs = db.readQuery(sql);

            while (rs.next()){
                user = new User(username, rs.getString("password"), rs.getString("firstname"), rs.getString("lastname"),
                        rs.getString("email"), rs.getString("position"));
            }
            //db.closeConnection();
            rs.close();

        } catch (SQLException e){
        }
        return user;
    }

    public static boolean usernameTaken(String username, Database db){
       // Database db = new Database();
       // db.connectDb();
        String sql = "select username from user where username = '" + username +"';";
        ResultSet rs = db.readQuery(sql);
        try {
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
           // db.closeConnection();
        }
        return false;

    }

    public void updateUserInfoInDB(String columnToUpdate, String updatedInfo){
        //skriv inn hvilken kolonne som skal få sin informasjon oppdatert, og hva den nye informasjonen skal være
        db = new Database("all_s_gruppe40_calendar");
        sql = "UPDATE user SET " + columnToUpdate + "='" + updatedInfo + "' WHERE username = '" + username + "';";
        db.connectDb("all_s_gruppe40", "qwerty");
        db.updateQuery(sql);
        db.closeConnection();
    }

    public void updateUserInfoInDB(String columnToUpdate, String updatedInfo, Database db){
        //skriv inn hvilken kolonne som skal få sin informasjon oppdatert, og hva den nye informasjonen skal være
        //db = new Database("all_s_gruppe40_calendar");
        sql = "UPDATE user SET " + columnToUpdate + "='" + updatedInfo + "' WHERE username = '" + username + "';";
       // db.connectDb("all_s_gruppe40", "qwerty");
        db.updateQuery(sql);
       // db.closeConnection();
    }




    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullName(){
        return "" + getFirstname() + " " + getLastname();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }





    public void deleteUserFromDb(String username) {
        db = new Database("all_s_gruppe40_calendar");
        sql = "DELETE FROM user WHERE username='" + username + "';";
        db.connectDb("all_s_gruppe40", "qwerty");
        db.updateQuery(sql);
        db.closeConnection();
    }

    public void deleteUserFromDb(String username, Database db) {
        //db = new Database("all_s_gruppe40_calendar");
        sql = "DELETE FROM user WHERE username='" + username + "';";
        //db.connectDb("all_s_gruppe40", "qwerty");
        db.updateQuery(sql);
        //db.closeConnection();
    }



    public boolean isAdmin(){
        db = new Database();
        db.connectDb();
        sql = "select admin from user where username = '" + this.getUsername() +"';";
        ResultSet rs = db.readQuery(sql);
        try {
            while(rs.next()){
                if(rs.getInt(1) == 1){
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Oops.");
            e.printStackTrace();
        } finally {
            db.closeConnection();

        }
        return false;

    }

    public boolean isAdmin(Database db){
        //db = new Database();
        //db.connectDb();
        sql = "select admin from user where username = '" + this.getUsername() +"';";
        ResultSet rs = db.readQuery(sql);
        try {
            while(rs.next()){
                if(rs.getInt(1) == 1){
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Oops.");
            e.printStackTrace();
        } finally {
            //db.closeConnection();

        }
        return false;

    }

    public static void setAdmin(String username){
        Database db;
        db = new Database();
        db.connectDb();
        String sql = "update user set admin = 1 where username = '" + username +"';";

        db.updateQuery(sql);
        db.closeConnection();
    }

    public static void setAdmin(String username, Database db){
        //Database db;
        //db = new Database();
        //db.connectDb();
        String sql = "update user set admin = 1 where username = '" + username +"';";

        db.updateQuery(sql);
        //db.closeConnection();
    }

    //NOTIFICATION

    public ArrayList<Notification> getNotificationsForUser(String username){
    try{
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        ArrayList<Integer> notificationIds = new ArrayList<Integer>();

        Database db = new Database();
        db.connectDb("all_s_gruppe40", "qwerty");
        String sql = "SELECT notificationId FROM notification" +
                " WHERE recipient = '" + username + "';";
        ResultSet rs = db.readQuery(sql);
        while (rs.next()) {
            notificationIds.add(rs.getInt("notificationId"));
        }
        db.closeConnection();

        for (Integer id: notificationIds){
            notifications.add(Notification.getNotificationFromDB(id));
        }
        return notifications;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }

    public ArrayList<Notification> getNotificationsForUser(String username, Database db){
        try{
            ArrayList<Notification> notifications = new ArrayList<Notification>();
            ArrayList<Integer> notificationIds = new ArrayList<Integer>();

            //Database db = new Database();
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "SELECT notificationId FROM notification" +
                    " WHERE recipient = '" + username + "';";
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {
                notificationIds.add(rs.getInt("notificationId"));
            }
            //db.closeConnection();

            for (Integer id: notificationIds){
                notifications.add(Notification.getNotificationFromDB(id));
            }
            return notifications;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fetchNotifications(Database db){
        notifications.clear();
        notifications = getNotificationsForUser(username, db);
    }


    public void addNotification(Notification notification){
        notifications.add(notification);
    }

    public Notification getFirstNotification(){
        return notifications.get(0);
    }

    public int getNumberOfNewNotifications(){
        int size = 0;
        for (Notification notification: notifications) {
            if (!notification.isHandled()){
                size++;
            }
        }
        return size;
    }

    public void removeAppointmentNotification(Appointment appointment){
        notifications.remove(appointment);
    }



    public void replyToInvite(Notification inviteNotification, Database db){
        int swValue;
        boolean replied = false;
        Boolean reply = null;
        Appointment ap = Appointment.getAppointment(inviteNotification.getAppointmentId());
        Notification replyToInviteNotification;


            // Display menu graphics
            System.out.println("============================");
            System.out.println("|       YOUR OPTIONS       |");
            System.out.println("============================");
            System.out.println("|        1. Accept         |");
            System.out.println("|        2. Decline        |");
            System.out.println("============================");


           // Switch construct
        while(!replied){
            swValue = KeyIn.inInt("Select option: ");


                switch (swValue) {
                    case 1:
                        System.out.println("");
                        System.out.println("Option 1 selected: You have accepted the invitation.\n\n");
                        System.out.println("");
                        reply = true;
                        try{
                            ap.addAttendant(username, db);
                        }catch (IllegalArgumentException e){
                            System.out.println("User is already registered.");
                            inviteNotification.handle(db);
                            replied = true;
                            break;

                        }
                        replied = true;
                        inviteNotification.handle(db);
                        replyToInviteNotification = new ReplyFromInvitedUserNotification(ap.getOwner(), username, ap.appointmentId, reply);
                        replyToInviteNotification.createNotificationInDB();
                        System.out.println("");
                        System.out.println("" + ap.getOwner() + " will now be notified about your reply.");
                        System.out.println("");
                        break;
                    case 2:
                        System.out.println(" ");
                        System.out.println("Option 2 selected: You have declined the invitation.");
                        System.out.println(" ");
                        reply = false;
                        replied = true;
                        inviteNotification.handle(db);
                        replyToInviteNotification = new ReplyFromInvitedUserNotification(ap.getOwner(), username, ap.appointmentId, reply);
                        replyToInviteNotification.createNotificationInDB();
                        System.out.println("");
                        System.out.println("" + ap.getOwner() + " will now be notified about your reply.\n\n");
                        System.out.println("");
                        break;
                    default:
                        System.out.println("Invalid selection (ノಠ益ಠ)ノ彡┻━┻");
                        break;
                    // This break is not really necessary
                }

        }
    }


}

