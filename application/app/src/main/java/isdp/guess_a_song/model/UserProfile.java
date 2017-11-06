package isdp.guess_a_song.model;

/**
 * Created on 16/10/2017, 6:11 PM
 */
import android.provider.Settings.Secure;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.util.UUID;

/**
 * Player model
 * Parcelable model
 * @Author Mindaugas Milius
 */

public class UserProfile implements Parcelable{
    private String name;
    private String uuid ;
    private boolean auth;
    private boolean isHost;
    int score;

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
    }
    public UserProfile() {
        this.name = null;
        this.auth = false;
        this.score= 0;
    }

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

    @Override
    public int describeContents() {
        return 0;
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
