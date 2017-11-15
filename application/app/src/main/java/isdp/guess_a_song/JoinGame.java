package isdp.guess_a_song;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.ActionSimple;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;
public class JoinGame extends AppCompatActivity {

    private EditText gameID_field;
    private EditText gamePIN_field;
    private TextView name_field;
    private TextView info_field;
    private Button btnJoin;
    private ProgressBar spinner;
    private  String gameID;
    private  String gamePIN;
    private ImageView checkImg;
    private PubNubClient client;

    UserProfile player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // INIT
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        gameID_field= (EditText)findViewById(R.id.etGameId);
        gamePIN_field = (EditText) findViewById(R.id.etPin);
        name_field = (TextView) findViewById(R.id.etName);
        info_field = (TextView) findViewById(R.id.tvInfo);
        btnJoin = (Button) findViewById(R.id.btJoin);
        checkImg = (ImageView) findViewById(R.id.imageView);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        checkImg.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.GONE);

        player = new UserProfile(Constants.DEFAULT_PLAYER_NAME);
        player.loadProfile(getApplicationContext());
        player.setHost(false);
        player.setAuth(false);
        name_field.setText("Name: " + player.getName());
        //this.game = new PlayerGame(0,0);
        //Some adjustments
        btnJoin.setVisibility(View.VISIBLE);
        info_field.setText("Please fill all fields and click join");



    }

    public void join(View view) {

        gameID = gameID_field.getText().toString();
        gamePIN = gamePIN_field.getText().toString();

        spinner.setVisibility(View.VISIBLE);


        this.client = new PubNubClient(player,this.gameID,false);


        //this.client.getRoomPlayers();

        this.client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    // USER CONNECTED
                    // Ask Host for authentication on connected
                    Action ask_auth = new ActionSimple(Constants.A_LOG_IN, gamePIN, player.getName(), Constants.HOST_USERNAME);
                    pubnub.publish().channel(gameID).message( ask_auth ).async(new PNCallback<PNPublishResult>() {
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
                    message.getMessage();
                    Log.v(Constants.LOGT, "PLAYER MESSAGE LISTENER: (" +message.toString() + ")");
                    Gson gson = new Gson();
                    ActionSimple action= gson.fromJson(message.getMessage(), ActionSimple.class);
                    if(action.getRecipient().equals(player.getName()) || action.getRecipient().equals(Constants.A_FOR_ALL) ){
                        if (action.getAction().equals(Constants.A_AUTH_RESPONSE)) {
                            if (action.getValue().equals(Constants.TRUE)) {
                                //IF HOST sends to this user and action is authorisation and auth value is TRUE
                                player.setAuth(true);
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
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      checkImg.setVisibility(View.VISIBLE);
                                                      spinner.setVisibility(View.GONE);
                                                      btnJoin.setVisibility(View.GONE);
                                                      name_field.setVisibility(View.GONE);
                                                      gameID_field.setVisibility(View.GONE);
                                                      gamePIN_field.setVisibility(View.GONE);
                                                      info_field.setText("Authentication Success! The Host will start soon the game");
                                                  }
                                              });
                            } else {
                                //set auth false
                                pubnub.setPresenceState()
                                        .channels(Arrays.asList(gameID))
                                        //.uuid(userName)
                                        .state(player.getState())
                                        .async(new PNCallback<PNSetStateResult>() {
                                            @Override
                                            public void onResponse(final PNSetStateResult result, PNStatus status) {
                                                Log.d(Constants.LOGT, "set auth state false OK");
                                            }
                                        });
                                //client.getPubnub().unsubscribeAll();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinner.setVisibility(View.GONE);
                                        checkImg.setVisibility(View.GONE);
                                        btnJoin.setVisibility(View.VISIBLE);
                                        name_field.setVisibility(View.VISIBLE);
                                        gameID_field.setVisibility(View.VISIBLE);
                                        gamePIN_field.setVisibility(View.VISIBLE);
                                        info_field.setText("Your Authentication failed. Please check all fields and try one more time!");

                                    }
                                });
                            }

                        }else if (action.getAction().equals(Constants.A_START_GAME) && action.getPublisher().equals(Constants.HOST_USERNAME)) {
                            Log.d(Constants.LOGT,"Action Start game got and from HOST");
                                //START GAME
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       info_field.setText("Host started the game. Redirecting finds here!");
                                    }
                                });

                                Intent intent = new Intent(JoinGame.this, PlayerInGame.class);
                                intent.putExtra("gameID", gameID);
                                intent.putExtra("guessTime", Integer.parseInt(action.getValue()));//guestime
                                client.getPubnub().unsubscribeAll();
                                startActivity(intent);
                                finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                //Log.d(Constants.LOGT, "[USER PRESENCE] event "+ presence.getEvent()+
                 //       "state:=" +presence.getState()+ " uuid:= "+ presence.getUuid());
            }
        });


        this.client.subscribe(Constants.NO_PRESENCE);
    }

    @Override
    public void onBackPressed() {
        //Asking the player to quit or something
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you really want to quit?");
        builder.setMessage("This will leave the room");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.getPubnub().unsubscribeAll();
                JoinGame.super.onBackPressed();

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
    protected void onStop() {
        super.onStop();
        if(this.client.getPubnub()!=null){
            this.client.getPubnub().unsubscribeAll();
        }
    }


    //Activity destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.getPubnub().unsubscribeAll();
    }
}
