package isdp.guess_a_song.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created on 10/5/2017, 11:01 PM
 */

/**
 * This is moodel Object of game settings.
 * This class can be send to other activities with Parcelable method
 *
 * @author Mindaugas Milius
 */


public class Settings implements Parcelable {
    private int guess_time;
    private int songs_amount;
    private int game_type;
    private int gameID;
    private int gamePIN;


    /**
     * Default constructor with default values
     */
    public Settings() {
        this.guess_time = 15;
        this.songs_amount = 10;
        this.game_type = 1;
        this.gameID = -1;
        this.gamePIN = -1;

    }

    /**
     * Constructor with all parameters
     *
     * @param guess_time
     * @param songs_amount
     * @param game_type
     */
    public Settings(int guess_time, int songs_amount, int game_type) {
        this.guess_time = guess_time;
        this.songs_amount = songs_amount;
        this.game_type = game_type;
        this.gameID = -1;
        this.gamePIN = -1;
    }

    /**
     * Getter & Setter
     */

    public int getGuess_time() {
        return guess_time;
    }

    public int getSongs_amount() {
        return songs_amount;
    }

    public int getGame_type() {
        return game_type;
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameIDString() {
        Log.d("mylogger", Integer.toString(gameID));
        return Integer.toString(gameID);
    }


    public int getGamePIN() {
        return gamePIN;
    }

    public void setGuess_time(int guess_time) {
        this.guess_time = guess_time;
    }

    public void setSongs_amount(int songs_amount) {
        this.songs_amount = songs_amount;
    }

    public void setGame_type(int game_type) {
        this.game_type = game_type;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setGamePIN(int gamePIN) {
        this.gamePIN = gamePIN;
    }

    /**
     * Parcelling part
     */

    public Settings(Parcel in) {
        this.guess_time = in.readInt();
        this.songs_amount = in.readInt();
        this.game_type = in.readInt();
        this.gameID = in.readInt();
        this.gamePIN = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.guess_time);
        dest.writeInt(this.songs_amount);
        dest.writeInt(this.game_type);
        dest.writeInt(this.gameID);
        dest.writeInt(this.gamePIN);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
    //end Parcelling

    @Override
    public String toString() {
        return "Settings{" +
                "guess_time='" + guess_time + '\'' +
                ", songs_amount='" + songs_amount + '\'' +
                ", game_type='" + game_type + '\'' +
                ", gameID='" + gameID + '\'' +
                ", gamePIN='" + gamePIN + '\'' +
                '}';
    }


}
