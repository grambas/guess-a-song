package isdp.guess_a_song.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.HostGame;
import isdp.guess_a_song.model.Settings;

/**
 * Basic implementation fo a score screen. Displays the scores from the current game instance.
 *
 * While nothing actually uses this score screen yet, the functionality is there.
 *
 * Created by Andy on 10/27/2017.
 */

public class ScoreScreen extends AppCompatActivity {

    private TextView scoreText;
    private HostGame game;
    private Settings settings;
    private int gameID;
    private int gamePIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen);

        scoreText = (TextView) findViewById(R.id.scoreText);

        // GET DATA FROM PREVIOUS VIEW - taken from HostPlayScreen.java
        Bundle bundle = getIntent().getExtras();
        settings = (Settings) bundle.getParcelable("game_settings");
        gameID = bundle.getInt("gameID");
        gamePIN = bundle.getInt("gameID");

        //INIT GAME INSTANCE
        game = game.getInstance();

        game.setSettings(settings);

        scoreText.setText(game.showScore());
    }
}
