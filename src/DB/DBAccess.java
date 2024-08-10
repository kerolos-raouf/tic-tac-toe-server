
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
                   "(USERNAME VARCHAR(50), " +   
                   " PASSWORD VARCHAR(50) NOT NULL, " +
                   " SCORE INT NOT NULL WITH DEFAULT 0, " +
                   " ISPLAYING BOOLEAN NOT NULL WITH DEFAULT FALSE, " +
                   " ISACTIVE BOOLEAN NOT NULL WITH DEFAULT FALSE, " +
                   " PRIMARY KEY ( USERNAME ))");
                temp.executeUpdate();
                temp.close();
                System.out.println("success");
            }
            getAllPlayers();
        }
    }
    
   public static ArrayList<Player> getAllPlayers() throws SQLException{
        if(ps != null) ps.close();
        ps = con.prepareStatement("SELECT * FROM PLAYER ORDER BY SCORE DESC", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        rs = ps.executeQuery();
        ArrayList<Player> temp = new ArrayList<>();
        while(rs.next()){
            temp.add(new Player(rs));
        }
        return temp;
    }   
   
   public static void insertPlayer(Player player) throws SQLException{
 
    PreparedStatement insertStmt = 
             con.prepareStatement("INSERT INTO PLAYER (USERNAME, PASSWORD, SCORE, ISPLAYING, ISACTIVE) VALUES (?,?,?,?,?)");
      
       insertStmt.setString(1, player.getUsername()); 
       insertStmt.setString(2, player.getPassword());
       insertStmt.setInt(3, 0);
       insertStmt.setBoolean(4,false);
       insertStmt.setBoolean(5, false);
       insertStmt.executeUpdate();
       insertStmt.close();
       
}
   public static Player getPlayerByUsername(String username) throws SQLException
   {
     ResultSet rs ;
     Player player = null;
     PreparedStatement stmt = 
             con.prepareStatement("SELECT * FROM PLAYER WHERE USERNAME =? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
  
     stmt.setString(1, username);
     rs = stmt.executeQuery();
     if (rs.next())
     {
        player = new Player(rs);
     }
     return player;
   }
   
   

  public static boolean signupValidation(String username) throws SQLException{
    
       ResultSet rs;
       PreparedStatement stmt = 
            con.prepareStatement("SELECT USERNAME FROM PLAYER WHERE USERNAME=? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

       stmt.setString(1, username);

       rs = stmt.executeQuery();
       
       return rs.next(); 
       

}
  public static boolean loginValidation(String username, String password) throws SQLException
  {
      ResultSet rs;
      PreparedStatement stmt = 
                          con.prepareStatement("SELECT USERNAME,PASSWORD FROM PLAYER WHERE USERNAME=? AND PASSWORD=? ", 
                                  ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      stmt.setString(1, username);
      stmt.setString(2, password);
      rs = stmt.executeQuery();
      return rs.next();
  }
  

  public static void setPlayingState(String username, boolean playing) throws SQLException
  {
    PreparedStatement updateStmt = 
             con.prepareStatement("UPDATE PLAYER SET ISPLAYING= ?  WHERE USERNAME =?");
    updateStmt.setBoolean(1,playing);
    updateStmt.setString(2, username);
    updateStmt.executeUpdate();
  }
  
  public static void resetAllPlayersStates() throws SQLException
  {
    PreparedStatement updateStmt = 
             con.prepareStatement("UPDATE PLAYER SET ISPLAYING=? , ISACTIVE=?");
    updateStmt.setBoolean(1,false);
    updateStmt.setBoolean(2, false);
    updateStmt.executeUpdate();
  }
  
  public static void updateWinningPlayerScore(String winningUserName) throws SQLException
  {
    PreparedStatement updateStmt = 
             con.prepareStatement("UPDATE PLAYER SET SCORE = SCORE + 1  WHERE USERNAME = ?");
    updateStmt.setString(1, winningUserName);
    updateStmt.executeUpdate();
  }
  
  public static void updateLosingPlayerState(String losingPlayerState) throws SQLException
  {
    PreparedStatement updateStmt = 
             con.prepareStatement("UPDATE PLAYER SET ISPLAYING = ?, SCORE = CASE WHEN SCORE > 0 THEN SCORE -1 ELSE SCORE END  WHERE USERNAME = ?");
    updateStmt.setBoolean(1,false);
    updateStmt.setString(2, losingPlayerState);
    updateStmt.executeUpdate();
  }
  
  public static void logout(String username) throws SQLException{
      PreparedStatement updateStmt = 
             con.prepareStatement("UPDATE PLAYER SET ISACTIVE = ?, ISPLAYING = ?  WHERE USERNAME =?");
    updateStmt.setBoolean(1,false);
    updateStmt.setBoolean(2,false);
    updateStmt.setString(3, username);
    updateStmt.executeUpdate();
  }
  
   public static void setActivityState(String username, boolean active) throws SQLException
  {
    PreparedStatement updateStmt = 

             con.prepareStatement("UPDATE PLAYER SET ISACTIVE = ?  WHERE USERNAME = ?");


    updateStmt.setBoolean(1,active);
    updateStmt.setString(2, username);
    updateStmt.executeUpdate();
  }
   
   
   
  public static void closeConnection() throws SQLException{
      if(ps != null)  ps.close();
      if(con != null)  con.close();
        con = null;
        ps = null;
        rs = null;
    }
 }