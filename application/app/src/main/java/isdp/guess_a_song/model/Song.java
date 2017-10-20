package isdp.guess_a_song.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.util.Log;

import isdp.guess_a_song.utils.Constants;

//TODO need to check constructors due to calling this object in SelectSongs view

/**
 * Created on 10/2/2017.
 */


/**
 * Song object model.
 * This class can be send to other Activities with Parcelable method
 *
 * @author Mindaugas Milius
 */
public class Song implements Parcelable {
    int id;
    String original_name;
    String artist;
    String title;
    String path;
    int is_real;
    int played_count;



    // Empty constructor
    public Song() {
    }

    /**
     * Constructor with all parameters
     *
     * @param id
     * @param original_name
     * @param artist
     * @param title
     * @param path
     * @param is_real
     * @param played_count
     */
    public Song(int id, String original_name, String artist, String title, String path, int is_real, int played_count) {
        this.id = id;
        this.original_name = original_name;
        this.artist = artist;
        this.title = title;
        this.path = path;
        this.is_real = is_real;
        this.played_count = played_count;
    }

    /**
     * Constructor without ID parameter
     *
     * @param original_name
     * @param artist
     * @param title
     * @param path
     * @param is_real
     */
    public Song(String original_name, String artist, String title, String path, int is_real) {
        this.original_name = original_name;
        this.artist = artist;
        this.title = title;
        this.path = path;
        this.is_real = is_real;
        this.played_count = played_count;
    }

    /**
     * Constructor for fake songs
     *
     * @param artist
     * @param title
     */
    public Song(String artist, String title) {
        this.artist = artist;
        this.title = title;
        this.is_real = 0;
    }


    // SETTER

    public void setID(int id) {
        this.id = id;
    }

    public void setOriginalNname(String _original_name) {
        this.original_name = _original_name;
    }

    public void setArtist(String _artist) {
        this.artist = _artist;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public void setPath(String _path) {
        this.path = _path;
    }

    public void setIsReal(int _is_real) {
        this.is_real = _is_real;
    }

    public void setPlayedCount(int played_count) {
        this.played_count = played_count;
    }

    //GETTER

    public int getID() {
        return id;
    }

    public String getOriginalName() {
        if(original_name != null && !original_name.isEmpty()){
            return original_name;
        }
        return "No name";

    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public int getIsRreal() { return is_real;  }

    public int getPlayedCount() {
        return played_count;
    }

    public String songToAnswer(int type){
        if( type == Constants.GAME_TYPE_TITLE){
            //Log.d("Song.java", "asked title. title= "+this.title);
            return this.title;
        }else if (type == Constants.GAME_TYPE_ARTIST){
            //Log.d("Song.java", "asked artist. artist= "+this.artist);
            return this.artist;
        }
        else return getOriginalName();
    }

    public String toStringFull() {
        return "Song{" +
                "id='" + id + '\'' +
                ", original_name='" + original_name + '\'' +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", is_real='" + is_real + '\'' +
                ", played_count='" + played_count + '\'' +
                '}';
    }
    @Override
    public String toString() {
        return  artist + " - " + title;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(original_name);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeInt(is_real);
        dest.writeInt(played_count);

    }
    public Song(Parcel source) {
        this.id = source.readInt();
        this.original_name = source.readString();
        this.artist = source.readString();
        this.title = source.readString();
        this.path = source.readString();
        this.is_real = source.readInt();
        this.played_count = source.readInt();
    }
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {

        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;

        Song song = (Song) o;

        return path.equals(song.path);

    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}