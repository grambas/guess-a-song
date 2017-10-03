package isdp.guess_a_song.model;

/**
 * Created on 10/2/2017.
 */

public class Song {
    int id;
    String original_name;
    String artist;
    String title;
    String path;
    int is_real;
    int played_count;



    /**
     * Song object model.
     *
     * @author Mindaugas Milius
     *
     */

    // Empty constructor
    public Song(){}

    /**
     * Constructor with all parameters
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

    // SETTER

    public void setID(int id){
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
        return artist + " - " + title;
    }
}