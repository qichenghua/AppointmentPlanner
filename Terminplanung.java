import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class Terminplanung
{
    public static void main(String[] args)
    {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:terminplanung.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            UserManager userManager = new UserManager(statement);
            DateManager dateManager = new DateManager(statement);

            Integer userID0 = userManager.add("Max Mustermann");
            Integer userID1 = userManager.add("Erika Musterfrau");
            Integer userID2 = userManager.add("Maria Musterfrau");
            dateManager.add(0,5,"Hochzeit", Arrays.asList(userID0, userID1));
            System.out.println("Test einer Nutzeraenderung:");
            System.out.println("Vorher:");
            userManager.getInfo(userID1);
            userManager.change(userID1, "Erika Mustermann");
            System.out.println("Nachher:");
            userManager.getInfo(userID1);
            dateManager.add(6,10,"grosses Treffen", Arrays.asList(userID0, userID1, userID2));
            dateManager.add(11,15,"Einzeltermin", Arrays.asList(userID2));
            System.out.println("Test einer Nutzerentfernung:");
            System.out.println("Vorher:");
            userManager.getAllInfo(dateManager);
            userManager.remove(userID2, dateManager);
            System.out.println("Nachher:");
            userManager.getAllInfo(dateManager);

            Integer dateID = dateManager.add(11,15,"Termin zum Aendern oder Loeschen", Arrays.asList(userID0));
            System.out.println("Test einer Terminaenderung:");
            System.out.println("Vorher:");
            dateManager.getDatesOf(userID0);
            dateManager.change(dateID, 16, 20, "Modifizierter Termin", Arrays.asList(userID0));
            System.out.println("Nachher:");
            dateManager.getDatesOf(userID0);
            System.out.println("Test einer Terminentfernung:");
            System.out.println("Vorher:");
            dateManager.getDatesOf(userID0);
            dateManager.remove(dateID);
            System.out.println("Nachher:");
            dateManager.getDatesOf(userID0);

            System.out.println("Test eines kollidierenden Termins:");
            try
            {
                dateManager.add(0,5,"Kollision",Arrays.asList(userID0));
                throw new RuntimeException("We added a colliding date, which should have triggered an error, however none was trigerred.");
            }
            catch(DateCollisionException e)
            {
                
            }
            System.out.println("Test erfolgreich bestanden");

            System.out.println("Test: Termine von einem Nutzer mit einem anderen Nutzer:");
            dateManager.getDatesOfWith(userID0, userID1);
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}

