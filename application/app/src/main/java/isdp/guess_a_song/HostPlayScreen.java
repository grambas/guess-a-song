package isdp.guess_a_song;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import isdp.guess_a_song.controller.Game;
import isdp.guess_a_song.model.Settings;

public class HostPlayScreen extends AppCompatActivity {

    private ProgressBar pbTimer;
    private Game game;
    private Settings settings;
    private int gameID;
    private int gamePIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_play_screen);


        // GET DATA FROM PREVIOUS VIEW
        Bundle bundle = getIntent().getExtras();
        settings = (Settings) bundle.getParcelable("game_settings");
        gameID = bundle.getInt("gameID");
        gamePIN = bundle.getInt("gameID");
        //questions = (Settings) bundle.getParcelable("questions");
        //players = (Settings) bundle.getParcelable("players");



        //INIT GAME INSTANCE
        game = game.getInstance();
        game.setID(gameID);
        game.setPIN(gamePIN);
        game.setSettings(settings);

        //game.setQuestions(questions);
        //game.setPlayers(players);
        game.start();

    }
}
