package isdp.guess_a_song.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Score class to keep track of an individual player's score.
 *
 * Created by Andy on 10/27/2017.
 */

public class Score implements Parcelable {

    private int playerID;
    private int points;

    //Constructors
    public Score(int PID, int startPoints){
        this.playerID = PID;
        this.points = startPoints;
    }

    public Score(int PID){
        this.playerID = PID;
        this.points = 0;
    }

    //Getters and Setters
    public int getScore(){
        return points;
    }

    //Works for positive or negative numbers
    public void changeScore(int change){
        points += change;
    }

    public int getPlayerID(){
        return playerID;
    }

    //No method to set playerID since that shouldn't change once an instance is created


    //Parcelling

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(playerID);
        dest.writeInt(points);
    }

    public Score(Parcel in){
        this.points = in.readInt();
        this.playerID = in.readInt();
    }

    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
    //end Parcelling

    public String toString(){ return ("Player ID: " + playerID + " Score: " + points);}

}
