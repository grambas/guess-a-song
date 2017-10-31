package isdp.guess_a_song;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.controller.PlayerGame;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.utils.Constants;

public class JoinGame extends AppCompatActivity {

    private EditText gameID_field;
    private EditText gamePIN_field;
    private EditText name_field;
    private TextView info_field;

    Button btnJoin;
    private ProgressBar spinner;

    private PlayerGame game;

    private  String gameID;
    private  String gamePIN;
    private String userName;
    ImageView checkImg;

    private PubNubClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        gameID_field= (EditText)findViewById(R.id.etGameId);
        gamePIN_field = (EditText) findViewById(R.id.etPin);
        name_field = (EditText) findViewById(R.id.etName);
        info_field = (TextView) findViewById(R.id.tvInfo);

        btnJoin = (Button) findViewById(R.id.btJoin);

        checkImg = (ImageView) findViewById(R.id.imageView);

        this.game = new PlayerGame(0,0);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        checkImg.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.GONE);


        btnJoin.setVisibility(View.VISIBLE);
        info_field.setText("Please fill all fields and click Jooin");



    }

    public void join(View view) {

        spinner.setVisibility(View.VISIBLE);

        gameID = gameID_field.getText().toString();
        gamePIN = gamePIN_field.getText().toString();
        userName = name_field.getText().toString();

        spinner=(ProgressBar)findViewById(R.id.progressBar);

        this.client = new PubNubClient(new UserProfile(this.userName),this.gameID);

        // channel subscribed. Now waiting for players.

        if(userName==null){
            this.client.getPubnub().unsubscribeAll();
        }

//        Map<String, JsonElement> r = this.client.getPresenceState();
//
//        for (Map.Entry<String, JsonElement> entry : r.entrySet())
//        {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//            Log.v(Constants.LOGT, "key: (" +entry.getKey() + ") value: "+entry.getValue().toString());
//
//        }




        this.client.getRoomPlayers();

        this.client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    //USER CONNECTED

                    //{ "action": "auth_checl",   "value": "pin...","publisher": "nickname..","recipient": "THE BOSS"}
                    pubnub.publish().channel(gameID).message(new Action(Constants.A_LOG_IN, gamePIN, userName, Constants.HOST_USERNAME) ).async(new PNCallback<PNPublishResult>() {
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
                    if(action.getRecipient().equals(userName)  ){
                        if (action.getAction().equals(Constants.A_AUTH_RESPONSE)) {
                            if (action.getValue().equals(Constants.TRUE)) {

                                JsonObject state = new JsonObject();
                                state.addProperty("is_auth", true);

                                pubnub.setPresenceState()
                                        .channels(Arrays.asList(gameID))
                                        //.uuid(userName)
                                        .state(state)
                                        .async(new PNCallback<PNSetStateResult>() {
                                            @Override
                                            public void onResponse(final PNSetStateResult result, PNStatus status) {
                                                Log.d(Constants.LOGT, "*** state updated");
                                            }
                                        });


                                Log.d(Constants.LOGT, "PLAYER MESSAGE LISTENER: AUTH SUCCESS");
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

                                game.setAuth(1);
                            } else {

                                Log.d(Constants.LOGT, "PLAYER MESSAGE LISTENER: AUTH FALSE");
                                client.getPubnub().unsubscribeAll();

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
                Log.d(Constants.LOGT, "[USER PRESENCE] event "+ presence.getEvent()+
                        "state:=" +presence.getState()+ " uuid:= "+ presence.getUuid());



            }
        });


        this.client.subscribe(this.gameID,Constants.WITH_PRESENCE);


        //Intent for the join screen
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


    private ObjectNode createState() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode payload = factory.objectNode();


            payload.put("auth", 1);
            payload.put("fullname", "Mindaugas");


        return payload;
    }
}
