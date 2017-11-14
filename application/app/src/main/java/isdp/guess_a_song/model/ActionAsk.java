package isdp.guess_a_song.model;

import java.util.HashMap;

/**
 * Created on 11/6/2017, 4:39 PM
 */

public class ActionAsk extends Action {

    private HashMap<Integer, String> question;
    int qIndex;

    public ActionAsk(String action, String publisher, String recipient, HashMap<Integer, String> question,int qIndex) {
        super(action, publisher, recipient);
        this.question = question;
        this.qIndex = qIndex;
    }

    public HashMap<Integer, String> getQuestion() {
        return question;
    }

    public int getqIndex() {
        return qIndex;
    }
}
