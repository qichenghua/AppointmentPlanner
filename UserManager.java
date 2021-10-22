import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserManager{
    Integer m_id;
    Statement m_statement;

    public UserManager(Statement statement) throws SQLException
    {
        m_statement = statement;
        m_id = 0;
        m_statement.executeUpdate("drop table if exists User");
        m_statement.executeUpdate("create table User (UserID integer, Name string, PRIMARY KEY (UserID))");
    }

    public Integer add(String name) throws SQLException
    {
        m_statement.executeUpdate("insert into User values(" + m_id.toString() + ", '" + name + "')");
        m_id += 1;
        return m_id - 1;
    }

    public void change(Integer id, String name) throws SQLException
    {
        m_statement.executeUpdate("update User set Name = '" + name + "' where UserID = " + id.toString());
    }

    public void getInfo(Integer id) throws SQLException
    {
        ResultSet users = m_statement.executeQuery("select * from User where UserID = " + id.toString());
        while(users.next())
        {
            System.out.println("UserID = " + users.getInt("UserID"));
            System.out.println("Name = " + users.getString("Name"));
        }
    }

    public void getAllInfo(DateManager dateManager) throws SQLException
    {
        ArrayList<Integer> userIDs = new ArrayList<Integer>();
        ResultSet users = m_statement.executeQuery("select * from User");
        while(users.next())
        {
            userIDs.add(users.getInt("UserID"));
            System.out.println("UserID = " + users.getInt("UserID"));
            System.out.println("Name = " + users.getString("Name"));
        }
        for(Integer userID : userIDs)
        {
            dateManager.getDatesOf(userID);
        }
        
    }

    public void remove(Integer id, DateManager dateManager) throws SQLException
    {
        dateManager.removeDatesWith(id);
        m_statement.executeUpdate("delete from User where UserID = " + id.toString());
    }
}

