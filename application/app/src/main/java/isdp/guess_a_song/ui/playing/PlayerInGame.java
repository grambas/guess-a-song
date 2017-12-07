package isdp.guess_a_song.ui.playing;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.Arrays;
import java.util.HashMap;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;

public class PlayerInGame extends Activity {
    Button[] btnAnswers = new Button[4];
    int guessTime;
    String gameID;
    PubNubClient client;
    HashMap<Integer, String> currentQ;
    ActionAsk currentAsk;
    UserProfile player;


    //countdown
    Button buttonStart;
    ProgressBar progressBar;
    TextView textCounter;
    TextView textScore;
    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_in_game);

        buttonStart = (Button)findViewById(R.id.start);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setMax(100);
        textCounter = (TextView)findViewById(R.id.counter);
        textScore = (TextView) findViewById(R.id.tvScore);


        btnAnswers[0] = (Button) findViewById(R.id.btnAnswer1);
        btnAnswers[1] = (Button) findViewById(R.id.btnAnswer2);
        btnAnswers[2] = (Button) findViewById(R.id.btnAnswer3);
        btnAnswers[3] = (Button) findViewById(R.id.btnAnswer4);



    //HIDE BUTTONS ON START AND ADD LISTENER
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final Button btn : btnAnswers) {
                    btn.setVisibility(View.INVISIBLE);
                    btn.setOnClickListener(answerListener);
                }
            }
        });

        //GET GAMEID FROM JOIN ACTIVITY
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            gameID = (String) b.get("gameID");
            guessTime = (int) b.get("guessTime");
        }

        this.player = new UserProfile();
        this.player.loadProfile(getApplicationContext());
        this.player.setAuth(true);
        this.player.setHost(false);
        this.client = new PubNubClient(player, gameID, false);

        this.client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    pubnub.setPresenceState()
                            .channels(Arrays.asList(gameID))
                            //.uuid(userName)
                            .state(player.getState())
                            .async(new PNCallback<PNSetStateResult>() {
                                @Override
                                public void onResponse(final PNSetStateResult result, PNStatus status) {
                                    Log.d(Constants.LOGT, "set auth state true OK");
                                }
                            });
                }


            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                Gson gson = new Gson();
                String action = null;
                String recipient = null;
                JsonObject obj=null;

                try {
                    obj = message.getMessage().getAsJsonObject();
                    recipient = obj.get("recipient").getAsString();
                    action =obj.get("action").getAsString();

                    Log.d(Constants.LOGT,"PlayerInGame: action="+action + ",recipient="+recipient);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (recipient.equals(client.getUser().getName()) || recipient.equals(Constants.A_FOR_ALL)) {
                    if (action.equals(Constants.A_ASK)) {
                        currentAsk = gson.fromJson(message.getMessage(), ActionAsk.class);
                        currentQ = currentAsk.getQuestion();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(100);
                                myCountDownTimer =  new MyCountDownTimer(guessTime * 1000, 1000);
                                myCountDownTimer.start();
                                for (int i = 0; i < 4; i++) {
                                    //set answers on buttons and change visibility
                                    btnAnswers[i].setText(currentQ.get(i).toString());
                                    btnAnswers[i].setVisibility(View.VISIBLE);
                                }
                            }
                        });//
                    }
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {}
        });

        this.client.getPubnub().subscribe().channels(Arrays.asList(client.getGameID())).execute();
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener answerListener = new View.OnClickListener() {
        public void onClick(View v) {
            int answer;
            if (v.getId() == R.id.btnAnswer1) {
                answer = 0;
            } else if (v.getId() == R.id.btnAnswer2) {
                answer = 1;
            } else if (v.getId() == R.id.btnAnswer3) {
                answer = 2;
            } else if (v.getId() == R.id.btnAnswer4) {
                answer = 3;
            } else {
                answer = -1;
            }
            ActionAnswer msg = new ActionAnswer(
                    Constants.A_ANSWER,
                    client.getUser().getName(),
                    Constants.HOST_USERNAME,
                    client.getUser().getUuid(),
                    currentAsk.getqIndex(),
                    answer
            );
            client.getPubnub().publish()
                    .channel(client.getGameID())
                    //.meta(Helpers.signHostMeta())
                    .message(msg).async(new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {

                    if (!status.isError()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Button btn : btnAnswers) {
                                    btn.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                }
            });

        }
    };
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            textCounter.setText(Long.toString(millisUntilFinished / 1000));
            int progress = (int) (millisUntilFinished/1000);
            long pos=100L * progress/guessTime;
            //Log.d(Constants.LOGT, "pos: "+pos);
            progressBar.setProgress((int) pos);
        }

        @Override
        public void onFinish() {
            textCounter.setText("Finished");
            textScore.setText("Score: " + player.getScore());
            progressBar.setProgress(0);
        }

    }

    //Activity destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.getPubnub().unsubscribeAll();
    }
}