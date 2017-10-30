package isdp.guess_a_song.controller;

/**
 * Created on 10/30/2017, 4:56 PM
 */

public class Action {
    String action;
    String value;
    String publisher;
    String recipient;

    public Action(String action, String value, String publisher, String recipient) {
        this.action = action;
        this.value = value;
        this.publisher = publisher;
        this.recipient = recipient;
    }

    public String getAction() {
        return action;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Action{" +
                "action='" + action + '\'' +
                ", value='" + value + '\'' +
                ", publisher='" + publisher + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}
