package isdp.guess_a_song.pubsub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import isdp.guess_a_song.R;
import isdp.guess_a_song.model.PresenceMessageListRowUi;
import isdp.guess_a_song.model.PresencePojo;

/**
 * Created on 16/10/2017, 14:01 PM
 */

/**
 * This is custom adapter to dynamically
 * exchange presence data  from controller
 * class to view class
 *
 * @author Mindaugas Milius
 */



public class PresenceListAdapter extends ArrayAdapter<PresencePojo> {
    private final Context context;
    private final LayoutInflater inflater;

    private final List<String> presenceList = new ArrayList<String>();
    private final Map<String, PresencePojo> latestPresence = new LinkedHashMap<String, PresencePojo>();

    public PresenceListAdapter(Context context) {
        super(context, R.layout.list_row_presence);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    public Map<String, PresencePojo> getItems(){
        return latestPresence;
    }


    @Override
    public void add(PresencePojo message) {
        if (latestPresence.containsKey(message.getSender()) || latestPresence.containsKey(message.getName())) {
            this.presenceList.remove(message.getSender());
        }
        if(!message.getSender().equals("Console_Admin")){
            this.presenceList.add(0, message.getSender());
            latestPresence.put(message.getSender(), message);
        }

        ((Activity) this.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String sender = this.presenceList.get(position);
        PresencePojo presenceMsg = this.latestPresence.get(sender);


        PresenceMessageListRowUi msgView;

        if (convertView == null) {
            msgView = new PresenceMessageListRowUi();

            convertView = inflater.inflate(R.layout.list_row_presence, parent, false);

            msgView.sender = (TextView) convertView.findViewById(R.id.sender);
            msgView.presence = (TextView) convertView.findViewById(R.id.value);
            msgView.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            msgView.auth = (TextView) convertView.findViewById(R.id.auth);


            convertView.setTag(msgView);
        } else {
            msgView = (PresenceMessageListRowUi) convertView.getTag();
        }
        msgView.sender.setText(presenceMsg.getSenderOrName());
        msgView.presence.setText(presenceMsg.getPresence());
        msgView.timestamp.setText(presenceMsg.getTimestamp());
        msgView.auth.setText(String.valueOf(presenceMsg.isAuth()));

        return convertView;
    }

    @Override
    public int getCount() {
        return this.presenceList.size();
    }

    public void clear() {
        this.presenceList.clear();
        this.latestPresence.clear();
        notifyDataSetChanged();
    }
}
