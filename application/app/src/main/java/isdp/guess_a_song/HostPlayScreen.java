package isdp.guess_a_song;


import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import isdp.guess_a_song.controller.HostGame;
import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.pubsub.PresenceListAdapter;
import isdp.guess_a_song.pubsub.PresencePnCallback;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

public class HostPlayScreen extends AppCompatActivity implements Observer {


    private HostGame game;
    private Settings settings;
    private int gameID;
    private int gamePIN;
    private ArrayList<Question> questions;

    CountDownTimer countDownTimer;

    //PUBNUB
    private PubNubClient client;
    private PresenceListAdapter mPresence;
    private PresencePnCallback mPresencePnCallback;


    private TextView tvSongname;
    private TextView tvTimer;
    private TextView tvAnswers;
    private ProgressBar pbTimer;
    private ImageButton ibPlay;
    private Button btNext;
    private Button btShowScore;
    private TextView tvAnsGot;

    private int currentQuestion = 0;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_play_screen);

        tvSongname = (TextView) findViewById(R.id.tvSongname);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvAnswers = (TextView) findViewById(R.id.tvCurrentAnswers);
        ibPlay = (ImageButton) findViewById(R.id.ibPlay);
        pbTimer = (ProgressBar) findViewById(R.id.pbTimer);
        btNext = (Button) findViewById(R.id.btNextSong);
        btShowScore = (Button) findViewById(R.id.btShowScore);
        tvAnsGot = (TextView) findViewById(R.id.tvAnsGot);


        // GET DATA FROM PREVIOUS VIEW


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Settings settings =  bundle.getParcelable("game_settings");
        List<Question> questions = intent.getParcelableArrayListExtra("questions");
        List<UserProfile> players = intent.getParcelableArrayListExtra("players");



        //INIT GAME INSTANCE
        game = game.getInstance();
        game.setSettings(settings);
        game.setQuestions(questions);
        game.setPlayers(players);
        game.addObserver(this);


        //pubnub
        client = new PubNubClient(new UserProfile(Constants.HOST_USERNAME),game.getSettings().getGameIDString(),true);
        this.mPresence = new PresenceListAdapter(this);
        this.mPresencePnCallback = new PresencePnCallback(this.mPresence);
        client.subscribe(game.getSettings().getGameIDString(),Constants.WITH_PRESENCE);

        client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                Gson gson = new Gson();
                boolean processed = false;
                ActionAnswer action= gson.fromJson(message.getMessage(), ActionAnswer.class);

                if (action.getRecipient() != null && action.getRecipient().equals(Constants.HOST_USERNAME)){
                    if(action.getAction().equals(Constants.A_ANSWER)){
                        Log.d(Constants.LOGT, "ANSWER GOT "+ message.getMessage().toString());
                        processed = game.processAnswer(action.getUuid(),action.getAnswerIndex(),action.getQuestionIndex());
                        if (!processed){
                            //Toast.makeText(HostPlayScreen.this, "Error in answer process", Toast.LENGTH_SHORT).show();
                            Log.d(Constants.LOGT, "Error in answer process");
                        }
                    }
                }
            }
            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {}
            @Override
            public void status(PubNub pubnub, PNStatus status) {}
        });


        game.start();

        countDownTimer = new CountDownTimer(game.getSettings().getGuess_time() * 1000, 1000) {
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
                game.setStatus(Constants.GAME_STATUS_TIME_OVER);
            }
        };

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlay();
            }
        });
        btShowScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.LOGT, game.showScore());
                Toast.makeText(HostPlayScreen.this, game.showScore(), Toast.LENGTH_SHORT).show();
            }
        });
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Any restrictions ? Like the song has to be finished?
                if(game.getStatus() == Constants.GAME_STATUS_READY ||
                        game.getStatus() == Constants.GAME_STATUS_TIME_OVER){

                    if (game.next_q()) {
                        game.setAns_amount(0);
                        countDownTimer.cancel();
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }
                        //tvSongname.setText(game.getCurrentQuestion().getSong().toString());
                        //tvAnswers.setText("Question: " + game.getCurrentQuestion());
                    } else {
                        //Multiplayer-stuff and score screen?
                        //no more qeustions
                    }
                }else{
                    Toast.makeText(HostPlayScreen.this, "Status is not READY!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void handlePlay() {

        Action msg = new ActionAsk(Constants.A_ASK,Constants.HOST_USERNAME,Constants.A_FOR_ALL,game.getCurrentQuestion().songsToPlayers());
        client.getPubnub().publish()
                .channel(this.game.getSettings().getGameIDString())
                .meta(Helpers.signHostMeta())
                .message( msg ).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // handle publish response
                //check if succes and play song local
                Log.d(Constants.LOGT,"now playing song from handler");
                game.setStatus(Constants.GAME_STATUS_ON_QUESTION);
                playSong(game.getCurrentQuestion().getSong().getPath(), countDownTimer);

            }
        });
    }


    //Managing the mediaplayer
    //The song will be played, if nothing is currently playing
    //If a song is playing, the song stops as well as the timer.
    private void playSong(String path, CountDownTimer timer) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
            mediaPlayer.start();
            timer.start();
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                timer.cancel();
            } else {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(path));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    timer.start();
                } catch (IOException e) {
                    Log.e("Error", "Cannot play song.");
                }

            }
        }
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


    @Override
    public void update(Observable observable, Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSongname.setText(game.getCurrentQuestion().getSong().toString());
                tvAnswers.setText("Question: " + game.getCurrentQuestion());
                tvAnsGot.setText("Question: " + game.getAns_amount());
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.deleteObserver(this);
    }
}
