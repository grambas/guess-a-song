package com.isdp.mindau.multiplayer_prototype;

import android.util.Log;
import android.widget.TextView;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * Created by Mindaugas on 9/19/2017.
 * Class to listen messages on specifig channel
 */

public class Listener extends SubscribeCallback {
    @Override
    public void status(PubNub pubnub, PNStatus status) {
        // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
    }
    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        try {
            //Log all messages
            Log.v(Constants.LOGT, "Message from PubNub to all Subscribers:" + message.toString());
            //Later add Adapter to output info to interface
            //this.Adapter.add(dsMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }
}
