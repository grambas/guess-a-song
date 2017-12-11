package isdp.guess_a_song.ui.playing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.ActionAnswer;
import isdp.guess_a_song.model.ActionAsk;
import isdp.guess_a_song.model.ActionGameOver;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.ui.GameOver;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.FeedbackText;

public class PlayerInGame extends Activity {
    Button[] btnAnswers = new Button[4];
    int guessTime;
    String gameID;
    PubNubClient client;
    HashMap<Integer, String> currentQ;
    ArrayList<String> scores;
    ActionAsk currentAsk;
    ActionGameOver actionGameOver;
    UserProfile player;

    //c for current
    int cGuess;
    int cCorrect;
    int cQuestionIndex;
    boolean guessed;

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

        /*
        * INIT
        */
        buttonStart = (Button) findViewById(R.id.start);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(100);
        textCounter = (TextView) findViewById(R.id.counter);
        textScore = (TextView) findViewById(R.id.tvScore);

        btnAnswers[0] = (Button) findViewById(R.id.btnAnswer0);
        btnAnswers[1] = (Button) findViewById(R.id.btnAnswer1);
        btnAnswers[2] = (Button) findViewById(R.id.btnAnswer2);
        btnAnswers[3] = (Button) findViewById(R.id.btnAnswer3);


        //HIDE BUTTONS ON START AND ADD LISENER
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (final Button btn : btnAnswers) {
                    btn.setVisibility(View.INVISIBLE);
                    btn.setOnClickListener(answerListener);
                }
            }
        });

        //GET GAME ID FROM JOIN ACTIVITY
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            gameID = (String) b.get("gameID");
            guessTime = (int) b.get("guessTime");
        }

        // SET USER PROFILE
        this.player = new UserProfile();
        this.player.loadProfile(getApplicationContext());
        this.player.setAuth(true);
        this.player.setHost(false);


        this.client = new PubNubClient(player, gameID, false);
        this.client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                //Check if connection status is "successfullyconnected"
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    pubnub.setPresenceState()
                            .channels(Arrays.asList(gameID))
                            //.uuid(userName)
                            .state(player.getState())
                            .async(new PNCallback<PNSetStateResult>() {
                                @Override
                                public void onResponse(final PNSetStateResult result, PNStatus status) {
                                    if (Constants.DEBUG_MODE) {
                                        Log.d(Constants.LOGT, "set auth state true OK");
                                    }
                                }
                            });
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                //Parse and prepare message for usage
                Gson gson = new Gson();
                String action = null;
                String recipient = null;
                JsonObject obj = null;

                try {
                    obj = message.getMessage().getAsJsonObject();
                    recipient = obj.get("recipient").getAsString();
                    action = obj.get("action").getAsString();

                    if (Constants.DEBUG_MODE) {
                        Log.d(Constants.LOGT, "PlayerInGame: action=" + action + ",recipient=" + recipient);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //PROCESS MESSAGE ONLY IF THE MESSAGE IS ASSIGNED FOR USER OR FOR ALL USERS
                if (recipient.equals(client.getUser().getName()) || recipient.equals(Constants.A_FOR_ALL)) {

                    //PROCESS QUESTION MESSAGE
                    if (action.equals(Constants.A_ASK)) {

                        currentAsk = gson.fromJson(message.getMessage(), ActionAsk.class);
                        currentQ = currentAsk.getQuestion();
                        cCorrect = currentAsk.getQCorrect();
                        cQuestionIndex = currentAsk.getQIndex();

                        if (Constants.DEBUG_MODE) {
                            Log.d(Constants.LOGT, "PlayerInGame (loggin A_ASK): action=" + currentAsk.toString() + ",recipient=" + recipient);
                            Log.d(Constants.LOGT, "getQCorrect=" + currentAsk.getQCorrect());
                            Log.d(Constants.LOGT, "cCorrect=" + cCorrect);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(100);
                                myCountDownTimer = new MyCountDownTimer(guessTime * 1000, 1000);
                                textCounter.setTextColor(Color.parseColor("white"));
                                myCountDownTimer.start();
                                for (int i = 0; i < 4; i++) {
                                    //set answers on buttons and change visibility
                                    btnAnswers[i].setText(currentQ.get(i).toString());
                                    btnAnswers[i].setVisibility(View.VISIBLE);
                                    btnAnswers[i].setEnabled(true);
                                    btnAnswers[i].setTextColor(Color.parseColor("white"));
                                }
                            }
                        });
                    }
                    //RPOCESS GAME OVER MESSAGE
                    if (action.equals(Constants.A_FINISH)) {

                        actionGameOver = gson.fromJson(message.getMessage(), ActionGameOver.class);
                        scores = actionGameOver.getScores();

                        if (Constants.DEBUG_MODE) {
                            Log.d(Constants.LOGT, "Got scores: " + scores.toString());
                        }

                        Intent intent1 = new Intent(PlayerInGame.this, GameOver.class);
                        intent1.putStringArrayListExtra("scores", scores);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    }
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }
        });

        this.client.getPubnub().subscribe().channels(Arrays.asList(client.getGameID())).execute();
    }

    // Create an anonymous implementation of OnClickListener to avoid redundant code
    private View.OnClickListener answerListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAnswer0:
                    cGuess = 0;
                    break;
                case R.id.btnAnswer1:
                    cGuess = 1;
                    break;
                case R.id.btnAnswer2:
                    cGuess = 2;
                    break;
                case R.id.btnAnswer3:
                    cGuess = 3;
                    break;
                default:
                    cGuess = -1;
                    break;
            }

            btnAnswers[cGuess].setTextColor(Color.parseColor("blue"));

            //PREPARE ANSWER MESSAGE TO HOST
            ActionAnswer msg = new ActionAnswer(
                    Constants.A_ANSWER,
                    client.getUser().getName(),
                    Constants.HOST_USERNAME,
                    client.getUser().getUuid(),
                    currentAsk.getQIndex(),
                    cGuess
            );

            //SEND ANSWER MESSAGE TO HOST
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
                                    btn.setEnabled(false);
                                    guessed = true;
                                }
                            }
                        });
                    }
                }
            });

        }
    };

    /*
    *  COUNTDOWN TIMER IMPLEMENTATION
    * */
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            textCounter.setText(Long.toString(millisUntilFinished / 1000));
            int progress = (int) (millisUntilFinished / 1000);
            long pos = 100L * progress / guessTime;
            progressBar.setProgress((int) pos);
        }

        @Override
        public void onFinish() {

            //Logic to show for user Feedback

            if (guessed == false) {
                textCounter.setText("Didn't tried? :(");
                textCounter.setTextColor(Color.parseColor("red"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (final Button btn : btnAnswers) {
                            btn.setEnabled(false);
                            guessed = true;
                        }
                    }
                });
            } else if (cGuess == cCorrect) {
                textCounter.setTextColor(Color.parseColor("green"));
                textCounter.setText(FeedbackText.getRandom(true));
                player.addScore(Constants.REWARD_CORRECT);
            } else {
                btnAnswers[cGuess].setTextColor(Color.parseColor("red"));
                textCounter.setTextColor(Color.parseColor("red"));
                textCounter.setText(FeedbackText.getRandom(false));
                player.addScore(Constants.REWARD_WRONG);
            }
            int temp = cQuestionIndex + 1;
            textScore.setText("Score: " + player.getScore() + " Question: " + temp);
            btnAnswers[cCorrect].setTextColor(Color.parseColor("green"));
            progressBar.setProgress(0);
            guessed = false;
        }

    }//COUNTDOWN TIMER IMPLEMENTATION

    //Activity destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.onDestroy();
        }
    }
}