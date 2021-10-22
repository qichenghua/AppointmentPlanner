import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.*;

public class DateManager{
    Integer m_id;
    Statement m_statement;

    public DateManager(Statement statement) throws SQLException
    {
        m_statement = statement;
        m_id = 0;
        m_statement.executeUpdate("drop table if exists Date");
        m_statement.executeUpdate("create table Date (DateID integer, Start integer, End integer, Title string, PRIMARY KEY (DateID))");
        m_statement.executeUpdate("drop table if exists DateUser");
        m_statement.executeUpdate("create table DateUser (UserID integer, DateID integer, FOREIGN KEY(UserID) REFERENCES User(UserID), FOREIGN KEY(DateID) REFERENCES Date(DateID))");
    }

    public void add(Integer id, Integer start, Integer end, String title, List<Integer> participantIDs) throws SQLException
    {
        // find potentially colliding date
        for(Integer participantID : participantIDs)
        {
            ResultSet otherDatesOfThisUser = m_statement.executeQuery(
                "select Start, End, Title from Date inner join DateUser on Date.DateID = DateUser.DateID where DateUser.UserID = " + participantID.toString());
            while (otherDatesOfThisUser.next())
            {
                if ((otherDatesOfThisUser.getInt("Start") <= start && otherDatesOfThisUser.getInt("End") > start)
                    || (otherDatesOfThisUser.getInt("Start") < end && otherDatesOfThisUser.getInt("End") >= end))
                    throw new DateCollisionException("Could not add Date because there is a collision. Tried to add "
                        + title + " from " + start.toString() + " until " + end.toString() + ", but participant "
                        + participantID.toString() + " already has " + otherDatesOfThisUser.getString("Title") + " from "
                        + Integer.valueOf(otherDatesOfThisUser.getInt("Start")).toString() + " until "
                        + Integer.valueOf(otherDatesOfThisUser.getInt("End")).toString());
            }
        }

        m_statement.executeUpdate("insert into Date values(" + id.toString() + ", " + start.toString() + ", " + end.toString() + ", '" + title + "')");

        for(Integer participantID : participantIDs)
        {
            m_statement.executeUpdate("insert into DateUser values(" + participantID.toString() + ", " + id.toString() + ")");
        }
    }

    public Integer add(Integer start, Integer end, String title, List<Integer> participantIDs) throws SQLException
    {
        add(m_id, start, end, title, participantIDs);
        m_id += 1;
        return m_id - 1;
    }

    public void remove(Integer id) throws SQLException
    {
        m_statement.executeUpdate("delete from DateUser where DateID = " + id.toString());
        m_statement.executeUpdate("delete from Date where DateID = " + id.toString());
    }

    public void change(Integer id, Integer start, Integer end, String title, List<Integer> participantIDs) throws SQLException
    {
        remove(id);
        add(id, start, end, title, participantIDs);
    }

    public void getDatesOf(Integer id) throws SQLException
    {
        System.out.println("These are the dates of user " + id.toString());
        ResultSet dates = m_statement.executeQuery("select * from Date inner join DateUser on Date.DateID = DateUser.DateID where DateUser.UserID = " + id.toString());
        while(dates.next())
        {
            System.out.println(dates.getString("Title") + " from " + dates.getString("Start") + " until " + dates.getString("End"));
        }        
    }

    public void getDatesOfWith(Integer id0, Integer id1) throws SQLException
    {
        System.out.println("These are the dates of user " + id0.toString() + " with user " + id1.toString());
        ArrayList<String> dateIDsWithFirstUser = new ArrayList<String>();
        ResultSet datesWithFirstUser = m_statement.executeQuery("select * from Date inner join DateUser on Date.DateID = DateUser.DateID where DateUser.UserID = " + id0.toString());
        while(datesWithFirstUser.next())
        {
            dateIDsWithFirstUser.add(datesWithFirstUser.getString("DateID"));
        }
        for (String dateID : dateIDsWithFirstUser)
        {
            ResultSet datesWithBothUsers = m_statement.executeQuery(
                "select * from Date inner join DateUser on Date.DateID = DateUser.DateID where DateUser.UserID = " + id1.toString() + " and DateUser.DateID = " + dateID);
            if (datesWithBothUsers.next())
                System.out.println(datesWithBothUsers.getString("Title") + " from " + datesWithBothUsers.getString("Start") + " until " + datesWithBothUsers.getString("End"));
        }
    }

    public void removeDatesWith(Integer id) throws SQLException
    {
        // find dates with user
        ArrayList<String> dateIDsWithUser = new ArrayList<String>();
        ResultSet datesWithUser = m_statement.executeQuery("select DateID from DateUser where UserID = " + id.toString());
        while(datesWithUser.next())
        {
            dateIDsWithUser.add(datesWithUser.getString("DateID"));
        }
        for(String dateID : dateIDsWithUser)
        {
            // check whether other users attend the same date
            ResultSet datesWithOtherUsers = m_statement.executeQuery("select * from DateUser where DateID = " + dateID + " and UserID <> " + id.toString());
            // delete date if no other users attend
            if(!datesWithOtherUsers.next()) m_statement.executeUpdate("delete from Date where DateID = " + dateID);
        }
        m_statement.executeUpdate("delete from DateUser where UserID = " + id.toString());
    }
}

