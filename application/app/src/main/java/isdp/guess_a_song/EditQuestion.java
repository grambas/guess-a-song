package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Validation;

public class EditQuestion extends AppCompatActivity {

    private Spinner spSongSelect;
    private EditText etAnswer[] = new EditText[3];
    private Button btSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        List<Song> songs = (List) getIntent().getExtras().getParcelableArrayList("selected_songs");

        spSongSelect = (Spinner) findViewById(R.id.spSongSelector);
        etAnswer[0] = (EditText) findViewById(R.id.etFirstAnswer);
        etAnswer[1] = (EditText) findViewById(R.id.etSecondAnswer);
        etAnswer[2] = (EditText) findViewById(R.id.etThirdAnswer);
        btSave = (Button) findViewById(R.id.btSave);

        setRandomAnswers((Song) spSongSelect.getSelectedItem());

        ArrayAdapter<Song> songAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, android.R.id.text1, songs);
        spSongSelect.setAdapter(songAdapter);

        spSongSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRandomAnswers((Song) spSongSelect.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate() == true) {
                    //Validate if every song has 3 answers
                    //Change activity, store data in intents, etc.
                    Intent intent = new Intent(EditQuestion.this, GameRoom.class);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean validate() {
        boolean val = true;
        for (EditText et : etAnswer) {
            if (!Validation.hasText(et)) val = false;
        }
        return val;
    }

    private void setRandomAnswers(Song song) {
        //Set the random generated answers
        for (EditText et : etAnswer) {
            //et.setText(AnswerGenerator.getRandomAnswersToSong((Song) spSongSelect.getSelectedItem())); or something like this
        }
    }

}
