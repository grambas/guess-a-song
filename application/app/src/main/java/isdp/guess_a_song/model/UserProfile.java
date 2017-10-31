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

    public UserProfile(String name, String id,boolean auth,boolean isHost) {
        this.name = name;
        this.auth = auth;
        this.uuid = id;
    }

    public UserProfile(String name, boolean auth) {
        this.name = name;
        this.auth = auth;
    }
    public UserProfile(String name) {
        this.name = name;
        this.auth = false;
    }
    public UserProfile() {
        this.name = null;
        this.auth = false;
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

    public static Creator getCREATOR() {
        return CREATOR;
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
                '}';
    }


    /**
     *  Parcelling part
     */

    public UserProfile(Parcel in){
        this.name = in.readString();
        //this.auth = in.read
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
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
