/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import DB.DBAccess;
import static DB.DBAccess.initiateDbConnection;
import DB.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.JSONParser;
import domain.PlayerMessageBody;
import domain.ScoreBoardItem;
import domain.SocketRoute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shyasuo
 */
public class MyServer extends Thread{
    private ServerSocket socket;
    private Socket createdSocket;
    private int port;
    private String addr;
    
  
    //Singleton instance of server
    private static MyServer instance;

    private static void setInstance(MyServer instance)
    {
        MyServer.instance = instance;
    }
    
    /**
     * Creates a server withe the specified local IP address, and port.
     * @param serverAddr the server address (should be your device's local IP address)
     * @param port the port number
     * @return a created instance of running server
     * @throws IllegalArgumentException if server address or port number were invalid
     * @throws IOException if an exception occurs while trying to communicate or receive data from client (Player)
     */
    public static MyServer initialize(String serverAddr, int port) throws IllegalArgumentException, IOException{
        
        if(instance != null){//basically restarts the server with different parameters
            instance.close();
        }
        instance = new MyServer(serverAddr, port);
        return instance;
    }
    
    /**
     * Creates a server withe the specified local IP address, and an ephemeral port.
     * the ephemeral port value can be retrieved by calling the getter getPort on the newly created server instance.
     * @param serverAddr the server address (should be your device's local IP address)
     * @return a created instance of running server
     * @throws IllegalArgumentException if server address or port number were invalid
     * @throws IOException if an exception occurs while trying to communicate or receive data from client (Player)
     */
    public static MyServer initialize(String serverAddr) throws IllegalArgumentException, IOException{
        
        if(instance != null){//basically restarts the server with different parameters
            instance.close();
        }
        instance = new MyServer(serverAddr, 0);
        return instance;
    }
    
    public static MyServer getInstance() throws InstantiationException{
        if(instance == null) throw new InstantiationError("An instance was not yet created, Create an instance by calling initialize first.");
        return instance;
    }

    public int getPort()
    {
        return port;
    }

    public String getAddr()
    {
        return addr;
    }
    
    
    private MyServer(String addr, int port) throws UnknownHostException, IllegalArgumentException, IOException{

            if(addr.split("[.]").length != 4) throw new IllegalArgumentException("invalid IP address");
            this.addr = addr;
            if (port < 0 || port > 0xFFFF) throw new IllegalArgumentException("Invalid port value");
            socket = new ServerSocket(port, 0, InetAddress.getByName(this.addr));
        try {
            DBAccess.initiateDbConnection();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
//              socket = new ServerSocket(port);
              System.out.println(socket.getInetAddress().getHostAddress());
              System.out.println(socket.getLocalPort());
            this.port = socket.getLocalPort();

       
    }

    @Override
    public void run()
    {
        
            try {
                while(true){
                    createdSocket = socket.accept();
                    new PlayerHandler(createdSocket);
                }
                //TODO: player handler implementation
            }catch(SocketException ex){
                  System.out.println("server closed");
             } catch (IOException ex) {
               Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
             } 
            
    }

    
    public void close() throws IOException
    {
        //NOTE: this should throw an exception because the thread will be waiiting for a player to connect
        socket.close();
        stop();
//        interrupt();
        // TODO: resources cleaning functionality of player handler
        MyServer.setInstance(null);
    }
}

class PlayerHandler extends Thread{
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintStream printStream;
    private PlayerHandler opponent;
    private Player player;
    
    static Vector<PlayerHandler> playerHandlers;

    
    static{
        playerHandlers = new Vector<>();
    }
    
    public PlayerHandler(Socket playerSocket)
    {
        try {
            this.socket = playerSocket;
            bufferedReader = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());
            player = null;
            opponent = null;
            playerHandlers.add(this);
//            jsonObject = new JSONObject(Player.toMap(player));
//              jsonObject = new JSONObject(new HashMap<String, Object>(){{
//           put("hello", "hello");
//        }});
//            outputStreamWriter.write(jsonObject.toString());
//            JSONObject json = new JSONObject();
//            json.put("type", "CONNECT");
//            Socket s = new Socket("192.168.0.100", 7777);
//            try (OutputStreamWriter out = new OutputStreamWriter(
//            s.getOutputStream(), StandardCharsets.UTF_8)) {
//            out.write(json.toString());
//}
            start();
        } catch (IOException ex) {
            System.out.println("Error in playerhandler");
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        int i = 0;
       while(true){
            try {
                String str = bufferedReader.readLine();
                if(str == null) throw new IOException();
                System.out.println(str);
                PlayerMessageBody pl = JSONParser.convertFromJSONToPlayerMessageBody(str);
                switch(pl.getState())
                {
                    case PLAYER_MOVE:
                    {
                        setMoveToTheOpponent(pl.getOpponentName(),pl.getMove());
                        System.out.println("move number " + i++);
                        break;
                    }
                    case LOG_IN:

                    {
                        checkLoginValidation(pl.getUsername(),pl.getPassword());
                        break;
                    }
                    case LOG_IN_RESPONSE:
                        break;
                    case SIGN_UP:

                    {
                        checkSignupValidation(pl.getUsername(),pl.getPassword());
                        break;
                    }
                    case SIGN_UP_RESPONSE:
                        break;
                    case AVAILABLE_PLAYERS:
                        sendAllPlayers();
                        break;
                    case LOG_OUT:
                        break;
                    case SURRENDER:
                        break;
                    case CHECK_SERVER:
                        break;
                    case ALL_PLAYERS:
                        sendAllPlayers();
                        break;
                    case REQUEST_TO_PLAY:
                    {
                        sendRequestToOppenent(pl.getOpponentName());
                        break;
                    } 
                       
                    case RESPONSE_TO_REQUEST_TO_PLAY:
                    {
                        respondToRequestToPlay(pl.getOpponentName(),pl.getResponse(),pl.isPlayerSymbol());
                        if(pl.getResponse() == true){
                            for(PlayerHandler ph : playerHandlers){
                                opponent = ph;
                                opponent.opponent = this;
                                break;
                            }
                        }
                        break;
                    }
                    case SCORE_BOARD:
                    {
                        sendScoreBoard();
                        break;
                    }  
                    case PLAY_AGAIN:
                    {
                        playAgain(pl.getOpponentName());
                        break;
                    }
                      
                    default:
                        throw new AssertionError(pl.getState().name());
                }
                System.out.println(str);
               } catch (IOException ex) {
                   System.out.println("connection reset exception message is expected, just added this line to check if another exception message is thrown");
                   if(opponent != null){
                       PlayerMessageBody pl = new PlayerMessageBody();
                   pl.setState(SocketRoute.SURRENDER);
                       try {
                           DBAccess.updateWinningPlayerScore(opponent.player.getUsername());
                           DBAccess.updateLosingPlayerState(player.getUsername());
                           opponent.printStream.println(JSONParser.convertFromPlayerMessageBodyToJSON(pl));
                       } catch (SQLException ex1) {
                           
                       }catch (JsonProcessingException ex1) {
                    Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
                   }
                try {
                    System.out.println("signed out crashed player");
                    DBAccess.logout(player.getUsername());
                } catch (SQLException ex1) {
                    Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
                    sendAllPlayers();
                   playerHandlers.remove(this);
                   stop();
                   break;
               }
       }
    }
    
    void playAgain(String op)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.PLAY_AGAIN);
        String msg = "";
        try {
             msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!msg.isEmpty())
        {
            for(PlayerHandler playerHandler : playerHandlers)
            {
                if(playerHandler.player != null && playerHandler.player.getUsername().equals(op))
                {
                    playerHandler.printStream.println(msg);
                }
            }
        }
        
    }
    void setMoveToTheOpponent(String op,String move)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setMove(move);
        pl.setState(SocketRoute.PLAYER_MOVE);
        
