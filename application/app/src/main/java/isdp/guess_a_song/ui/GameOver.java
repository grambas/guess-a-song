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
        List<String> scores = getIntent().getStringArrayListExtra("scores");

        listView = (ListView) findViewById(R.id.lvScores);
        ArrayAdapter<String> scoreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, scores );
        listView.setAdapter(scoreAdapter);


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
