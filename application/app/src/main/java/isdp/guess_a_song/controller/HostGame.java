package isdp.guess_a_song.controller;


import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.locks.Condition;

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

public class HostGame extends Observable {

    //Static final attributes
    private static final HostGame instance = new HostGame();

    Settings settings;
    List<Question> questions;
    HashMap<String, UserProfile> players;
    HashMap<Integer, Set<String>> already_answered;

    //Score score

    int status;
    int currentIndex;
    int ans_amount;

    public static HostGame getInstance() {
        return instance;
    }

    private HostGame() {
        status = Constants.GAME_STATUS_STARTED;
        currentIndex = 0;
       // scoreMap = null;
        players = new HashMap<String,UserProfile>();

        questions = null;
        settings = null;
        ans_amount = 0;
    }

    public int getAns_amount() {
        return ans_amount;
    }

    public void setAns_amount(int ans_amount) {
        this.ans_amount = ans_amount;
        setChanged();
        notifyObservers();
    }


    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    /**
     * Methdod to start a game after all game
     * creation steps are done and all players joined
     * into the room
     */
    public void start(){
        status  = Constants.GAME_STATUS_STARTED;
    }

    public boolean next_q(){
        ans_amount = 0;
        if (currentIndex == questions.size()-1 ) {
            return false;
        }
        currentIndex++;
        setChanged();
        notifyObservers();
        return true;
    }
    public void pause(){
        status  = Constants.GAME_STATUS_PAUSE;
    }

    public Question getCurrentQuestion(){
        return questions.get(currentIndex);
    }

    public int getCurrentIndex(){
        return currentIndex;
    }
    public String showScore(){
        String scoreString = "Score: \n";
        //TODO add sorting
        for (UserProfile value : players.values()) {
            scoreString += (value.getName() +" : " + value.getScore()+ "\n");
        }
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
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public  HashMap<String, UserProfile> getPlayers() {
        return players;
    }

    public void setPlayers( List<UserProfile> players) {
        for (UserProfile value : players) {
            Log.d(Constants.LOGT, value.toString());
            this.players.put(value.getUuid(),value);
        }
    }
    public boolean processAnswer(String player,int guess,int guess_index){
        boolean result = true;
        Log.d(Constants.LOGT, "processAnswer: "+ player);
        if(currentIndex == guess_index && players.containsKey(player)){
            if(getCurrentQuestion().isNotAnswered(player,guess)){
                if( getCurrentQuestion().isCorrect(guess)){
                    players.get(player).addScore(Constants.REWARD_CORRECT);
                }else{
                    //decrement?
                    players.get(player).addScore(Constants.REWARD_WRONG);
                }
                ans_amount++;
                setChanged();
                notifyObservers();
            }

        }else{
           result = false;
        }
    return result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        Log.d(Constants.LOGT, "Game Status changed from "+ this.status + " to "+ status);
        this.status = status;
        setChanged();
        notifyObservers();
    }

    public String getHumanStatus(){
        if (status == Constants.GAME_STATUS_STARTED){
            return "Started";
        }else  if(status == Constants.GAME_STATUS_FINISHED){
            return "Finished";
        }else  if(status == Constants.GAME_STATUS_ON_QUESTION){
            return "On question";
        }else  if(status == Constants.GAME_STATUS_PAUSE){
            return "Paused";
        }else  if(status == Constants.GAME_STATUS_TIME_OVER){
            return "Time over";
        }else  if(status == Constants.GAME_STATUS_READY){
            return "Ready";
        }else{
            return "not defined";
        }
    }
    @Override
    public String toString() {
        return "HostGame{" +
                ", settings=" + settings +
                ", questions=" + questions +
                ", players=" + players +
                ", status=" + status +
                '}';
    }

}