        try {
            String msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
            for(PlayerHandler playerHandler : playerHandlers)
            {
                if(playerHandler.player != null && playerHandler.player.getUsername().equals(op))
                {
                    playerHandler.printStream.println(msg);
                }
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendAllPlayers()
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        String msg;
        try{
             try {
                pl.setPlayers(DBAccess.getAllPlayers());
                pl.setState(SocketRoute.ALL_PLAYERS);
            } catch (SQLException ex) {
                pl.setMessage("Couldn't get all players at the moment , please try again");
                pl.setState(SocketRoute.ERROR_OCCURED);
                Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
            } 
             msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
             for(PlayerHandler ph: playerHandlers){
                 if(ph != null)
                    ph.printStream.println(msg);
             }
        }catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    

    void checkLoginValidation(String userName,String password)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.LOG_IN_RESPONSE);
        boolean response;
        try {
            response = DBAccess.loginValidation(userName, password);
            if(response)
            {
                DBAccess.setActivityState(userName, true);
                player = DBAccess.getPlayerByUsername(userName);
                pl.setUsername(player.getUsername());
                pl.setPassword(player.getPassword());
                pl.setScore(player.getScore());
                pl.setIsActive(player.isIsActive());
                pl.setIsPlaying(player.isIsPlaying());
                sendAllPlayers();
            }
             pl.setResponse(response);
            String msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
            
            printStream.println(msg);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
      void checkSignupValidation(String userName,String password)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.SIGN_UP_RESPONSE);
        boolean response;
        try {
            response = DBAccess.signupValidation(userName);
            if(!response)
            {
                Player newPlayer = new Player(userName,password,0,false,false);
                DBAccess.insertPlayer(newPlayer);  
                sendAllPlayers();
            }  
            pl.setResponse(!response);
            String msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
            printStream.println(msg);
        } catch (SQLException | JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendRequestToOppenent(String name)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.REQUEST_TO_PLAY);
        pl.setOpponentName(player.getUsername());
        String msg = "";
        try {
             msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!msg.isEmpty())
        {
            for(PlayerHandler playerHandler : playerHandlers)
            {
                if(playerHandler.player != null && playerHandler.player.getUsername().equals(name))
                {
                    playerHandler.printStream.println(msg);
                }
            }
        }
    }
    
    void respondToRequestToPlay(String name,boolean response,boolean symbol)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.RESPONSE_TO_REQUEST_TO_PLAY);
        pl.setOpponentName(player.getUsername());
        pl.setResponse(response);
        pl.setPlayerSymbol(symbol);
        
        String msg = "";
        try {
             msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!msg.isEmpty())
        {
            for(PlayerHandler playerHandler : playerHandlers)
            {
                if(playerHandler.player.getUsername().equals(name))
                {
                    if(response)
                    {
                        playerHandler.opponent = this;
                    }
                    playerHandler.printStream.println(msg);
                }
            }
        }
    }
    
    
    void sendScoreBoard()
    {
        ArrayList<ScoreBoardItem> scoreList = new ArrayList<>();
        ArrayList<Player> playersList = new ArrayList<>();
        try {
            playersList = DBAccess.getAllPlayers();
        } catch (SQLException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(Player playerInList : playersList)
        {
            scoreList.add(new ScoreBoardItem(playerInList.getUsername(),playerInList.getScore()));
        }
        
        
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setState(SocketRoute.SCORE_BOARD);
        pl.setScoreBoardItem(scoreList);
        String msg = "";
        try {
             msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!scoreList.isEmpty())
        {
            printStream.println(msg);
        }

    }

}

