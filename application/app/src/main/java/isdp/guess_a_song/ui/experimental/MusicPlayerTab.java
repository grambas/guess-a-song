package isdp.guess_a_song.ui.experimental;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.HostGame;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.pubsub.PresenceListAdapter;
import isdp.guess_a_song.pubsub.PresencePnCallback;
import isdp.guess_a_song.utils.Constants;

/**
 * Created by Maxi on 15.11.2017.
 */



public class MusicPlayerTab extends Fragment implements Observer {

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

    private PlayerOnlineTab playerOnlineTab;

    //private int currentQuestion = 0;
    private MediaPlayer mediaPlayer;
    private GameCreationTab gameCreationTab;
    private PagerAdapter pagerAdapter;

    public void setGameCreationTab(GameCreationTab gameCreationTab) {
        this.gameCreationTab = gameCreationTab;
    }

    public void setPlayerOnlineTab(PlayerOnlineTab playerOnlineTab) {
        this.playerOnlineTab = playerOnlineTab;
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_host_play_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //INIT GUI FIELDS
        tvSongname = (TextView) getView().findViewById(R.id.tvSongname);
        tvTimer = (TextView) getView().findViewById(R.id.tvTimer);
        tvAnswers = (TextView) getView().findViewById(R.id.tvCurrentAnswers);
        tvStatus = (TextView) getView().findViewById(R.id.tvCurrentStatus);
        ibPlay = (ImageButton) getView().findViewById(R.id.ibPlay);
        pbTimer = (ProgressBar) getView().findViewById(R.id.pbTimer);
        btNext = (Button) getView().findViewById(R.id.btNextSong);
        btShowScore = (Button) getView().findViewById(R.id.btShowScore);


        // GET DATA FROM PREVIOUS VIEW

        Intent intent = getActivity().getIntent();
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

        final UserProfile host = new UserProfile();
        host.loadProfile(getView().getContext().getApplicationContext());

        //pubnub
        client = new PubNubClient(host, game.getSettings().getGameIDString(), true);
        this.mPresencePnCallback = new PresencePnCallback(gameCreationTab.getPresenceListAdapter());
        client.initChannelsHost(mPresencePnCallback);
        listView = (ListView) getView().findViewById(R.id.presence_list);

        client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                Gson gson = new Gson();
                boolean processed = false;
                String action = null;
                JsonObject obj=null;
                JsonElement actionElm=null;
                try {
                    obj = message.getMessage().getAsJsonObject();
                    actionElm = obj.get("action");
                    action = actionElm.getAsString();
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
                }
//                else{
//                    Log.d(Constants.LOGT, "HostPlayerScreen: msg skiped: "+message.getMessage().toString());
//                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {}
            @Override
            public void status(PubNub pubnub, PNStatus status) {}
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTimer.setVisibility(View.INVISIBLE);
                    }
                });
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

                Toast.makeText(getView().getContext(), game.showScore(), Toast.LENGTH_SHORT).show();
            }
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
                        Toast.makeText(getView().getContext(), "No more questions!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getView().getContext(), "Status is not READY!", Toast.LENGTH_SHORT).show();
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTimer.setVisibility(View.VISIBLE);
                        }
                    });

                    playSong(game.getCurrentQuestion().getSong().getPath(), countDownTimer);

                }

            }
        });
    }


    //Managing the mediaplayer
    //The song will be played, if nothing is currently playing
    //If a song is playing, the song stops as well as the timer.
    private void playSong(String path, CountDownTimer timer) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(path));
            mediaPlayer.start();
            timer.start();
            gameCreationTab.setMediaPlayer(mediaPlayer);
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                timer.cancel();
            } else {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(getContext(), Uri.parse(path));
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
    public void update(Observable observable, Object o) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvSongname.setText(game.getCurrentQuestion().getSong().toString());
                tvAnswers.setText("Answered: " + game.getAns_amount());
                tvStatus.setText("Status: " + game.getHumanStatus());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        game.deleteObserver(this);
        client.getPubnub().unsubscribeAll();
    }
}
