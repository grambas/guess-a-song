package isdp.guess_a_song.ui.playing;


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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.HostGame;
import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.ActionSimple;
import isdp.guess_a_song.model.PresencePojo;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.pubsub.PresenceListAdapter;
import isdp.guess_a_song.pubsub.PresencePnCallback;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

public class HostPlayScreen extends AppCompatActivity implements Observer {


    private HostGame game;;
    private ArrayList<Question> questions;

    private CountDownTimer countDownTimer;

    //PUBNUB
    private PubNubClient client;
    private PresenceListAdapter mPresence;
    private PresencePnCallback mPresencePnCallback;


    private TextView tvSongname;
    private TextView tvTimer;
    private TextView tvAnswers;
    private TextView tvStatus;
    private ProgressBar pbTimer;
    private ImageButton ibPlay;
    private Button btNext;
    private Button btShowScore;
    private TextView tvAnsGot;
    private ListView listView;

    //private int currentQuestion = 0;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_play_screen);

        //INIT GUI FIELDS
        tvSongname = (TextView) findViewById(R.id.tvSongname);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvAnswers = (TextView) findViewById(R.id.tvCurrentAnswers);
        tvStatus = (TextView) findViewById(R.id.tvCurrentStatus);
        ibPlay = (ImageButton) findViewById(R.id.ibPlay);
        pbTimer = (ProgressBar) findViewById(R.id.pbTimer);
        btNext = (Button) findViewById(R.id.btNextSong);
        btShowScore = (Button) findViewById(R.id.btShowScore);


        // GET DATA FROM PREVIOUS VIEW

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Settings settings = bundle.getParcelable("game_settings");
        List<Question> questions = intent.getParcelableArrayListExtra("questions");
        List<UserProfile> players = intent.getParcelableArrayListExtra("players");


        //INIT GAME INSTANCE
        game = game.getInstance();
        game.setSettings(settings);
        game.setQuestions(questions);
        game.setPlayers(players);
        game.addObserver(this);
        // Shuffle answers
        for (final Question q : game.getQuestions()) {
            q.shuffle();
        }
        game.start();

        //client
        final UserProfile host = new UserProfile(Constants.HOST_USERNAME);
        host.loadProfile(getApplicationContext());
        host.setAuth(true);
        host.setHost(true);

        //pubnub
        client = new PubNubClient(host, game.getSettings().getGameIDString(), host.isHost());
        this.mPresence = new PresenceListAdapter(this);
        this.mPresencePnCallback = new PresencePnCallback(this.mPresence);
        client.initChannelsHost(mPresencePnCallback);
        listView = (ListView) findViewById(R.id.presence_list);
        listView.setAdapter(this.mPresence);

