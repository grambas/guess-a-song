package isdp.guess_a_song.ui.experimental;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.*;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import isdp.guess_a_song.R;
import isdp.guess_a_song.controller.HostGame;
import isdp.guess_a_song.pubsub.PresenceListAdapter;
import isdp.guess_a_song.ui.playing.HostPlayScreen;
import isdp.guess_a_song.utils.Constants;

public class GameCreationTab extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private HostGame game;
    private PresenceListAdapter mPresence;
    private CountDownTimer timer;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    public void setGame(HostGame game) {
        this.game = game;
    }
    public HostGame getGame() {
        return this.game;
    }

    public PresenceListAdapter getPresenceListAdapter() {
        return mPresence;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_creation_tab);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 2, this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //this.mPresence = new PresenceListAdapter(this);
    }
    void setmPresence(PresenceListAdapter p){
        this.mPresence = p;
    }

    @Override
    public void onBackPressed() {
        //Asking the player to quit or something
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(mediaPlayer.isPlaying() || game.getStatus() == Constants.GAME_STATUS_ON_QUESTION){
            builder.setTitle("Game is on question");
            builder.setMessage("You cannot back during the question.");
        }else{
            builder.setTitle("Do you really want to quit?");
            builder.setMessage("This will disband the room.");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    //More multiplayer stuff (Like closing the room)
                    GameCreationTab.super.onBackPressed();
                }
            });
        }


        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Doing nothing...
                    }
                }
        );
        builder.show();
    }
}
