package isdp.guess_a_song.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import isdp.guess_a_song.R;
import isdp.guess_a_song.StartScreen;
import isdp.guess_a_song.model.Score;

public class GameOver extends Activity {

    private ListView listView;
    private Button btBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        /*
        Get the scores somewhere?
         */

        List<Score> scores = new ArrayList<>();
        //Sorts the List, so the player with most correctly answered questions is #1.
        scores.sort(new Comparator<Score>() {
        @Override public int compare(Score s1, Score s2) {
            if (s1.getScore() > s2.getScore()) {
                return 1;
            }
            if (s1.getScore() < s2.getScore()) {
                return -1;
            }
            return 0;
        }});

        listView = (ListView) findViewById(R.id.lvScores);
        ArrayAdapter<Score> scoreAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_1, android.R.id.text1, scores );


        btBackToMenu = (Button) findViewById(R.id.btBackToMenu);
        btBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, StartScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

}