        client.getPubnub().addListener(new SubscribeCallback() {

            @Override

	            public void status(PubNub pubnub, PNStatus status) {
                             if (status != null && status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    	                    //set auth state true for host automatically
                                        pubnub.setPresenceState()
                    	                            .channels(Arrays.asList(game.getSettings().getGameIDString()))
                    	                            //.uuid(userName)
                                               .state(host.getState())
                                                .async(new PNCallback<PNSetStateResult>() {
				                               @Override
				                                public void onResponse(final PNSetStateResult result, PNStatus status) {
                                                            }
					                            });

                    	                }
                	            }



            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                Gson gson = new Gson();
                boolean processed;
                String action = null;
                JsonObject obj=null;
                JsonElement actionElm=null;
                try {
                    obj = message.getMessage().getAsJsonObject();
                    action = obj.get("action").getAsString();
                    Log.d(Constants.LOGT,"HostPlayerScreen: action: "+action);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(action.equals(Constants.A_ANSWER)){
                    ActionAnswer msgAnswer =  gson.fromJson(message.getMessage(), ActionAnswer.class);
                    if (game.getStatus() == Constants.GAME_STATUS_ON_QUESTION) {
                        processed = game.processAnswer(msgAnswer.getUuid(), msgAnswer.getAnswerIndex(), msgAnswer.getQuestionIndex());
                        if (!processed) {
                            Log.d(Constants.LOGT, "Error in answer process");
                        }
                    }
                }else if (action.equals(Constants.A_LOG_IN)) {
                    //IF PIN IS GOOD
                    ActionSimple msgLogIn = gson.fromJson(message.getMessage(), ActionSimple.class);
                    Log.d(Constants.LOGT,"HostPlayScreen:user pin ==host pin?:"+msgLogIn.getValue()+"=="+game.getSettings().getGamePIN());
                    if ( Integer.parseInt(msgLogIn.getValue()) == game.getSettings().getGamePIN()) {
                        client.publish(new ActionSimple(Constants.A_START_GAME, String.valueOf(game.getSettings().getGuess_time()), Constants.HOST_USERNAME, msgLogIn.getPublisher()), Helpers.signHostMeta());
                        Log.d(Constants.LOGT, "HostPlayScreen Listener: "+ msgLogIn.getPublisher() + " auth true + direct redirect to game)");
                        syncPlayersWithPresence();
                        //IF PIN IS BAD
                    }else{
                        client.publish(new ActionSimple(Constants.A_AUTH_RESPONSE,Constants.FALSE,Constants.HOST_USERNAME,msgLogIn.getPublisher()),Helpers.signHostMeta());
                        Log.d(Constants.LOGT, "HostPlayScreen Listener: "+ msgLogIn.getPublisher() + " auth false (bad pin)");
                    }
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {}

        });


        countDownTimer = new CountDownTimer(game.getSettings().getGuess_time() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(Long.toString(millisUntilFinished / 1000));
                if (mediaPlayer != null) {
                    pbTimer.setProgress((int) (((double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration()) * 100));
                }
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                pbTimer.setProgress(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTimer.setVisibility(View.INVISIBLE);
                        ibPlay.setVisibility(View.VISIBLE);
                        btNext.setVisibility(View.VISIBLE);
                    }
                });
                game.setStatus(Constants.GAME_STATUS_TIME_OVER);
            }
        };

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(game.getStatus() != Constants.GAME_STATUS_ON_QUESTION) {
                    handlePlay();
                }
            }
        });
        btShowScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.LOGT, game.showScore());
                client.hereNow(mPresencePnCallback);
                syncPlayersWithPresence();
                Toast.makeText(HostPlayScreen.this, "ID: "+game.getSettings().getGameID()+" PIN: "+game.getSettings().getGamePIN()+"\n"+game.showScore(), Toast.LENGTH_SHORT).show();}
        });
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Any restrictions ? Like the song has to be finished?
                if (game.getStatus() == Constants.GAME_STATUS_READY ||
                        game.getStatus() == Constants.GAME_STATUS_TIME_OVER) {

                    if (game.next_q()) {
                        countDownTimer.cancel();
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }
                    } else {
                        Toast.makeText(HostPlayScreen.this, "No more questions!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HostPlayScreen.this, "Status is not READY!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void handlePlay() {

        Action msg = new ActionAsk(
                Constants.A_ASK,
                Constants.HOST_USERNAME,
                Constants.A_FOR_ALL,
                game.getCurrentQuestion().songsToPlayers(),
                game.getCurrentQuestion().getCorrectIndex(),
                game.getCurrentIndex()
        );

        client.getPubnub().publish()
                .channel(this.game.getSettings().getGameIDString())
                //.meta(Helpers.signHostMeta())
                .message(msg).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // handle publish response
                //check if succes and play song local
                if(!status.isError()){
                    Log.d(Constants.LOGT, "now playing song from handler");
                    game.setStatus(Constants.GAME_STATUS_ON_QUESTION);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ibPlay.setVisibility(View.INVISIBLE);
                            btNext.setVisibility(View.INVISIBLE);
                            tvTimer.setVisibility(View.VISIBLE);
                        }
                    });

                    playSong(game.getCurrentQuestion().getSong().getPath(), countDownTimer);

                }

            }
        });
    }

    public void syncPlayersWithPresence() {
        boolean exist;
        Map<String, PresencePojo> temp = mPresence.getItems();
        if( temp!=null){
            for (Map.Entry<String, PresencePojo> entry : temp.entrySet())
            {
                exist=false;
                for (String key : game.getPlayers().keySet()) {
                    Log.d(Constants.LOGT,"is equals? "+key+"=="+entry.getKey().toString());
                    if(key.equals(entry.getKey().toString())){
                        exist = true;
                        Log.d(Constants.LOGT,"YES");
                    }

                }
                //if not exist and not host
                if(!exist){
                    String checkBoss = entry.getValue().getName();
                    if(checkBoss !=null && !checkBoss.equals(Constants.HOST_USERNAME)){
                        UserProfile tempUser = new UserProfile(
                                entry.getValue().getName(),
                                entry.getValue().getSender(),
                                true,
                                false
                        );
                        Log.d(Constants.LOGT,"adding user"+tempUser.toString());
                        game.addPlayers(tempUser);
                    }

                }
            }
        }

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

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBackPressed() {
        //Asking the player to quit or something
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.d(Constants.LOGT,"Game status when back is pressed"+game.getStatus());

        if(game.getStatus() == Constants.GAME_STATUS_ON_QUESTION){
            builder.setTitle("Game is on question");
            builder.setMessage("You cannot back during the question.");
        }else{
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
        }


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
                tvAnswers.setText("Answered: " + game.getAns_amount());
                tvStatus.setText("Status: " + game.getHumanStatus());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.deleteObserver(this);
        if(client != null && client.getPubnub()!=null){
            client.getPubnub().unsubscribeAll();
        }    }
}
