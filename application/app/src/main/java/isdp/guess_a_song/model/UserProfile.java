package isdp.guess_a_song.model;

/**
 * Created on 16/10/2017, 6:11 PM
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Player model
 * Parcelable model
 * @Author Mindaugas Milius
 */

public class UserProfile implements Parcelable{
    private String name;

    public UserProfile(String name) {
        this.name = name;
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

}
