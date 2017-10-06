package isdp.guess_a_song.model;

import android.os.Parcel;
import android.os.Parcelable;

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
     * @param played_count
     */
    public Song(String original_name, String artist, String title, String path, int is_real, int played_count) {
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

    public Song(Parcel source) {
        this.id = source.readInt();
        this.original_name = source.readString();
        this.artist = source.readString();
        this.title = source.readString();
        this.path = source.readString();
        this.is_real = source.readInt();
        this.played_count = source.readInt();
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
        return original_name;
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

    public int getIsRreal() {
        return is_real;
    }

    public int getPlayedCount() {
        return played_count;
    }

    @Override
    public String toString() {
        return "Artist: " + artist + " Title: " + title;
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
}