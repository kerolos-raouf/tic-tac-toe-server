/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import data.MyServer;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTimeoutException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.CustomDialogBase;


/**
 *
 * @author emyal
 */
public class ServerController{
    
    private MyServer server;
    private boolean started;
    private boolean suspended;
    private String ip;
    private int port;
    static MainScreenBase mainScreenBase;

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getIp(){
        return ip;
    }

    public boolean isSuspended()
    {
        return suspended;
    }

    public void setSuspended(boolean suspended)
    {
        this.suspended = suspended;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }

    public boolean isStarted()
    {
        return started;
    }
    

    public ServerController(String serverIp, int port)
    {
        ip = getLocalNetworkAddress();
        ip = ip != null ? ip : serverIp;
        server = null;
        this.port = port;
        started = false;
        suspended = false;
    }
    
    private String getLocalNetworkAddress(){
        String ip = null;
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()){
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while(addresses.hasMoreElements()){
                    InetAddress address = addresses.nextElement();
                    if(!address.isLoopbackAddress() && address.isSiteLocalAddress()){
                        ip = address.getHostAddress();
                        System.out.println("IP: " + ip);
                    }
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
            System.err.println("failed to get ip address");
        }
        return ip;
       }
    

    
     public ServerController(String serverIp)
    {
        ip = getLocalNetworkAddress();
        ip = ip != null ? ip : serverIp;
        server = null;
        port = -1;
        started = false;
        suspended = false;
    }
     
     boolean startServer(){
        try {
            server = (port < 1) ? MyServer.initialize(ip) : MyServer.initialize(ip, port);
            System.out.println(server.getPort());
            System.out.println(server.getAddr());
            server.start();
            started = true;
            mainScreenBase.stopButton.setDisable(!isStarted());
            mainScreenBase.startButton.setDisable(isStarted());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch(SQLNonTransientConnectionException ex){
            started = false;
            new CustomDialogBase("Couldn't connect to the Database", "Retry", "Cancel", () -> {
            startServer();
            }, null);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            started = false;
            new CustomDialogBase(ex.getMessage(), "Retry", "Cancel", () -> {
            startServer();
            }, null);
        }
        return started;
     }
     
     void suspendServer(){
         server.pause();
         suspended = true;
     }
     
     void resumeServer(){
         server.carryOn();
         suspended = false;
     }
    
     void stopServer(){
        try {
            if(server != null) server.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        }
     }
}
