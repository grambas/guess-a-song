package isdp.guess_a_song;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import isdp.guess_a_song.controller.AnswersGenerator;
import isdp.guess_a_song.controller.Game;
import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Constants;

/**
 * Basic implementation fo a score screen. Displays the scores from the current game instance.
 *
 * While nothing actually uses this score screen yet, the functionality is there.
 *
 * Created by Andy on 10/27/2017.
 */

public class ScoreScreen extends AppCompatActivity{

    private TextView scoreText;
    private Game game;
    private Settings settings;
    private int gameID;
    private int gamePIN;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
