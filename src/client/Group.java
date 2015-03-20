package client;

import database.Database;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IllegalFormatException;

/**
 * Created by andrealouise on 24.02.15.
 */
public class Group implements AppointmentListener {

    @Override
    public void appointmentNotification(Appointment appointment) {
        //send notification til alle medlemmer
    }

    String groupname;
    int groupID;
    Database db;
    String sql;
    ArrayList<String> members = new ArrayList<String>();
    ArrayList<Group> subgroups = new ArrayList<Group>();

    public Group(String groupname) {
        this.groupname = groupname;
    }

    public Group() {

    }
    public void createGroup(Group group) {
        db = new Database("all_s_gruppe40_calendar");
        db.connectDb("all_s_gruppe40", "qwerty");
        sql = ("INSERT INTO group_1 (name) values('" + (group.groupname) + "');");
        db.updateQuery(sql);
        db.closeConnection();
    }

    public void createGroup(Group group, Database db) {
       // db = new Database("all_s_gruppe40_calendar");
        //db.connectDb("all_s_gruppe40", "qwerty");
        sql = ("INSERT INTO group_1 (name) values('" + (group.groupname) + "');");
        db.updateQuery(sql);
       // db.closeConnection();
    }

    public static void removeGroupFromDB(String gname, Database db) throws IllegalArgumentException{
        db.updateQuery("delete from group_1 where name like '" + gname + "%';");
    }

    public ArrayList<String> getMembers(){
        return this.members;
    }

    public void createSubGroup(String subGroupName, Database db){
        Group subgroup = new Group(this.getGroupname() + "." + subGroupName);
        if(!subgroups.contains(subgroup)) {
            subgroups.add(subgroup);
            sql = ("insert into group_1 (name) values('" + subgroup.getGroupname() + "');");
            db.updateQuery(sql);
            System.out.println("Group created");
        }
    }


    public static void addMember(User user, int groupID) {
        Database db2 = new Database("all_s_gruppe40_calendar");
        db2.connectDb("all_s_gruppe40", "qwerty");
        String sql = ("INSERT INTO userGroup (username, groupID) values ('" + user.getUsername() + "', " + groupID + ");");
        db2.updateQuery(sql);
        db2.closeConnection();
    }

    public static void addMember(User user, int groupID, Database db) {
        //Database db2 = new Database("all_s_gruppe40_calendar");
        //db2.connectDb("all_s_gruppe40", "qwerty");
        String sql = ("INSERT INTO userGroup (username, groupID) values ('" + user.getUsername() + "', " + groupID + ");");
        db.updateQuery(sql);
        //db.closeConnection();
    }

    public static void removeMember(User user, int groupID) {
        Database db2 = new Database("all_s_gruppe40_calendar");
        db2.connectDb("all_s_gruppe40", "qwerty");
        String sql = ("DELETE FROM userGroup WHERE username = '" + user.getUsername() + "' AND groupID = " + groupID + ";");
        db2.updateQuery(sql);
        db2.closeConnection();
    }

