package isdp.guess_a_song;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import isdp.guess_a_song.controller.Game;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;

public class HostPlayScreen extends AppCompatActivity {


    private Game game;
    private Settings settings;
    private int gameID;
    private int gamePIN;
    private ArrayList<Question> questions;

    private TextView tvSongname;
    private TextView tvTimer;
    private ProgressBar pbTimer;
    private ImageButton ibPlay;

    private int currentQuestion = 0;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_play_screen);

        tvSongname = (TextView) findViewById(R.id.tvSongname);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        ibPlay = (ImageButton) findViewById(R.id.ibPlay);
        pbTimer = (ProgressBar) findViewById(R.id.pbTimer);


        // GET DATA FROM PREVIOUS VIEW
        Bundle bundle = getIntent().getExtras();
        settings = (Settings) bundle.getParcelable("game_settings");
        gameID = bundle.getInt("gameID");
        gamePIN = bundle.getInt("gameID");
        questions = (ArrayList) bundle.getParcelableArrayList("questions");
        //players = (Settings) bundle.getParcelable("players");



        //INIT GAME INSTANCE
        game = game.getInstance();
        game.setID(gameID);
        game.setPIN(gamePIN);
        game.setSettings(settings);

        tvSongname.setText(questions.get(0).getSong().toString());

        final CountDownTimer countDownTimer = new CountDownTimer(game.getSettings().getGuess_time() * 1000, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(Long.toString(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
            }
        };

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(questions.get(currentQuestion), countDownTimer);
            }
        });


        //TODO get this progressbar working

        //game.setQuestions(questions);
        //game.setPlayers(players);
        game.start();

    }

    private void playSong(Question q, CountDownTimer timer) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(q.getSong().getPath()));
            mediaPlayer.start();
            timer.start();
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                timer.cancel();
            } else {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(q.getSong().getPath()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    timer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void onBackPressed() {
        //Asking the player to quit or something
    }
}
