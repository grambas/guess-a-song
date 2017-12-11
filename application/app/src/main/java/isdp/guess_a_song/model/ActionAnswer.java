package isdp.guess_a_song.model;

/**
 * Created on 11/6/2017, 10:23 PM
 */

public class ActionAnswer extends Action {

    private String uuid;
    private int questionIndex;
    private int answerIndex;

    public ActionAnswer(String action, String publisher, String recipient, String uuid, int questionIndex, int answerIndex) {
        super(action, publisher, recipient);
        this.uuid = uuid;
        this.questionIndex = questionIndex;
        this.answerIndex = answerIndex;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }
}