    public static void removeMember(User user, int groupID, Database db) {
        //Database db2 = new Database("all_s_gruppe40_calendar");
       // db2.connectDb("all_s_gruppe40", "qwerty");
        String sql = ("DELETE FROM userGroup WHERE username = '" + user.getUsername() + "' AND groupID = " + groupID + ";");
        db.updateQuery(sql);
       // db2.closeConnection();
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getMember(int i){
        return this.members.get(i);

    }

    public static Group getGroup(int groupID) {
        Database db = new Database();
        Group group = new Group();
        try {

            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select * from group_1 where groupId = " + groupID + ";";
            ResultSet rs = db.readQuery(sql);

            while (rs.next()) {
                group.setGroupID(rs.getInt("groupId"));
                group.setGroupname(rs.getString("name"));
            }
            db.closeConnection();
            rs.close();
        } catch (SQLException e) {
        }
        return group;
    }

    public static Group getGroup(int groupID, Database db) {
        //Database db = new Database();
        Group group = new Group();
        try {

           // db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "select * from group_1 where groupId = " + groupID + ";";
            ResultSet rs = db.readQuery(sql);

            while (rs.next()) {
                group.setGroupID(rs.getInt("groupId"));
                group.setGroupname(rs.getString("name"));
            }
            //db.closeConnection();
            rs.close();
            sql = "select name from group_1 where name like '" + group.getGroupname()
                    + ".%'";
            rs = db.readQuery(sql);
            while (rs.next()){
                group.subgroups.add(new Group(rs.getString(0)));
            }
            rs.close();
        } catch (SQLException e) {
        }
        group.getMembers(db);
        return group;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public  String getGroupNameFromDB(int groupID) {

        try {
            Database db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT name FROM group_1 WHERE groupID = " + groupID +" ;");
            ResultSet rs = db.readQuery(sql);
            while(rs.next()) {
                this.groupname = rs.getString("name");
            }
            db.closeConnection();
            rs.close();

            }
        catch (SQLException e){
            throw new IllegalArgumentException("Something is not the way it should be.");

        }
        return this.groupname;
    }

    public  String getGroupNameFromDB(int groupID, Database db) {

        try {
            //Database db = new Database("all_s_gruppe40_calendar");
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT name FROM group_1 WHERE groupID = " + groupID +" ;");
            ResultSet rs = db.readQuery(sql);
            while(rs.next()) {
                this.groupname = rs.getString("name");
            }
            //db.closeConnection();
            rs.close();

        }
        catch (SQLException e){
            throw new IllegalArgumentException("Something is not the way it should be.");

        }
        return this.groupname;
    }

    public static int getGroupIDFromDB(String groupname) {
        int groupId = -1;
        try {

            Database db = new Database();
            db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT groupID FROM group_1 WHERE name = '" + groupname +"' ;");
            ResultSet rs = db.readQuery(sql);
            while(rs.next()) {
                groupId = rs.getInt("groupID");
            }
            db.closeConnection();
            rs.close();

        }
        catch (SQLException e){
            e.printStackTrace();

        }
        return groupId;
    }

    public static int getGroupIDFromDB(String groupname, Database db) {
        int groupId = -1;
        try {

            //Database db = new Database();
            //db = new Database("all_s_gruppe40_calendar");
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT groupId FROM group_1 WHERE name = '" + groupname +"';");
            ResultSet rs = db.readQuery(sql);
            if(!rs.next()){
                throw new IllegalAccessError();
            }
            while(rs.next()) {
                groupId = rs.getInt("groupId");
            }

           // db.closeConnection();
            rs.close();

        }
        catch (SQLException e){
            e.printStackTrace();

        }
        return groupId;
    }


    public ArrayList<String> getMembers(int groupID) {

        try {
            Database db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT username FROM userGroup WHERE groupId = " + groupID + ";");
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {

                members.add(rs.getString("username"));
            }

            db.closeConnection();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return members;

    }

    public static ArrayList<String> getMembers(int groupID, Database db) {
        ArrayList<String> members = new ArrayList<String>();
        try {
            //Database db = new Database("all_s_gruppe40_calendar");
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT username FROM userGroup WHERE groupId = " + groupID + ";");
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {

                members.add(rs.getString("username"));
            }

            //db.closeConnection();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return members;

    }

    public ArrayList<String> getMembers(String groupname) {
        this.groupID = getGroupIDFromDB(groupname);
        try {
            Database db = new Database("all_s_gruppe40_calendar");
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT username FROM userGroup WHERE groupId = " + this.groupID + ";");
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {

                members.add(rs.getString("username"));
            }

            db.closeConnection();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return members;

    }

    public void getMembers(Database db) {
        this.groupID = getGroupIDFromDB(this.getGroupname());
        try {
            //Database db = new Database("all_s_gruppe40_calendar");
           // db.connectDb("all_s_gruppe40", "qwerty");
            String sql = ("SELECT username FROM userGroup WHERE groupId = " + this.groupID + ";");
            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {

                this.members.add(rs.getString("username"));
            }

           // db.closeConnection();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }


    }

    public ArrayList<Appointment> getAppointmentsForGroup(Group group) {

        try {
            ArrayList<Integer> appIdList = new ArrayList<Integer>();
            ArrayList<Appointment> appList = new ArrayList<Appointment>();
            int groupId = group.getGroupID();

            Database db = new Database();
            db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "SELECT appointment.appointmentId FROM userAppointment, appointment, group_1, userGroup WHERE " +
                    "appointment.appointmentId = userAppointment.appointmentId AND userGroup.username = userAppointment.username" +
                    " AND group_1.groupId = " + String.valueOf(groupId) + " ORDER BY start;"; //+ " ORDER BY userAppointment.username";

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

    public ArrayList<Appointment> getAppointmentsForGroup(Group group, Database db) {

        try {
            ArrayList<Integer> appIdList = new ArrayList<Integer>();
            ArrayList<Appointment> appList = new ArrayList<Appointment>();
            int groupId = group.getGroupID();

            //Database db = new Database();
            //db.connectDb("all_s_gruppe40", "qwerty");
            String sql = "SELECT appointment.appointmentId FROM userAppointment, appointment, group_1, userGroup WHERE " +
                    "appointment.appointmentId = userAppointment.appointmentId AND userGroup.username = userAppointment.username" +
                    " AND group_1.groupId = " + String.valueOf(groupId) + " ORDER BY start;"; //+ " ORDER BY userAppointment.username";

            ResultSet rs = db.readQuery(sql);
            while (rs.next()) {
                if(!appIdList.contains(rs.getInt("appointmentId"))) {
                    appIdList.add(rs.getInt("appointmentId"));
                }
            }
            //db.closeConnection();

            for (Integer id: appIdList){

                appList.add(Appointment.getAppointment(id, db));

            }
            return appList;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String toString() {
        return "Group{" +
                "groupname='" + groupname + '\'' +
                ", groupID=" + groupID +
                '}';
    }

    public  static ArrayList<String> getGroupNames(Database db){

        ArrayList<String> groups = new ArrayList<String>();

        String sql = "SELECT name FROM group_1;";
        ResultSet rs = db.readQuery(sql);
        try {
            while(rs.next()){
                groups.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;

    }

    public  static ArrayList<String> getGroupNamesLowerCase(Database db){

        ArrayList<String> groups = new ArrayList<String>();

        String sql = "SELECT name FROM group_1;";
        ResultSet rs = db.readQuery(sql);
        try {
            while(rs.next()){
                groups.add(rs.getString("name").toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;

    }

    public static ArrayList<Group> getSubGroups(Group group){
        return group.subgroups;
    }
}
