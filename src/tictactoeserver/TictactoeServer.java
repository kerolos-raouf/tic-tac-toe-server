/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import data.MyServer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author emyal
 */
public class TictactoeServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new MainScreenBase(stage);
        
        Scene scene = new Scene(root, 1500, 1000);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if(MyServer.isInitialized())
            MyServer.getInstance().close();
        super.stop();
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}