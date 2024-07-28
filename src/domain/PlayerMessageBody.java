/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import DB.Player;
import java.util.ArrayList;

/**
 *
 * @author Kerolos Raouf
 */
public class PlayerMessageBody extends Player{
    
    private SocketRoute state;
    private boolean response;
    private String message;
    private String move;
    private ArrayList<ScoreBoardItem> scoreBoardItem;
    private ArrayList<Player> players;



    
        public ArrayList<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(ArrayList<Player> players)
    {
        this.players = players;
    }

       public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public SocketRoute getState() {
        return state;
    }

    public void setState(SocketRoute state) {
        this.state = state;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }


    public ArrayList<ScoreBoardItem> getScoreBoardItem() {
        return scoreBoardItem;
    }

    public void setScoreBoardItem(ArrayList<ScoreBoardItem> scoreBoardItem) {
        this.scoreBoardItem = scoreBoardItem;
    }

    public boolean getResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
    
    
    public PlayerMessageBody()
    {
        super();
    }
    
}
