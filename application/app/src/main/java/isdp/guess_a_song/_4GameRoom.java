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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.ActionSimple;
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
    private boolean isStarted;

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
        isStarted = false;
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
        final UserProfile host = new UserProfile(Constants.HOST_USERNAME,null,true,true);
        host.loadProfile(getApplicationContext());
        client = new PubNubClient(host,this.gameID,true);



        client.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status != null && status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    //set auth state true for host automatically
                    pubnub.setPresenceState()
                            .channels(Arrays.asList(gameID))
                            //.uuid(userName)
                            .state(host.getState())
                            .async(new PNCallback<PNSetStateResult>() {
                                @Override
                                public void onResponse(final PNSetStateResult result, PNStatus status) {}
                            });

                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                String action = null;
                String recipient=null;
                JsonObject obj = null;
                JsonElement actionElm;
                JsonElement recipientElm;
                Gson gson = new Gson();

                try {
                    obj = message.getMessage().getAsJsonObject();
                    actionElm = obj.get("action");
                    action = actionElm.getAsString();
                    recipientElm = obj.get("recipient");
                    recipient = recipientElm.getAsString();
                    Log.d(Constants.LOGT, "_4GameRoom Listener: got ACTION:" + action);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(recipient.equals(Constants.HOST_USERNAME)){
                    Log.d(Constants.LOGT, recipient+"=="+Constants.HOST_USERNAME);
                }else{
                    Log.d(Constants.LOGT, recipient+"!="+Constants.HOST_USERNAME);
                }
                //IF MSG IS LOGIN REQUEST
                if (recipient.equals(Constants.HOST_USERNAME)) {
                    //IF PIN IS GOOD
                    if (action.equals(Constants.A_LOG_IN)) {
                        ActionSimple msgLogIn = gson.fromJson(message.getMessage(), ActionSimple.class);
                        if (msgLogIn.getValue().equals(gamePIN)) {
                            if (isStarted) {
                                client.publish(new ActionSimple(Constants.A_START_GAME, String.valueOf(game_settings.getGuess_time()), Constants.HOST_USERNAME, msgLogIn.getPublisher()), Helpers.signHostMeta());
                                Log.d(Constants.LOGT, "_4GameRoom Listener: "+ msgLogIn.getPublisher() + " auth true + direct redirect to game)");
                            } else {
                                client.publish(new ActionSimple(Constants.A_AUTH_RESPONSE, Constants.TRUE, Constants.HOST_USERNAME, msgLogIn.getPublisher()), Helpers.signHostMeta());
                                Log.d(Constants.LOGT, "_4GameRoom Listener: "+ msgLogIn.getPublisher() + " auth true + wait for start)");
                            }
                        //IF PIN IS BAD
                        }else{
                            client.publish(new ActionSimple(Constants.A_AUTH_RESPONSE,Constants.FALSE,Constants.HOST_USERNAME,msgLogIn.getPublisher()),Helpers.signHostMeta());
                            Log.d(Constants.LOGT, "_4GameRoom Listener: "+ msgLogIn.getPublisher() + " auth false (bad pin)");
                        }
                    }

                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        //set view fields with data
        this.tvGameId.setText("ID: "+gameID);
        this.tvPIN.setText("PIN: "+gamePIN);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startGame(v); }
        });


        this.game_settings.setGameID(Integer.parseInt(Constants.DEMO_CHANNEL));
        this.game_settings.setGamePIN(Integer.parseInt(this.gamePIN));

        listView.setAdapter(mPresence);
        client.initChannelsHost(mPresencePnCallback);
        client.subscribe(Constants.WITH_PRESENCE);
    }




    public void startGame(View view) {
        isStarted = true;
        Intent intent = new Intent(this, HostPlayScreen.class);
        UserProfile u_temp;
        //send game settings and game questions (instead of songs) to next activity

        //fetch all logged in users to list
        Map<String, PresencePojo> tempor = this.mPresence.getItems();
        for (Map.Entry<String, PresencePojo> entry : tempor.entrySet())
        {
            //skip if not auth
            //skip Console_Admin also
            //skip ghost
            if (entry.getValue().isAuth()
                    && !entry.getValue().getSender().equals("Console_Admin")
                    && !entry.getValue().getSender().equals(Constants.HOST_USERNAME)) {
                u_temp = new UserProfile(entry.getValue().getName(),entry.getValue().getSender(),entry.getValue().isAuth(),false);
                players.add(u_temp);
            }
        }
        client.publish(new ActionSimple(Constants.A_START_GAME,String.valueOf(game_settings.getGuess_time()),Constants.HOST_USERNAME,Constants.A_FOR_ALL),Helpers.signHostMeta());

        Log.d(Constants.LOGT, "START GAME CLICKED! COLLECTING DATA");
        Log.d(Constants.LOGT, "Questions= "+questions.toString());
        Log.d(Constants.LOGT, "Players= "+players.toString());

        intent.putExtra("game_settings", game_settings);

        Bundle b = new Bundle();
        b.putParcelableArrayList("questions", questions); // Be sure questions is not null here
        b.putParcelableArrayList("players", players); // Be sure players is not null here
        intent.putExtras(b);
        startActivity(intent);
    }


}
