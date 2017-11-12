package isdp.guess_a_song.controller;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.endpoints.presence.GetState;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import isdp.guess_a_song.model.Action;
import isdp.guess_a_song.model.PresencePojo;
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
    public String gameID;
    private UserProfile user;
    private boolean usePost;

    public PubNubClient(UserProfile user,String gameID, boolean isHost) {

        this.gameID = gameID;
        this.user = user;
        this.usePost = true;

        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(Constants.PKEY);
        config.setSubscribeKey(Constants.SKEY);
        //config.setSecure(true);

        if (isHost){
            config.setUuid(this.user.getName());
        }else{
            config.setUuid(this.user.getUuid());
           // config.setFilterExpression("from == '" + Constants.HOST_USERNAME+"'");
        }

        this.pubnub = new PubNub(config);
        Log.d(Constants.LOGT, user.getName() + " PubNub Client init");

    }

    public String getGameID() {
        return gameID;
    }

    public void publish(Action m, Map<String, Object> meta) {
        try {
            pubnub.publish()
                    .message(m)
                    .channel(gameID)
                    .meta(meta)
                    .shouldStore(true)
                    .usePOST(this.usePost)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            if (status != null && status.isError()) {
                                // something bad happened.
                                Log.d(Constants.LOGT,user.getName()+ " Publish error");
                            } else {
                                //Log.d(Constants.LOGT, user.getName()+ " Published! result: " + result.toString());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(Constants.LOGT, "exception while publishing message", e);
        }
    }
    public void subscribe(boolean withPre){
        if (withPre){
            this.pubnub.subscribe().withPresence().channels( Arrays.asList(gameID) ).execute();
            Log.d(Constants.LOGT, user.getName()+" Subscribed with Presence");
        }else{
            this.pubnub.subscribe().channels( Arrays.asList(gameID) ).execute();
            Log.d(Constants.LOGT, user.getName()+" Subscribed withOUT Presence");

        }
    }

    public final void initChannelsHost(final PresencePnCallback pr) {
        this.pubnub.addListener(pr);

        this.pubnub.subscribe().channels( Arrays.asList(this.gameID) ).withPresence().execute();
        this.pubnub.hereNow().channels( Arrays.asList(this.gameID) ).async(new PNCallback<PNHereNowResult>() {
            @Override
            public void onResponse(PNHereNowResult result, PNStatus status) {
                if (status != null && status.isError()) {
                    return;
                }

                try {
                    Log.d(Constants.LOGT, "HOST HERE NOW" + user.getName()+" hereNow() " +result.toString());

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
    public Map<String, PresencePojo> getRoomPlayers(){

        // Put elements to the map
        final Map<String, PresencePojo> r = new LinkedHashMap<String, PresencePojo>();

        this.pubnub.hereNow()
                // tailor the next two lines to example
                .channels(Arrays.asList(gameID))
                .includeUUIDs(true)
                .includeState(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (status.isError()) {
                            // handle error
                            return;
                        }

                        for (PNHereNowChannelData channelData : result.getChannels().values()) {
                            Log.d(Constants.LOGT,"---");
                            Log.d(Constants.LOGT,"channel:" + channelData.getChannelName());
                            Log.d(Constants.LOGT,"occupancy: " + channelData.getOccupancy());
                            Log.d(Constants.LOGT,"occupants:");
                            for (PNHereNowOccupantData occupant : channelData.getOccupants()) {
                                Log.d(Constants.LOGT,"uuid: " + occupant.getUuid() + " state: " + occupant.getState());
                                r.put(occupant.getUuid(), new PresencePojo(occupant.getUuid(),"",""));
                            }
                        }
                    }
                });
        return r;
    }

    public void setPresenceState(JsonObject state,String uuid){

        this.pubnub.setPresenceState().uuid(uuid)
                //.channels(Arrays.asList(gameID))
                .state(state)
                .async(new PNCallback<PNSetStateResult>() {
                    @Override
                    public void onResponse(final PNSetStateResult result, PNStatus status) {
                    }
                });
    }
    public Map<String, JsonElement> getPresenceState(){
        final Map<String, JsonElement> r = new HashMap<>();
        this.pubnub.getPresenceState()
                .channels(Arrays.asList(this.gameID)) // channels to fetch state for
                .uuid("suchUUID") // uuid of user to fetch, or for own uuid
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        // handle response
                        //r.putAll(result.getStateByUUID());
                    }
                });
        return r;
    }
    public PubNub getPubnub() {
        return pubnub;
    }

    public UserProfile getUser() {
        return user;
    }

    public String getUsername() {
        return this.user.getName();
    }

}