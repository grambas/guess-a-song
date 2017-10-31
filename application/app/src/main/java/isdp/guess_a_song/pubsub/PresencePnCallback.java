package isdp.guess_a_song.pubsub;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import isdp.guess_a_song.model.PresencePojo;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

/**
 * Created on 16/10/2017, 18:01 PM
 */

/**
 * This is a Listener witch is triggered every
 * time new player joins or leaves the room
 *
 * @author Mindaugas Milius
 */

public class PresencePnCallback extends SubscribeCallback {
    private final PresenceListAdapter presenceListAdapter;

    public PresencePnCallback(PresenceListAdapter presenceListAdapter) {
        this.presenceListAdapter = presenceListAdapter;
    }
    public PresenceListAdapter getAdapter(){
        return this.presenceListAdapter;
    }
    @Override
    public void status(PubNub pubnub, PNStatus status) {
        // no status handling for simplicity
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        // no message handling for simplicity
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        PresencePojo pm;
        String name = null;
        boolean is_auth = false;
        try {
            Log.v(Constants.LOGT, "gotPresencePnCallback.java : (" +presence.toString() + ")");
            String sender = presence.getUuid();
            String presenceString = presence.getEvent().toString();
            String timestamp = Helpers.getTimeStampUtc();

            if(presence.getState() != null){
                is_auth = presence.getState()
                        .getAsJsonObject()
                        .get("is_auth")
                        .getAsBoolean();
                name = presence.getState().getAsJsonObject().get("name").getAsString();
            }

            pm = new PresencePojo(sender, presenceString, timestamp);
            pm.setAuth(is_auth);
            pm.setName(name);

            Log.d(Constants.LOGT, "HOST PRESENCE= " + pm.toString());
            presenceListAdapter.add(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
