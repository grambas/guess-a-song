package isdp.guess_a_song.model;

import java.util.ArrayList;
import java.util.Collections;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Question model as described in the UML.
 *
 * Needs more work, but this will get us by for now.
 */

public class Question implements Parcelable {

    private ArrayList<Answer> answers = new ArrayList<Answer>();

    /* 1 is song, 2 is artist* (changed to 1,2 because, radio button gives
    * 1,2 by default in game creation set settings step) also let's use Constant type
    * Constants.GAME_TYPE_TITLE or Constants.GAME_TYPE_ARTIST
    *
    */
    private int type;
    private Song song;

    /*Constructors*/


    /*All parameters*/
    public Question(ArrayList<Answer> ans, int ty, Song son){
        this.answers = ans;
        this.type = ty;
        this.song = son;
    }

    /*Just the song. Auto builds the question*/
    /*Right now, doesn't generate proper random wrong answers. Will add functionality later.*/
    public Question(Song son,int ty){

        answers = new ArrayList<Answer>();
        this.answers.add( new Answer("Wrong Answer",false));
        this.answers.add( new Answer("Wrong Answer",false));
        this.answers.add( new Answer("Wrong Answer",false));
        this.answers.add( new Answer(son.getTitle(),true));


        this.type = ty;
        this.song = son;
    }


    /*Getters and setters*/
    public ArrayList<Answer> getAnswers(){return (ArrayList<Answer>) answers;}

    public Answer getAnswer(int index){return answers.get(index);}

    public void setAnswer(int index, Answer newAnswer){this.answers.set(index, newAnswer);}

    public int getType(){return type;}

    public void setType(int newType){this.type = newType;}

    public Song getSong(){return song;}


    /**
     *  Parcelling part
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
    public Question(Parcel in){
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

    public String toStringFull() {

        return "Question{" +
                "song=" + song.getOriginalName() +
                "q0=" + answers.get(0).getText() +
                "q1=" + answers.get(1).getText() +
                "q2=" + answers.get(2).getText() +
                "q3=" + answers.get(3).getText() +

                '}';
    }
    @Override
    public String toString() {
        return song.toString();
    }

    public void shuffle(){
        Collections.shuffle(this.answers);
    }

    public boolean isCorrect(int i){

        Answer ans = answers.get(i);
        if ( ans.isCorrect() )
        {
            return true;
        }
       return false;
    }
}
