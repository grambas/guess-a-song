package isdp.guess_a_song.model;


/**
 * Created on 11/6/2017, 4:52 PM
 */

public class ActionSimple extends Action {
    String value;
    public ActionSimple(String action, String value, String publisher, String recipient) {
        super(action, publisher, recipient);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
