package isdp.guess_a_song.model;

/**
 * Created on 16/10/2017, 6:11 PM
 * Updated on 11/9/2017
 */
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.JsonObject;
import android.content.Context;


import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
/**
 * Player model
 * Parcelable model
 * @Author Mindaugas Milius and Andrew Burns
 */

public class UserProfile implements Parcelable{
    private String name;
    private String uuid ;
    private boolean auth;
    private boolean isHost;
    private int score;

    public UserProfile(String name, String id,boolean auth,boolean isHost) {
        this.name = name;
        this.auth = auth;
        this.uuid = id;
        this.isHost = isHost;
        this.score = 0;
    }

    public UserProfile(String name) {
        this.name = name;
        this.auth = false;
        this.score= 0;
        this.uuid = java.util.UUID.randomUUID().toString();
    }
    public UserProfile() {
    }

    //Getters and setters
    public String getUuid() {
        return uuid;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int change) {
        this.score += change;
    }

    //other functions
    public void generateNewUUID(){
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    public void saveProfile(Context con){
        SharedPreferences pref = con.getApplicationContext().getSharedPreferences("GASpref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //prep key/value pair contents
        editor.putString("profileName", this.name);
        editor.putString("UUID", this.uuid);
        editor.putBoolean("auth", this.auth);
        editor.putBoolean("isHost", this.isHost);
        editor.putInt("score", this.score);

        //commit saved changes to SHaredPreferences
        editor.commit();
    }

    public void loadProfile(Context con){

        SharedPreferences pref = con.getApplicationContext().getSharedPreferences("GASpref", MODE_PRIVATE);

        //retrieve saved profile from SharedPreferences
        this.name = pref.getString("profileName", this.name);
        this.uuid = pref.getString("UUID", this.uuid);
        this.auth = pref.getBoolean("auth", this.auth);
        this.isHost = pref.getBoolean("isHost", this.isHost);
        this.score = pref.getInt("score", this.score);

        if(this.uuid == null){
            this.name = "GAS Player";
            this.auth = false;
            this.score= 0;
            this.uuid = java.util.UUID.randomUUID().toString();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                "uuid='" + uuid + '\'' +
                "auth='" + auth + '\'' +
                "isHost='" + isHost + '\'' +
                "score='" + score + '\'' +

                '}';
    }

    /**
     *  Parcelling part
     */

    public UserProfile(Parcel in){
        this.name = in.readString();
        this.uuid = in.readString();
        this.auth = in.readByte() != 0;     //myBoolean == true if byte != 0
        this.isHost = in.readByte() != 0;     //myBoolean == true if byte != 0
        this.score = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(this.uuid);
        dest.writeByte((byte) (this.auth ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeByte((byte) (this.isHost ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeInt(this.score);
    }
    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    //end Parcelling
    public JsonObject getState() {
        JsonObject state = new JsonObject();
        state.addProperty("is_auth", this.auth);
        state.addProperty("name", this.name);
        return state;
    }
}
