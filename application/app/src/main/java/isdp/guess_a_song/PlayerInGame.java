package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.HashMap;

import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;

public class PlayerInGame extends Activity {
    Button[] btnAnswers = new Button[4];
    PubNubClient client;
    HashMap<Integer, String> currentQ;
    ActionAsk currentAsk;
    UserProfile player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_in_game);

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
        String gameID = null;
        if (b != null) {
            gameID = (String) b.get("gameID");
        }
        this.player = new UserProfile();
        this.player.loadProfile(getApplicationContext());
        this.client = new PubNubClient(player, gameID, false);

        this.client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {}

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                try {
                    Log.v(Constants.LOGT, "PLAYER MESSAGE LISTENER: (" + message.toString() + ")");
                    Gson gson = new Gson();
                    currentAsk = gson.fromJson(message.getMessage(), ActionAsk.class);

                    if (currentAsk.getRecipient().equals(client.getUser().getName()) || currentAsk.getRecipient().equals(Constants.A_FOR_ALL)) {

                        if (currentAsk.getAction().equals(Constants.A_ASK)) {

                            currentQ = currentAsk.getQuestion();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 4; i++) {
                                        //set answers on buttons and change visibility
                                        btnAnswers[i].setText(currentQ.get(i).toString());
                                        btnAnswers[i].setVisibility(View.VISIBLE);
                                    }
                                }
                            });//
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
}