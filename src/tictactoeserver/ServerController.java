/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import data.MyServer;
import java.io.IOException;
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
        try {
            server = MyServer.initialize(serverIp, port);
            started = false;
            suspended = false;
        } catch (IllegalArgumentException ex) {
            new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
             new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        }
    }
    
     public ServerController(String serverIp)
    {
        try {
            server = MyServer.initialize(serverIp);
            System.out.println(server.getPort());
            System.out.println(server.getAddr());
        } catch (IllegalArgumentException ex) {
            new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
             new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        }
    }
     
     void startServer(){
         server.start();
         started = true;
     }
     
     void suspendServer(){
         server.suspend();
         suspended = true;
     }
     
     void resumeServer(){
         server.resume();
         suspended = false;
     }
    
     void stopServer(){
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            new CustomDialogBase(ex.getMessage(), "ok", null, null , null);
        }
     }
}
