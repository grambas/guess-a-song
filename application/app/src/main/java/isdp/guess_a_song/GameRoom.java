package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.controller.AnswersGenerator;
import isdp.guess_a_song.controller.Game;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

public class GameRoom extends AppCompatActivity {

    private int gameID;
    private int gamePIN;

    private TextView tvGameId;
    private TextView tvPIN;
    private Game game;
    private Settings game_settings;
    private ArrayList<Question> questions;


    /**
     * Create gameID and gamePIN. We do not create main game instance here due to avoid
     * unnecessary object transfer in Activities.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);


        game_settings = getIntent().getExtras().getParcelable("game_settings");
        questions =  getIntent().getParcelableArrayListExtra("questions");



        //To test questions object
        if(questions !=null){
            for (Question q : questions) {
                Log.d("GameRoom",q.toString());
            }
        }


        // generate  game ID & PIN
        gameID = Helpers.randomInt(Constants.RANDOM_MIN,Constants.RANDOM_MAX);
        gamePIN = Helpers.randomInt(Constants.RANDOM_MIN,Constants.RANDOM_MAX);

        // Set display fields
        tvGameId = (TextView) findViewById(R.id.tvGameId);
        tvPIN = (TextView) findViewById(R.id.tvPIN);

        tvGameId.setText("Game ID: " + gameID);
        tvPIN.setText("PIN: " + gamePIN);

        //The grid view will be filled with the profile pictures? of each player
    }

    public void startGame(View view) {

        Intent intent = new Intent(this, HostPlayScreen.class);

        //send game settings and game questions (instead of songs) to next activity

        //TODO intent.putParcelableArrayListExtra("players",players);
        intent.putExtra("gameID", gameID);
        intent.putExtra("gamePIN", gamePIN);

        intent.putExtra("game_settings", game_settings);
        intent.putParcelableArrayListExtra("questions", questions);

        startActivity(intent);
    }
}
