package isdp.guess_a_song.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import isdp.guess_a_song.utils.Constants;

/**
 * Question model as described in the UML.
 * <p>
 * Needs more work, but this will get us by for now.
 */

public class Question implements Parcelable {

    private ArrayList<Answer> answers = new ArrayList<Answer>();
    private HashMap<String, Integer> player_answers = new HashMap<String, Integer>();

    /* 1 is song, 2 is artist* (changed to 1,2 because, radio button gives
    * 1,2 by default in game creation set settings step) also let's use Constant type
    * Constants.GAME_TYPE_TITLE or Constants.GAME_TYPE_ARTIST
    *
    */
    private int type;
    private Song song;

    /*Constructors*/


    /*All parameters*/
    public Question(ArrayList<Answer> ans, int ty, Song son) {
        this.answers = ans;
        this.type = ty;
        this.song = son;
    }


    public boolean isNotAnswered(String uuid, int guess) {
        Log.d(Constants.LOGT, "isNotAnswered: " + uuid);

        if (player_answers.containsKey(uuid)) {
            return false;
        }
        player_answers.put(uuid, guess);
        return true;
    }


    /*Getters and setters*/
    public ArrayList<Answer> getAnswers() {
        return (ArrayList<Answer>) answers;
    }

    public Answer getAnswer(int index) {
        return answers.get(index);
    }

    public int getType() {
        return type;
    }

    public void setType(int newType) {
        this.type = newType;
    }

    public Song getSong() {
        return song;
    }


    /**
     * Parcelling part
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(answers);
        dest.writeValue(song);
        dest.writeInt(type);
    }

    public Question(Parcel in) {
        in.readTypedList(answers, Answer.CREATOR);
        this.song = (Song) in.readValue(Song.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    //end Parcelling

    @Override
    public String toString() {
        return song.toString();
    }

    public void shuffle() {
        Collections.shuffle(this.answers);
    }

    public HashMap<Integer, String> songsToPlayers() {
        HashMap<Integer, String> r = new HashMap<Integer, String>();
        r.put(0, answers.get(0).getText());
        r.put(1, answers.get(1).getText());
        r.put(2, answers.get(2).getText());
        r.put(3, answers.get(3).getText());
        return r;
    }

    public boolean isCorrect(int i) {
        Answer ans = answers.get(i);
        if (ans.isCorrect()) {
            return true;
        }
        return false;
    }

    public int getCorrectIndex() {
        for (int i = 0; i < 4; i++) {
            if (answers.get(i).isCorrect()) {
                return i;
            }
        }
        return -1;
    }
}
