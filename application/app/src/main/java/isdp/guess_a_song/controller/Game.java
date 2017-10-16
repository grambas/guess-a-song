package isdp.guess_a_song.controller;


import java.util.List;

import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Score;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.utils.Constants;

/**
 * Created on 10/6/2017, 12:04 PM
 */

/**
 * Main game controller and instance
 * this class should be called in game creation last step
 * @author Mindaugas Milius
 */

public class Game {

    //Static final attributes
    private int ID;
    private int PIN;

    private static final Game instance = new Game();

    // Game attributes
    Settings settings;
    List<Question> Questions;
    // List<Player> players;
    List<Score> scoreList;
    int status;

    public static Game getInstance() {
        return instance;
    }

    private Game() {
        status = Constants.GAME_STATUS_STARTED;
    }

    /**
     * Methdod to start a game after all game
     * creation steps are done and all players joined
     * into the room
     */
    public void start(){
        status  = Constants.GAME_STATUS_STARTED;
    }
    public void next_q(){
        //start timer e.t.c...
    }
    public void pause(){
        status  = Constants.GAME_STATUS_PAUSE;
    }
    public String showScore(){
        String scoreString = "";
        for (Score sc:scoreList) {
            scoreString += (sc.toString() + "\n");
        }

        return scoreString;
    }
    public void end(){
        status  = Constants.GAME_STATUS_FINISHED;
    }

    public  int getID() {
        return ID;
    }

    public  int getPIN() {
        return PIN;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }
}
