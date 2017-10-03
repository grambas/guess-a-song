package isdp.guess_a_song.db;

/**
 * Created on 10/2/2017.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.SongsImporter;

import static android.R.attr.name;


/**
 * Database manager. This class interacts with database.
 * Contents all CRUD methods
 * @author Mindaugas Milius
 *
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "guess-a-song";

    // Songs table name
    private static final String TABLE_SONGS = "songs";

    private Context _context;

    // Songs Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ORG_NAME = "original_name";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PATH = "path";
    private static final String KEY_IS_REAL = "is_real";
    private static final String KEY_PLAYED_COUNT = "played_count";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ORG_NAME + " TEXT,"
                + KEY_ARTIST + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_PATH + " TEXT,"
                + KEY_IS_REAL + " INTEGER,"
                + KEY_PLAYED_COUNT + " INTEGER"
                + ")";
        db.execSQL(CREATE_SONGS_TABLE);

        addFakeSongs(db);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Erases everything in the table.
     */
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        // Create tables again
        onCreate(db);

    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    /**
     *  Adding new song
     * @param s song
     * @return inserted record ID as long
     */
    public long addSong(Song s) {
        long inserted_id;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORG_NAME, s.getOriginalName());
        values.put(KEY_ARTIST, s.getArtist());
        values.put(KEY_TITLE, s.getTitle());
        values.put(KEY_PATH, s.getPath());
        values.put(KEY_IS_REAL, s.getIsRreal());
        values.put(KEY_PLAYED_COUNT, s.getPlayedCount());

        // Inserting Row
        inserted_id = db.insert(TABLE_SONGS, null, values);
        db.close(); // Closing database connection
        return inserted_id;
    }

    /**
     * Getting single song
     * @param id long
     * @return song object
     */
    public Song getSong(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SONGS, new String[] { KEY_ID,
                        KEY_ORG_NAME,KEY_ARTIST,KEY_TITLE,KEY_PATH,KEY_IS_REAL,KEY_PLAYED_COUNT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Song song = new Song(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)),
                Integer.parseInt(cursor.getString(6))
                );
        return song;
    }

    /**
     * Getting All Songs
     * @return songs in List
     */
    public List<Song> getAllSongs(int onlyReal) {
        List<Song> songsList = new ArrayList<Song>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery;

        // Select All Query
        if (onlyReal == 1){
            selectQuery = "SELECT * FROM "+TABLE_SONGS+" WHERE "+KEY_IS_REAL+" ="+1;
        }else{
            selectQuery = "SELECT  * FROM " + TABLE_SONGS;
        }
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setOriginalNname(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setTitle(cursor.getString(3));
                song.setPath(cursor.getString(4));
                song.setIsReal(Integer.parseInt(cursor.getString(5)));
                song.setPlayedCount(Integer.parseInt(cursor.getString(6)));

                // Adding song to list
                songsList.add(song);
            } while (cursor.moveToNext());
        }

        // return song list
        return songsList;
    }

    /**
     * Update song record in database
     * @param s song to update
     * @return 1 if success
     */
    public int updateSong(Song s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORG_NAME, s.getOriginalName());
        values.put(KEY_ARTIST, s.getArtist());
        values.put(KEY_TITLE, s.getTitle());
        values.put(KEY_PATH, s.getPath());
        values.put(KEY_IS_REAL, s.getIsRreal());
        values.put(KEY_PLAYED_COUNT, s.getPlayedCount());

        // updating row
        return db.update(TABLE_SONGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(s.getID()) });
    }

    /**
     * Delete song from database.
     * Method takes Song object and deletes it by ID
     * @param s Song to delete
     */
    public void deleteSong(Song s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGS, KEY_ID + " = ?",
                new String[] { String.valueOf(s.getID()) });
        db.close();
    }


    /**
     * Counts all song records
     * @return number of songs in database as int
     */
    public int getSongsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    /**
     * Adds some fake songs to database
     * uisng in "onCreate" method
     */
    public void addFakeSongs( SQLiteDatabase db ) {

        List<Song> songs = new SongsImporter(this._context).importSongs();
        ContentValues values = new ContentValues();
        values.put(KEY_ORG_NAME,"");
        values.put(KEY_PATH, "");
        values.put(KEY_IS_REAL, 0);
        values.put(KEY_PLAYED_COUNT, 0);

        for (final Song s : songs) {
            values.put(KEY_ARTIST, s.getArtist());
            values.put(KEY_TITLE, s.getTitle());
            db.insert(TABLE_SONGS, null, values);
        }
        values.put(KEY_ORG_NAME, "Pink - whtat about us");
        values.put(KEY_PATH, "/storage/sdcard0/Music/Pink - whtat about us.mp3");

        //Add one fake "real" song
        values.put(KEY_ORG_NAME, "Sam Smith - Too Good At Goodbyes");
        values.put(KEY_ARTIST, "Sam Smith");
        values.put(KEY_TITLE, "Too Good At Goodbyes");
        values.put(KEY_PATH, "/storage/sdcard0/Music/Sam Smith - Too good at goodbyes.mp3");
        values.put(KEY_IS_REAL, 1);
        values.put(KEY_PLAYED_COUNT, 0);
        db.insert(TABLE_SONGS, null, values);

        db.close();
    }




}