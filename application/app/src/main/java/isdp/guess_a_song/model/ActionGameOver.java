package isdp.guess_a_song.model;

import java.util.ArrayList;

/**
 * Created by Maxi on 10.12.2017.
 */

public class ActionGameOver extends Action {

    private ArrayList<String> scores;


    public ActionGameOver(String action, String publisher, String recipient, ArrayList<String> scores) {
        super(action, publisher, recipient);
        this.scores = scores;
    }

    public ArrayList<String> getScores() {
        return scores;
    }

    public void setScores(ArrayList<String> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "ActionGameOver{" +
                "scores=" + scores +
                '}';
    }
}
