package isdp.guess_a_song;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
    private TextView tvQuestion;
    private ProgressBar pbTimer;
    private ImageButton ibPlay;
    private Button btNext;

    private int currentQuestion = 0;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_play_screen);

        tvSongname = (TextView) findViewById(R.id.tvSongname);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvQuestion = (TextView) findViewById(R.id.tvCurrentQuestion);
        ibPlay = (ImageButton) findViewById(R.id.ibPlay);
        pbTimer = (ProgressBar) findViewById(R.id.pbTimer);
        btNext = (Button) findViewById(R.id.btNextSong);


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
        tvQuestion.setText("Question: " + Integer.toString(currentQuestion + 1));

        final CountDownTimer countDownTimer = new CountDownTimer(game.getSettings().getGuess_time() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(Long.toString(millisUntilFinished/1000));
                if (mediaPlayer != null) {
                    pbTimer.setProgress((int)(((double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration()) * 100));
                }

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

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Multiplayer stuff

                //Any restrictions ? Like the song has to be finished?


                if (questions.size() > currentQuestion+1) {
                    currentQuestion++;
                    setCurrentQuestion(countDownTimer, currentQuestion);
                } else {
                    //Multiplayer-stuff and score screen?
                }
            }
        });


        //game.setQuestions(questions);
        //game.setPlayers(players);
        game.start();

    }

    //Managing the mediaplayer
    //The song will be played, if nothing is currently playing
    //If a song is playing, the song stops as well as the timer.
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
                    Log.e("Error", "Cannot play song.");
                }

            }
        }

    }

    private void setCurrentQuestion(CountDownTimer countDownTimer, int currentQuestion) {
        countDownTimer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        tvSongname.setText(questions.get(currentQuestion).getSong().toString());
        tvQuestion.setText("Question: " + Integer.toString(currentQuestion + 1));

    }

    @Override
    public void onBackPressed() {
        //Asking the player to quit or something
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you really want to quit?");
        builder.setMessage("This will disband the room.");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                //More multiplayer stuff (Like closing the room)

                HostPlayScreen.super.onBackPressed();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Doing nothing...
                    }
                }
            );
        builder.show();
    }
}
