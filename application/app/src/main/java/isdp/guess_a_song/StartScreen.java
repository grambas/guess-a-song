package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
    }

    public void hostGame(View v) {
        Intent intent = new Intent(this, _1HostGame.class);
        startActivity(intent);
    }

    public void joinGame(View v) {
        Intent intent = new Intent(this, JoinGame.class);
        startActivity(intent);
    }

    public void spGame(View v) {
        Intent intent = new Intent(this, SingleplayerGame.class);
        startActivity(intent);
    }
    public void MusicLibrary(View v) {
        Intent intent = new Intent(this, MusicLibrary.class);
        startActivity(intent);
    }
}
