package tictactoeserver;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import resources.ResourcesLocation;

public class MainScreenBase extends AnchorPane {

    protected final ImageView imageView;
    protected final Button startButton;
    protected final Button stopButton;
    protected final Button exitButton;
    protected final Text text;
    private String ip;
    
    private final ServerController serverController;

    public MainScreenBase(Stage stage) {

        imageView = new ImageView();
        startButton = new Button();
        stopButton = new Button();
        exitButton = new Button();
        text = new Text();
        ip = "10.10.13.71";
        //change ip here
        serverController = new ServerController(ip, 5005);
        
        setId("AnchorPane");
        setPrefHeight(1000.0);
        setPrefWidth(1500.0);

        imageView.setFitHeight(1000.0);
        imageView.setFitWidth(1500.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(ResourcesLocation.class.getResource("images/backgrounds/main.jpg").toExternalForm()));

        startButton.setLayoutX(174.0);
        startButton.setLayoutY(176.0);
        startButton.setMnemonicParsing(false);
        startButton.setPrefHeight(77.0);
        startButton.setPrefWidth(287.0);
        startButton.getStylesheets().add("resources/css/application.css");
        startButton.setText("Start");
        startButton.setTextFill(javafx.scene.paint.Color.valueOf("#d9d9d9"));
        startButton.setFont(new Font("Agency FB Bold", 48.0));
        startButton.setOnAction((e) -> {
            serverController.startServer();
            stopButton.setDisable(false);
            startButton.setDisable(true);
        });

        stopButton.setLayoutX(174.0);
        stopButton.setLayoutY(362.0);
        stopButton.setMnemonicParsing(false);
        stopButton.setPrefHeight(77.0);
        stopButton.setPrefWidth(287.0);
        stopButton.getStylesheets().add("resources/css/application.css");
        stopButton.setText("Stop");
        stopButton.setTextFill(javafx.scene.paint.Color.valueOf("#d9d9d9"));
        stopButton.setFont(new Font("Agency FB Bold", 48.0));
        stopButton.setDisable(true);
        stopButton.setOnAction((e) -> {
            if(serverController.isSuspended()){
                serverController.resumeServer();
                stopButton.setText("Stop");
            }else{
                 serverController.suspendServer();
                 stopButton.setText("Resume");
            }
        });

        exitButton.setLayoutX(174.0);
        exitButton.setLayoutY(549.0);
        exitButton.setMnemonicParsing(false);
        exitButton.setPrefHeight(77.0);
        exitButton.setPrefWidth(287.0);
        exitButton.getStylesheets().add("resources/css/application.css");
        exitButton.setText("Exit");
        exitButton.setTextFill(javafx.scene.paint.Color.valueOf("#d9d9d9"));
        exitButton.setFont(new Font("Agency FB Bold", 48.0));
        exitButton.setOnAction((e) -> {
            serverController.stopServer();
            stage.close();
        });

        text.setFill(javafx.scene.paint.Color.valueOf("#d9d9d9"));
        text.setLayoutX(520.0);
        text.setLayoutY(113.0);
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText("Server IP: " + ip);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        text.setWrappingWidth(459.013671875);
        text.setFont(new Font("Agency FB Bold", 36.0));
       
        stage.setOnCloseRequest((e) -> {
            serverController.stopServer();
        });
        getChildren().add(imageView);
        getChildren().add(startButton);
        getChildren().add(stopButton);
        getChildren().add(exitButton);
        getChildren().add(text);
        
    }
}
