package isdp.guess_a_song.ui.experimental;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import isdp.guess_a_song.R;
import isdp.guess_a_song.pubsub.PresenceListAdapter;


/**
 * Created by Maxi on 15.11.2017.
 */

public class PlayerOnlineTab extends Fragment {

    private ListView presenceList;
    private GameCreationTab gameCreationTab;

    public void setGameCreationTab(GameCreationTab gameCreationTab) {
        this.gameCreationTab = gameCreationTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_players, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        presenceList = (ListView) getView().findViewById(R.id.online_list);
        presenceList.setAdapter(gameCreationTab.getPresenceListAdapter());
        TextView title = (TextView) getView().findViewById(R.id.textView7);
        title.setText( "ID: "+gameCreationTab.getGame().getSettings().getGameID()+" PIN: "+gameCreationTab.getGame().getSettings().getGamePIN() );
    }
}
