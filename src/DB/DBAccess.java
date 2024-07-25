
package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDriver;
/**
 *
 * @author emyal
 */

public class DBAccess {
    
    // this method is used to put player signup info into DB
   public void insertPlayer(Player player) throws SQLException{
       boolean isExisting = false;
       DriverManager.registerDriver(new ClientDriver());
       Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe","root","root");
       PreparedStatement insertStmt = 
               con.prepareStatement("INSERT INTO Player (username, password, score, isPlaying, isActive) VALUES (?,?,?,?,?)");
       if (isExisting == false)
       { insertStmt.setString(1, player.getUsername()); 
       insertStmt.setString(1, player.getPassword());
       insertStmt.setInt(1, player.getScore());
       insertStmt.setBoolean(1, player.isIsPlaying());
       insertStmt.setBoolean(1, player.isIsActive());
       }
       insertStmt.executeUpdate();
       insertStmt.close();
       con.close();
}
   //this methos is used to retrieve player info in order to login in
   
    
}
