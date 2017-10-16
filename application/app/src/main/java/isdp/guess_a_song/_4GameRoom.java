package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import isdp.guess_a_song.controller.PubNubClient;
import isdp.guess_a_song.model.PubSubPojo;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_wait_room);

        // generate  game ID & PIN
        // FOR TESTING ALWAYS SAME CHANNEL (ROOM)
        // String gameID = Helpers.randomNumberString(Constants.RANDOM_MIN,Constants.RANDOM_MAX);
        gameID = "123456";
        gamePIN = Helpers.randomNumberString(Constants.RANDOM_MIN,Constants.RANDOM_MAX);


        this.tvGameId = (TextView) findViewById(R.id.tvGameId);
        this.tvPIN = (TextView) findViewById(R.id.tvGamePIN);

        this.tvGameId.setText("ID: "+gameID);
        this.tvPIN.setText("PIN: "+gamePIN);

        Button start = (Button) findViewById(R.id.startGame);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });

        /* turned off for TEST instant debbug
        game_settings = getIntent().getExtras().getParcelable("game_settings");
        questions =  getIntent().getParcelableArrayListExtra("questions");
        */


        //The grid view will be filled with the profile pictures? of each player

        this.mPresence = new PresenceListAdapter(this);
        this.mPresencePnCallback = new PresencePnCallback(this.mPresence);

        ListView listView = (ListView) findViewById(R.id.presence_list);
        listView.setAdapter(mPresence);


        client = new PubNubClient(new UserProfile("Mindaugas"),gameID);
        client.initChannels(mPresencePnCallback);
        client.subscribe(gameID,Constants.WITH_PRESENCE);
        client.publish(gameID, new PubSubPojo("sender","msg","timstp"));
    }




    public void startGame(View view) {

        Intent intent = new Intent(this, HostPlayScreen.class);
        //send game settings and game questions (instead of songs) to next activity

        //TODO intent.putParcelableArrayListExtra("players",players);
        //TODO make UserPlayer Parceable to move list to other view

        List<UserProfile> players = new ArrayList<UserProfile>();
        Map<String, PresencePojo> tempor = this.mPresence.getItems();

        for (Map.Entry<String, PresencePojo> entry : tempor.entrySet())
        {
            //Log.d(Constants.LOGT, entry.getKey().toString() + "/" + entry.getValue().toString());
            if (entry.getValue().getPresence() == "join") { //user joined{
                players.add(new UserProfile(entry.getValue().getSender()));
            }
        }


//        for (int i=0;i<this.mPresence.getCount();i++){
//            temp  = this.mPresence.getItem(i);
//            if (temp.getPresence() == "join") { //user joined
//                players.add(new UserProfile(temp.getSender()));
//            }
//        }

        Log.d(Constants.LOGT, "START GAME CLICKER! COLLECTING DATA");
        Log.d(Constants.LOGT, "game_settings && questions are turned off on TEST Activity so skipping...");
        //Log.d(Constants.LOGT, "Questions="+questions.toString());

        Log.d(Constants.LOGT, "GameID="+this.gameID);
        Log.d(Constants.LOGT, "GamePIN="+this.gamePIN);
        Log.d(Constants.LOGT, "Users with event=join"+players.toString());


        /* COMMENTED FOR TESTING BUTTON
        intent.putExtra("gameID", gameID);
        intent.putExtra("gamePIN", gamePIN);
        intent.putExtra("game_settings", game_settings);
        intent.putParcelableArrayListExtra("questions", questions);
        startActivity(intent);
        */
    }

}