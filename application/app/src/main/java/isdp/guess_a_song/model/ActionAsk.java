package isdp.guess_a_song.model;

import java.util.HashMap;

/**
 * Created on 11/6/2017, 4:39 PM
 */

public class ActionAsk extends Action {

    private HashMap<Integer, String> question;
    int qIndex;
    int correct;

    public ActionAsk(String action, String publisher, String recipient, HashMap<Integer, String> question,int correct,int qIndex) {
        super(action, publisher, recipient);
        this.question = question;
        this.qIndex = qIndex;
        this.correct = correct;
    }

    public HashMap<Integer, String> getQuestion() {
        return question;
    }
    public int getQIndex() {
        return qIndex;
    }
    public int getQCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return "Action{" +
                "action='" + action + '\'' +
                ", publisher='" + publisher + '\'' +
                ", recipient='" + recipient + '\'' +
                ", correct='" + correct + '\'' +
                ", qIndex='" + qIndex + '\'' +
                ", question='" + question.toString() + '\'' +
                '}';
    }
}
