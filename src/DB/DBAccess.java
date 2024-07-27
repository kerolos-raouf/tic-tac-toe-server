
package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDriver;
/**
 *
 * @author emyal
 */

public class DBAccess {

    
   
   public static void insertPlayer(Player player) throws SQLException{
 
    DriverManager.registerDriver(new ClientDriver());
    Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe","root","root");
    PreparedStatement insertStmt = 
             con.prepareStatement("INSERT INTO PLAYER (username, password, score, isPlaying, isActive) VALUES (?,?,?,?,?)");
      
       insertStmt.setString(1, player.getUsername()); 
       insertStmt.setString(2, player.getPassword());
       insertStmt.setInt(3, 0);
       insertStmt.setBoolean(4,false);
       insertStmt.setBoolean(5, false);
       insertStmt.executeUpdate();
       insertStmt.close();
       con.close();
       
}
   public static Player getPlayer(String username) throws SQLException
   {
     ResultSet rs ;
     String foundUser;
     Player player = null;
       DriverManager.registerDriver(new ClientDriver());
       Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe","root","root");
     PreparedStatement stmt = 
             con.prepareStatement("SELECT USERNAME FROM PLAYER WHERE USERNAME =? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
  
     rs = stmt.executeQuery();
     while (rs.next())
     {
        foundUser = rs.getString("USERNAME");
        player = new Player (foundUser);
     }
     return player;
   }

  public static boolean signupValidation(String username) throws SQLException{
    
       ResultSet rs ;
       
       Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe","DB","DB");
       PreparedStatement stmt = 
            con.prepareStatement("SELECT USERNAME FROM PLAYER WHERE USERNAME=? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
       stmt.setString(1,username );
       rs = stmt.executeQuery();
       
       if(rs.next())
       {
           return true;
       }
       else {
           return false;
       }

}
  public static boolean loginValidation(String username, String password) throws SQLException
  {
      ResultSet rs;
      Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe","DB","DB");
      PreparedStatement stmt = 
                          con.prepareStatement("SELECT USERNAME,PASSWORD FROM PLAYER WHERE USERNAME=? AND PASSWORD=? ", 
                                  ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      stmt.setString(1, username);
      stmt.setString(1, password);
      rs = stmt.executeQuery();
       if(rs.next())
       {
           return true;
       }
       else {
           return false;
       }
  }
  
 }
  
   
    

