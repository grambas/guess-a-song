package isdp.guess_a_song.controller;


import java.util.List;

import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Score;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;

/**
 * Created on 10/6/2017, 12:04 PM
 */

/**
 * Main game controller and instance
 * this class should be called in game creation last step
 */

public class Game {

    //Static final attributes
    private static final Game instance = new Game();

    // Game attributes
    Settings settings;
    List<Question> Questions;
<<<<<<< HEAD
    // List<Player> players;
    List<Score> scoreList;
=======
    List<UserProfile> players;
<<<<<<< HEAD
    //Score score

    public List<Question> getQuestions() {
        return Questions;
    }

    public void setQuestions(List<Question> questions) {
        Questions = questions;
    }

    public List<UserProfile> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserProfile> players) {
        this.players = players;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

>>>>>>> some changes on laptop sync
=======
    List<Score> scoreList;
>>>>>>> merging master with mindau branch
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
<<<<<<< HEAD
=======

>>>>>>> merging master with mindau branch
    public String showScore(){
        String scoreString = "";
        for (Score sc:scoreList) {
            scoreString += (sc.toString() + "\n");
        }
<<<<<<< HEAD

=======
>>>>>>> merging master with mindau branch
        return scoreString;
    }

    public void end(){
        status  = Constants.GAME_STATUS_FINISHED;
    }


    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<Question> getQuestions() {
        return Questions;
    }

    public void setQuestions(List<Question> questions) {
        Questions = questions;
    }

    public List<UserProfile> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserProfile> players) {
        this.players = players;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Game{" +
                ", settings=" + settings +
                ", Questions=" + Questions +
                ", players=" + players +
                ", status=" + status +
                '}';
    }

}
