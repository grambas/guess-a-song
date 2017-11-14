package isdp.guess_a_song.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 10/9/2017, 2:52 PM
 */

/**
 * This class holds answer text ant boolean
 * if answer ir correct one.
 *  This class is Parcelable ready
 *
 * @Author Mindaugas Milius
 */

public class Answer  implements Parcelable {
    private String text;
    private boolean correct;


    public Answer(String text,boolean correct) {
        this.correct = correct;
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "text='" + text + '\'' +
                ", correct=" + correct +
                '}';
    }

    /**
     *  Parcelling part
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(correct ? 1 : 0);
    }
    public Answer(Parcel in){
        this.text = in.readString();
        this.correct  = (in.readInt() == 0) ? false : true;

    }
    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
    //end Parcelling

}
