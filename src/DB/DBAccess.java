
package DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;
/**
 *
 * @author emyal
 */

public class DBAccess {
    private static Connection con;
    
    //has all players and is sensitive updatable
    private static PreparedStatement ps;
    //the ps's sensitive updatable result set
    private static ResultSet rs;
  
    
    public static void initiateDbConnection() throws SQLException{
        if(con==null){
            DriverManager.registerDriver(new ClientDriver());
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/tictactoe", "root", "root");
            DatabaseMetaData dbm = con.getMetaData();
            // check if "player" table is there
            ResultSet tables = dbm.getTables(null, null, "PLAYER", null);
            //create table if not exists
            if (!tables.next()) {
                PreparedStatement temp= con.prepareStatement("CREATE TABLE PLAYER " +
                   "(username VARCHAR(50), " + 
                   " email VARCHAR(50) NOT NULL, " +     
                   " password VARCHAR(50) NOT NULL, " +
                   " score INT NOT NULL WITH DEFAULT 0, " +
                   " isplaying BOOLEAN NOT NULL WITH DEFAULT FALSE, " +
                   " isactive BOOLEAN NOT NULL WITH DEFAULT FALSE, " +
                   " PRIMARY KEY ( username ))");
                temp.executeUpdate();
                temp.close();
                System.out.println("success");
            }
            getAllPlayers();
        }
    }
    
   public static ArrayList<Player> getAllPlayers() throws SQLException{
        if(ps != null) ps.close();
        ps = con.prepareStatement("select username, isplaying, isactive from Player", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        rs = ps.executeQuery();
        ArrayList<Player> temp = new ArrayList<>();
        while(rs.next()){
            temp.add(new Player(rs));
        }
        return temp;
    }   
   
   public static void insertPlayer(Player player) throws SQLException{
 
    PreparedStatement insertStmt = 
             con.prepareStatement("INSERT INTO PLAYER (username, password, score, isPlaying, isActive) VALUES (?,?,?,?,?)");
      
       insertStmt.setString(1, player.getUsername()); 
       insertStmt.setString(2, player.getPassword());
       insertStmt.setInt(3, 0);
       insertStmt.setBoolean(4,false);
       insertStmt.setBoolean(5, false);
       insertStmt.executeUpdate();
       insertStmt.close();
       
}

  public static boolean signupValidation(String username) throws SQLException{
    
       ResultSet rs ;
       
       PreparedStatement stmt = 
            con.prepareStatement("SELECT USERNAME FROM PLAYER WHERE USERNAME=? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
       stmt.setString(1, username);
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
  
   public static void closeConnection() throws SQLException{
        ps.close();
        con.close();
        con = null;
        ps = null;
        rs = null;
    }
  
 }