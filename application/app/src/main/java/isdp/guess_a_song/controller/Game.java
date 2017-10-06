package isdp.guess_a_song.controller;


import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

/**
 * Created on 10/6/2017, 12:04 PM
 */

/**
 * Main game controller and instance
 * this class should be called in game creation last step
 */

public class Game {

    //Static final attributes
    private static final int ID = generateID();
    private static final int KEY = generatePassword();

    public static final int RANDOM_MIN = 1000;
    public static final int RANDOM_MAX = 100000;


    private static final Game instance = new Game();

    // Game attributes
    Settings settings;
    // List<Question> Questions;
    // List<Player> players;
    // Score score
    int status;

    public static Game getInstance() {
        return instance;
    }

    private Game() {
        status = Constants.GAME_STATUS_INIT;
    }

    /**
     * Methdo to start a game after all game
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
    }
    public void showScore(){
    }
    public void end(){
    }

    private static int generateID(){
        return Helpers.randomInt(RANDOM_MIN,RANDOM_MAX);
    }
    private static int generatePassword(){
        return Helpers.randomInt(RANDOM_MIN,RANDOM_MAX);
    }

    public static int getID() {
        return ID;
    }

    public static int getKEY() {
        return KEY;
    }
}
