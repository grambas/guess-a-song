package isdp.guess_a_song.ui.experimental;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;

import isdp.guess_a_song.pubsub.PresenceListAdapter;

/**
 * Created by Maxi on 15.11.2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    private GameCreationTab gameCreationTab;
    private PresenceListAdapter presenceListAdapter;
    protected MusicPlayerTab musicPlayerTab;
    protected PlayerOnlineTab playerOnlineTab;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, GameCreationTab gameCreationTab) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.gameCreationTab = gameCreationTab;
    }

    public void setPresenceListAdapter(PresenceListAdapter presenceListAdapter) {
        this.presenceListAdapter = presenceListAdapter;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                musicPlayerTab = new MusicPlayerTab();
                musicPlayerTab.setGameCreationTab(gameCreationTab);
                return musicPlayerTab;
            case 1:
                playerOnlineTab = new PlayerOnlineTab();
                playerOnlineTab.setGameCreationTab(gameCreationTab);
                return playerOnlineTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Music player";
            case 1:
                return "Online players";
            default:
                return null;
        }
    }
}
