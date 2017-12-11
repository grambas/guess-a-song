package isdp.guess_a_song.model;

/**
 * Created on 10/30/2017, 4:56 PM
 */


/**
 * This is data holder for json messages
 * exchange. Converting JSON to Java class
 * with basic methods. Please add other attributes by needs.
 *
 * @Author Mindaugas Milius
 **/


public abstract class Action {
    String action;
    String publisher;
    String recipient;

    public Action(String action, String publisher, String recipient) {
        this.action = action;
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
                ", publisher='" + publisher + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}
