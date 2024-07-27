/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import DB.DBAccess;
import static DB.DBAccess.initiateDbConnection;
import DB.Player;
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
    static Vector<PlayerHandler> playerHandlers;

    
    static{
        playerHandlers = new Vector();
    }
  
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
                    playerHandlers.add(new PlayerHandler(createdSocket));
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
    Player opponent;
    Player player;
    
    public PlayerHandler(Socket playerSocket)
    {
        try {
            this.socket = playerSocket;
            bufferedReader = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());
            player = opponent = null;
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
                System.out.println(str);
               } catch (IOException ex) {
                   System.out.println("connection reset exception message is expected, just added this line to check if another exception message is thrown");
                   Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
                   stop();
                   MyServer.playerHandlers.remove(this);
                   break;
               }
       }
    }
}

