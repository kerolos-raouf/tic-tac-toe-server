/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Kerolos Raouf
 */
public class ScoreBoardItem {
    String player;
    int score;

    public ScoreBoardItem(String player, int score) {
        this.player = player;
        this.score = score;
    }

    
    
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    
}
