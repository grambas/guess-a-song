package isdp.guess_a_song;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.controller.PlayerGame;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;

public class JoinGame extends AppCompatActivity {

    private EditText gameID_field;
    private EditText gamePIN_field;
    private EditText name_field;
    Button btnJoin;
    private ProgressBar spinner;

    private PlayerGame game;

    private  String gameID;
    private  String gamePIN;
    private String gameName;
    ImageView checkImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        gameID_field= (EditText)findViewById(R.id.etGameId);
        gamePIN_field = (EditText) findViewById(R.id.etPin);
        name_field = (EditText) findViewById(R.id.etName);
        btnJoin = (Button) findViewById(R.id.btJoin);

        checkImg = (ImageView) findViewById(R.id.imageView);

        this.game = new PlayerGame(0,0);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        checkImg.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.GONE);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO DOES NOT WORK
                btnJoin.setVisibility(View.VISIBLE);
            }
        });


    }

    public void join(View view) {

        spinner.setVisibility(View.VISIBLE);

        gameID = gameID_field.getText().toString();
        gamePIN = gamePIN_field.getText().toString();
        gameName = name_field.getText().toString();

        spinner=(ProgressBar)findViewById(R.id.progressBar);

        PubNubClient client = new PubNubClient(new UserProfile(this.gameName),this.gameID);

        client.subscribe(this.gameID,Constants.NO_PRESENCE);
        // channel subscribed. Now waiting for players.


        client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    //USER CONNECTED
                    //{ "action": "auth_checl",   "value": "pin...","publisher": "nickname..","recipient": "THE BOSS"}
                    pubnub.publish().channel(gameID).message(new Action(Constants.A_LOG_IN, gamePIN, gameName, Constants.HOST_USERNAME) ).async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // handle publish response
                        }
                    });
                }
            }
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                try {
                    Log.v(Constants.LOGT, "PLAYER MESSAGE LISTENER: (" +message.toString() + ")");
                    Gson gson = new Gson();
                    Action action= gson.fromJson(message.getMessage(), Action.class);
                    if(action.getRecipient().equals(gameName)  ){
                        if (action.getAction().equals(Constants.A_AUTH_RESPONSE)) {
                            if (action.getValue().equals(Constants.TRUE)) {
                                Log.d(Constants.LOGT, "PLAYER MESSAGE LISTENER: AUTH SUCCESS");
                                checkImg.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);

                                game.setAuth(1);
                                // NOTIFY USER FOR SUCCESSFULY AUTH.
                            } else {
                                Log.d(Constants.LOGT, "PLAYER MESSAGE LISTENER: AUTH FALSE");
                                spinner.setVisibility(View.GONE);
                                btnJoin.setVisibility(View.VISIBLE);
                                btnJoin.invalidate();

                            }
                        }else if (action.getAction().equals(Constants.A_START_GAME)) {
                            if ( action.getValue().equals(Constants.TRUE) && action.getPublisher().equals(Constants.HOST_USERNAME)) {
                               //TODO START A GAME TRIGGER HERE
                           }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });





        //test publish
        client.publish(gameID, new Action("log_in", gamePIN, game.getUser().getName(), Constants.HOST_USERNAME) );
        //Intent for the join screen
    }
}
