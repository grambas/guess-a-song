package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.Song;

public class _2SelectSongs extends AppCompatActivity {

    private ListView listView;
    private Button btNext;
    Settings game_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_songs);

        game_settings = getIntent().getExtras().getParcelable("game_settings");


        Toast.makeText(_2SelectSongs.this, game_settings.toString(), Toast.LENGTH_LONG).show();


        //Songs still have to be parsed

        DatabaseHandler db = new DatabaseHandler(this);
        List<Song> songs = db.getAllSongs(1); // 1 - real songs only

        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);

        ArrayAdapter<Song> songAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, songs);
        listView.setAdapter(songAdapter);

        //Limit checking
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int cntChoice = listView.getCheckedItemCount();

                if (cntChoice > limit) {
                    SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                    for (int i = 0; i < listView.getCount(); i++) {
                        if (sparseBooleanArray.get(i)) {
                            listView.setItemChecked(i, false);
                            break;
                        }
                    }
                }
            }
        });
        */


        btNext = (Button) findViewById(R.id.btNext);
        //Example to show the songs you selected
        //Will be replaced for changing the activity and storing the selected songs
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cntChoice = listView.getCount();


                String selected = "";
                ArrayList<Song> selected_songs = new ArrayList<Song>();
                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
                        selected += listView.getItemAtPosition(i).toString() + "\n";
                        selected_songs.add((Song) listView.getItemAtPosition(i));// add songs to list
                    }
                }

                Toast.makeText(_2SelectSongs.this, selected, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(_2SelectSongs.this, _3EditQuestion.class);

                intent.putParcelableArrayListExtra("selected_songs",selected_songs);
                intent.putExtra("game_settings", game_settings);
                startActivity(intent);

            }
        });


    }


}
