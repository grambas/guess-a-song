package com.isdp.mindau.multiplayer_prototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pubnub.api.*;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.Arrays;

/**
 * Created by Mindaugas on 9/19/2017.
 * Runs demo android app.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init PubNub
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(Constants.SKEY);
        pnConfiguration.setPublishKey(Constants.PKEY);
        PubNub pubnub = new PubNub(pnConfiguration);

        //Output text on display
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText("PubNub connected successfully!\n\n");

        // subscribe to channel
        pubnub.subscribe()
                .channels(Arrays.asList(Constants.ROOM))
                .execute();
        tv.append("Subscribed to room: " + Constants.ROOM + " successfuly !\n\n");


        //Init & Add Listener on subscribed channel
        SubscribeCallback subscribeCallback = new Listener();
        tv.append("Listener created and listening all room messages \n\n");
        pubnub.addListener(subscribeCallback);

        tv.append("Trying to publish message for listeners. Listener should catch it and write to 'my_log' in Android Monitor \n\n");
        pubnub.publish()
                .message(Arrays.asList("user1", "answer1"))
                .channel(Constants.ROOM)
                .shouldStore(true)
                .usePOST(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        TextView tv = (TextView) findViewById(R.id.textView);
                        if (status.isError()) {
                            // something bad happened.
                            System.out.println("error happened while publishing: " + status.toString());
                            tv.append("error happened while publishing: " + status.toString() + "... \n");
                        } else {
                            System.out.println("publish worked! timetoken: " + result.getTimetoken());
                            tv.append("publish worked! timetoken: " + result.getTimetoken()+ "... \n");
                        }
                    }
                });
    }
}
