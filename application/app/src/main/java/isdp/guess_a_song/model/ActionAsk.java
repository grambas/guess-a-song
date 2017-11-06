package isdp.guess_a_song.model;

import java.util.HashMap;

/**
 * Created on 11/6/2017, 4:39 PM
 */

public class ActionAsk extends Action {

    private HashMap<Integer, String> question;

    public ActionAsk(String action, String publisher, String recipient, HashMap<Integer, String> question) {
        super(action, publisher, recipient);
        this.question = question;
    }


}
