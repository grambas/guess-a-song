package isdp.guess_a_song.controller;

import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

import java.util.Arrays;
import java.util.Map;

import isdp.guess_a_song.model.PresencePojo;
import isdp.guess_a_song.model.PubSubPojo;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.pubsub.PresencePnCallback;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Helpers;

/**
 * Created on 16/10/2017, 15:18 PM
 */


/**
 * This is Wrapper from PubNub APi Framework
 * @Author Mindaugas Milius
 */

public class PubNubClient{

    private PubNub pubnub;
    private String gameID;
    private UserProfile user;
    private boolean usePost;

    public PubNubClient(UserProfile user,String gameID) {

        this.gameID = gameID;
        this.user = user;
        this.usePost = true;

        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(Constants.PKEY);
        config.setSubscribeKey(Constants.SKEY);
        config.setUuid(this.user.getName());
        //config.setSecure(true);
        this.pubnub = new PubNub(config);
        Log.d(Constants.LOGT, user.getName() + " PubNub Client init");

    }


    public void publish(final String gameID, PubSubPojo m) {
        try {
        pubnub.publish()
                .message(m)
                .channel(gameID)
                .shouldStore(true)
                .usePOST(this.usePost)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            // something bad happened.
                            Log.d(Constants.LOGT,user.getName()+ " Publish error");

                        } else {
                           Log.d(Constants.LOGT, user.getName()+ " Published! result: " + result.toString());
                        }
                    }
                });
        } catch (Exception e) {
            Log.e(Constants.LOGT, "exception while publishing message", e);
        }
    }

    public void subscribe(String gameID,boolean withPre){
        if (withPre){
            this.pubnub.subscribe().withPresence().channels( Arrays.asList(gameID) ).execute();
            Log.d(Constants.LOGT, user.getName()+" Subscribed with Presence");
        }else{
            this.pubnub.subscribe().channels( Arrays.asList(gameID) ).execute();
            Log.d(Constants.LOGT, user.getName()+" Subscribed withOUT Presence");

        }
    }

    public final void initChannels(final PresencePnCallback pr) {
        this.pubnub.addListener(pr);

        this.pubnub.subscribe().channels( Arrays.asList(this.gameID) ).withPresence().execute();
        this.pubnub.hereNow().channels( Arrays.asList(this.gameID) ).async(new PNCallback<PNHereNowResult>() {
            @Override
            public void onResponse(PNHereNowResult result, PNStatus status) {
                if (status.isError()) {
                    return;
                }

                try {
                    Log.d(Constants.LOGT, user.getName()+" hereNow() " +result.toString());

                    for (Map.Entry<String, PNHereNowChannelData> entry : result.getChannels().entrySet()) {
                        for (PNHereNowOccupantData occupant : entry.getValue().getOccupants()) {
                            pr.getAdapter().add(new PresencePojo(occupant.getUuid(), "join", Helpers.getTimeStampUtc()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}