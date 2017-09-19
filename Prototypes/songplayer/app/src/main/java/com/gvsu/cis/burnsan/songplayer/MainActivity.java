package com.gvsu.cis.burnsan.songplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = (Button) findViewById(R.id.playButton);
        Button stopButton = (Button) findViewById(R.id.stopButton);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.crazyforyou);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediaPlayer.seekTo(randomTime(mediaPlayer.getDuration()));
                mediaPlayer.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

    }

    int randomTime(int length){
        Random rand = new Random();
        return rand.nextInt(length);
    }
}
