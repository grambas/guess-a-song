package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class GameRoom extends AppCompatActivity {

    private TextView tvGameId;
    private TextView tvPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        tvGameId = (TextView) findViewById(R.id.tvGameId);
        tvPIN = (TextView) findViewById(R.id.tvPIN);

        //tvGameId.setText("Game ID: " + ...); Game ID
        //tvPIN.setText("PIN: " + ...); PIN

        //The grid view will be filled with the profile pictures? of each player
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, HostPlayScreen.class);
        startActivity(intent);
    }
}
