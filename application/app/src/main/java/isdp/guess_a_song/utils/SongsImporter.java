package isdp.guess_a_song.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.model.Song;

/**
 * Created on 10/3/2017, 9:34 PM
 */

/**
 * This class reads text file with song names and returns
 * Song objects with parsed Artist and Title fields.
 * File foramt: [artist];[title]
 * Usage:
 *  SongsImporter importer = new SongsImporter(getApplicationContext());
 *  List<Song> songs_list = importer.importSongs();
 *
 * @Author Mindaugas Milius
 */

public class SongsImporter {
    public static final String path = "fake_songs.txt";
    private List<Song> songs;
    private Context _context;

    public SongsImporter(Context context) {
        this._context = context;
        this.songs = new ArrayList<Song>();
    }
    public List<Song> importSongs() {
        AssetManager am =  _context.getAssets();
        String[] exploded;
        try {
            InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                //split String with delimiter ; into 2 pieces
                exploded = line.split(";",2);
                if (exploded.length == 2) {
                    songs.add(new Song(exploded[0], exploded[1]));
                } else {
                    Log.d("Helpers", "line without ';' delimter: "+ line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }
}
