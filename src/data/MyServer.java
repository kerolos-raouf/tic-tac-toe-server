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
import java.sql.SQLException;
import java.util.Vector;
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
    Socket socket;
    BufferedReader bufferedReader;
    PrintStream printStream;
    PlayerHandler opponent;
    Player player;
    
    static Vector<PlayerHandler> playerHandlers;

    
    static{
        playerHandlers = new Vector();
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
       while(true){
            try {
                String str = bufferedReader.readLine();
                PlayerMessageBody pl = new PlayerMessageBody();
                switch(pl.getState())
                {
                    case PLAYER_MOVE:
                    {
                        setMoveToTheOpponent(pl.getMove());
                        break;
                    }
                    case LOG_IN:
                    {
                        break;
                    }
                    case LOG_IN_RESPONSE:
                        break;
                    case SIGN_UP:
                    {
                        break;
                    }
                    case SIGN_UP_RESPONSE:
                        break;
                    case AVAILABLE_PLAYERS:
                        break;
                    case LOG_OUT:
                        break;
                    case SURRENDER:
                        break;
                    case CHECK_SERVER:
                        break;
                    case ALL_PLAYERS:
                        break;
                    case REQUEST_TO_PLAY:
                        break;
                    case RESPONSE_TO_REQUEST_TO_PLAY:
                        break;
                    case DIALOG_REQUEST_TO_PLAY:
                        break;
                    case WAITING_REQUEST_TO_PLAY:
                        break;
                    case SCORE_BOARD:
                        break;
                    default:
                        throw new AssertionError(pl.getState().name());
                }
                System.out.println(str);
               } catch (IOException ex) {
                   System.out.println("connection reset exception message is expected, just added this line to check if another exception message is thrown");
                   Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
                   stop();
                   playerHandlers.remove(this);
                   break;
               }
       }
    }
    
    
    void setMoveToTheOpponent(String move)
    {
        PlayerMessageBody pl = new PlayerMessageBody();
        pl.setMove(move);
        pl.setState(SocketRoute.PLAYER_MOVE);
        try {
            String msg = JSONParser.convertFromPlayerMessageBodyToJSON(pl);
            opponent.printStream.println(msg);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
}

