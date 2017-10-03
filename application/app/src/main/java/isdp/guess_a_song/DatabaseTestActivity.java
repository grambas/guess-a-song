package isdp.guess_a_song;

/**
 * Created  on 10/2/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Song;

/**
 * This class is only for Database testing purpose
 *
 * @author Mindaugas Milius
 */

public class DatabaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);


        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * DEMO USING DATABASE
         */
        // Inserting Songs
        Log.d("Insert: ", "Insert test ..");
        Song fake_song = new Song("R.Williams - Here I am", "R.Williams","Here I am2","/home/media/songs/r.williams-here i am.mp3",0,0);
        long fake_song_id =  db.addSong(fake_song);

        // Reading all songs
        Log.d("Reading: ", "Reading all songs..");
        List<Song> songs = db.getAllSongs(1);
        for (Song s : songs) {
            String log = "Id: "+s.getID()+" ,Original Name: " + s.getOriginalName();
            Log.d("Name: ", log);
        }

        // Get song
        Log.d("Getting: ", "song from ID..");
        Song song_from_db = db.getSong(fake_song_id);
        String log = "Id: "+song_from_db.getID()+" ,Original Name: " + song_from_db.getOriginalName();

        //Update Song
        song_from_db.setOriginalNname("One Direction - Best Song Ever.mp3");
        db.updateSong(song_from_db);


        // Reading all songs again
        Log.d("Reading: ", "Reading all songs again..");
        songs = db.getAllSongs(1);
        for (Song s : songs) {
            log = "Id: "+s.getID()+" ,Original Name: " + s.getOriginalName();
            Log.d("Name: ", log);
        }

        // Delete  Song
        Log.d("song_from_db: ", log);
        Log.d("Deleting: ", "song from ID..");
        db.deleteSong(song_from_db);
    }

}
