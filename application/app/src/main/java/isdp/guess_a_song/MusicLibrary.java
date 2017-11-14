package isdp.guess_a_song;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Song;

/**
 * Created on 10/6/2017, 18:18 PM
 */


/**
 * This activity shows current songs in Database
 * also allows to sync DB songs with storage
 *
 * @Author Mindaugas Milius
 */

public class MusicLibrary extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private List<Song> classMusicList = new ArrayList<>();
    private ArrayAdapter<Song> songsAdapter;
    private DatabaseHandler db;
    private ListView listView;
    private Button btSync;
    private Button btTest;
    private String tag = "MusicLibrary";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);
        db = new DatabaseHandler(this);
        //db.clearDatabase();


        /********************************************
         * Check and Grand storage access permission
         *******************************************/
        if (ContextCompat.checkSelfPermission(MusicLibrary.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicLibrary.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MusicLibrary.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MusicLibrary.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            doStuff();
        }


    }


    public void doStuff() {

        /********************************************
         * for testing only
         *******************************************/

        btTest = (Button) findViewById(R.id.btTest2);
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, " btTest2 clicked ");

                db.clearTable();
                dbSongsToView();

            }
        });


        /*
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getAdapter().getItem(position);
                MediaPlayer mp=new MediaPlayer();
                try{
                    mp.setDataSource(s.getPath());
                    mp.prepare();
                    mp.start();
                }catch(Exception e){e.printStackTrace();}


                Toast.makeText(MusicLibrary.this, "Playing song for 5 seconds", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
                mp.stop();

            }

        });
         */


        /********************************************
         * add buttons and listeners
         *******************************************/

        btSync = (Button) findViewById(R.id.btSync);
        btSync.setOnClickListener(btSyncListener);

        listView = (ListView) findViewById(R.id.listView);
        songsAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, classMusicList);
        //get songs from db
        dbSongsToView();
        listView.setAdapter(this.songsAdapter);

    }

    /**
     * Get music files from storage and parse it to
     * Song object List
     *
     * @return
     */
    public static List<Song> getStorageMusic(ContentResolver cr) {
        List<Song> found_songs = new ArrayList<Song>();
        Song curr;
        ContentResolver contentResolver =cr;
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songName = songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentSongName = songCursor.getString(songName);
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);

                curr = new Song(currentSongName, currentArtist, currentTitle, currentLocation, 1);
                found_songs.add(new Song(currentSongName, currentArtist, currentTitle, currentLocation, 1));
                //Log.d("getStorageMusic()", curr.toStringFull());

            } while (songCursor.moveToNext());
        }
        return found_songs;
    }


    /**
     * get all real songs from DB
     * & update list & notify adapter
     */
    public void dbSongsToView() {

        List<Song> all_songs = db.getAllSongs(1);
        classMusicList.clear();
        classMusicList.addAll(all_songs);
        songsAdapter.notifyDataSetChanged();


    }

    /**
     * Compare DB song list with storage list
     * and sync the DB
     */
    //TODO remove log messages
    public static List<Song> syncSongToDB(ContentResolver cr,DatabaseHandler db) {
        List<Song> fresh_list = getStorageMusic(cr);
        List<Song> songs_list = new ArrayList<>();


        List<Song> temp_to_del = new ArrayList<Song>(db.getAllSongs(1));
        List<Song> temp_to_add = new ArrayList<Song>(fresh_list);
//        Log.d(tag, "songs_list= " + songs_list.toString());
//        Log.d(tag, "fresh_list= " + fresh_list.toString());


        //remove from temporary list all active songs in storage
        temp_to_del.removeAll(fresh_list);
//        Log.d(tag, "temp_to_del(after removeAll)= " + temp_to_del.toString());
//        Log.d(tag, "songs_list= " + songs_list.toString());

        temp_to_add.removeAll(songs_list);
//        Log.d(tag, "temp_to_add(after removeAll)= " + temp_to_add.toString());
//
//        Log.d(tag, "For temp to del call ...");

        //delete from db all songs witch are not in storage
        for (final Song song : temp_to_del) {
            db.deleteSong(song);
        }
//        Log.d(tag, "For temp to add call ...");
        //add all songs from fresh list witch are not in songs_list
        for (final Song song : temp_to_add) {
            db.addSong(song);
        }
//        Log.d(tag, "DONE!");
        return  songs_list;
    }

    /**
     * Storage permission stuff
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (ContextCompat.checkSelfPermission(MusicLibrary.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    doStuff();
                } else {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    /**
     * Sunc button listener
     */
    private View.OnClickListener btSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            ContentResolver contentResolver = getContentResolver();
            classMusicList = syncSongToDB(contentResolver,db);
            dbSongsToView();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, " super.onResume();\n");

    }
}
