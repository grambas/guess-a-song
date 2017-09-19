package com.example.maxi.guess_a_song_prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HostGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
    }

    public void selectSongs(View view) {
        Intent intent = new Intent(this, SelectSongs.class);
        startActivity(intent);
    }
}
