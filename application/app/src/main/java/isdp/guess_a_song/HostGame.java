package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Validation;

public class HostGame extends AppCompatActivity {

    private Button btNext;
    private RadioGroup radioGroup;
    private int game_type;
    private EditText guess_time;
    private EditText songs_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        btNext = (Button) findViewById(R.id.btStep1Next);
        radioGroup = (RadioGroup) findViewById(R.id.etGameType);
        guess_time = (EditText)findViewById(R.id.etTimeSong);
        songs_amount = (EditText)findViewById(R.id.etNumberQuestions);

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostGame.this, SelectSongs.class);

                int selectedId=radioGroup.getCheckedRadioButtonId();
                int game_type=0;
                if(selectedId==R.id.rbGuessArtist){game_type= Constants.GAME_TYPE_ARTIST;}
                else if(selectedId==R.id.rbGuessTitle){game_type=Constants.GAME_TYPE_TITLE;}


                if ( checkValidation() ){
                    intent.putExtra("game_settings", new Settings(Integer.parseInt(guess_time.getText().toString()),
                            Integer.parseInt(songs_amount.getText().toString()), game_type));

                    startActivity(intent);
                }
                else{
                    Toast.makeText(HostGame.this, "Form contains error", Toast.LENGTH_LONG).show();
                }


                //intent.putExtra("guess_time",Integer.parseInt(guess_time.getText().toString()) );



            }
        });
    }

    public void selectSongs(View view) {
        Intent intent = new Intent(this, SelectSongs.class);
        startActivity(intent);
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(guess_time)) ret = false;
        if (!Validation.validNumber(guess_time)) ret = false;
        if (!Validation.hasText(songs_amount)) ret = false;
        if (!Validation.validNumber(songs_amount)) ret = false;

        return ret;
    }

}
