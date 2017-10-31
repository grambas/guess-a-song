package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Map;

import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.pubsub.PresenceListAdapter;
import isdp.guess_a_song.pubsub.PresencePnCallback;
import isdp.guess_a_song.model.PresencePojo;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;


public class _4GameRoom extends AppCompatActivity {

    private String gameID;
    private String gamePIN;

    private TextView tvGameId;
    private TextView tvPIN;

    private Settings game_settings;
    private ArrayList<Question> questions;
    private ArrayList<UserProfile> players = new ArrayList<UserProfile>();

    //PUBNUB
    private PubNubClient client;
    private PresenceListAdapter mPresence;
    private PresencePnCallback mPresencePnCallback;


    /**
     * Create gameID and gamePIN. We do not create main game instance here due to avoid
     * unnecessary object transfer in Activities.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //INIT AND GETTER

        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_wait_room);

        //gui objects
        this.tvGameId = (TextView) findViewById(R.id.tvGameId);
        this.tvPIN = (TextView) findViewById(R.id.tvGamePIN);
        Button start = (Button) findViewById(R.id.startGame);

        //game data
        this.game_settings = getIntent().getExtras().getParcelable("game_settings");
        questions = getIntent().getParcelableArrayListExtra("questions");

        //pubnub
        this.mPresence = new PresenceListAdapter(this);
        this.mPresencePnCallback = new PresencePnCallback(this.mPresence);
        ListView listView = (ListView) findViewById(R.id.presence_list);


        this.gameID = Constants.DEMO_CHANNEL;
        this.gamePIN = Helpers.randomNumberString(Constants.RANDOM_MIN,Constants.RANDOM_MAX);

        //client
        client = new PubNubClient(new UserProfile(Constants.HOST_USERNAME),this.gameID);



        client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){

//                    pubnub.publish().channel("123456").message("status.getCategory() == PNStatusCategory.PNConnectedCategory").async(new PNCallback<PNPublishResult>() {
//                        @Override
//                        public void onResponse(PNPublishResult result, PNStatus status) {
//                            // handle publish response
//                        }
//                    });
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                Log.d(Constants.LOGT, "HOST MESSAGE LISTENER "+ message.getMessage().toString());

                Gson gson = new Gson();
                Action action= gson.fromJson(message.getMessage(), Action.class);
                //{ "action": "log_in",   "value": "4852","publisher": "TestPlayer1","recipient": "THE BOSS"}
                if (action.getRecipient() != null && action.getRecipient().equals(Constants.HOST_USERNAME)){
                    if(action.getAction().equals(Constants.A_LOG_IN)){
                        Log.d(Constants.LOGT, "Action value=  "+ action.getValue() + " host pin="+gamePIN);

                        if(action.getValue().equals(gamePIN)){
                            Log.d(Constants.LOGT, "HOST MESSAGE LISTENER Player "+ action.getPublisher() + " auth true SUCCESS!");
                            client.publish(gameID,new Action(Constants.A_AUTH_RESPONSE,Constants.TRUE,Constants.HOST_USERNAME,action.getPublisher()));
                        }else{
                            client.publish(gameID,new Action(Constants.A_AUTH_RESPONSE,Constants.FALSE,Constants.HOST_USERNAME,action.getPublisher()));
                            Log.d(Constants.LOGT, "HOST MESSAGE LISTENER Player "+ action.getPublisher() + " auth false (bad pin)");
                        }
                    }
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });





        //SETTER


        // generate  game ID & PIN
        // FOR TESTING ALWAYS SAME CHANNEL (ROOM)
        // String gameID = Helpers.randomNumberString(Constants.RANDOM_MIN,Constants.RANDOM_MAX);


        //set view fields with data
        this.tvGameId.setText("ID: "+gameID);
        this.tvPIN.setText("PIN: "+gamePIN);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });


        this.game_settings.setGameID(123456);
        this.game_settings.setGamePIN(Integer.parseInt(this.gamePIN));

        listView.setAdapter(mPresence);

        client.initChannelsHost(mPresencePnCallback);
        client.subscribe(gameID,Constants.WITH_PRESENCE);
        // channel subscribed. Now waiting for players.

        //test publish
        //client.publish(gameID, new PubSubPojo("sender","msg","timstp"));
    }




    public void startGame(View view) {

        Intent intent = new Intent(this, HostPlayScreen.class);
        //send game settings and game questions (instead of songs) to next activity

        //fetch all logged in users to list
        Map<String, PresencePojo> tempor = this.mPresence.getItems();
        for (Map.Entry<String, PresencePojo> entry : tempor.entrySet())
        {
            //Log.d(Constants.LOGT, entry.getKey().toString() + "/" + entry.getValue().toString());
            //skip Console_Admin also
            if (entry.getValue().getPresence() == "join" && !entry.getValue().getSender().equals("Console_Admin")) { //user joined{
                players.add(new UserProfile(entry.getValue().getSender()));
            }
        }

//        PresencePojo temp;
//        for (int i=0;i<this.mPresence.getCount();i++){
//            temp  = this.mPresence.getItem(i);
//            if (temp.getPresence() == "join") { //user joined
//                players.add(new UserProfile(temp.getSender()));
//            }
//        }

        Log.d(Constants.LOGT, "START GAME CLICKER! COLLECTING DATA");
        Log.d(Constants.LOGT, "Questions="+questions.toString());
        Log.d(Constants.LOGT, "Users with event=join"+players.toString());

        intent.putExtra("game_settings", game_settings);

        Bundle b = new Bundle();
        b.putParcelableArrayList("questions", questions); // Be sure questions is not null here
        b.putParcelableArrayList("players", players); // Be sure players is not null here
        intent.putExtras(b);

        startActivity(intent);

    }

}
