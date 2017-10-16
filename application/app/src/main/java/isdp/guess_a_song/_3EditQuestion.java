package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import isdp.guess_a_song.controller.AnswersGenerator;
import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Validation;

public class _3EditQuestion extends AppCompatActivity {

    private Spinner spSongSelect;
    private EditText etAnswer[] = new EditText[3];
    private Button btSave;

    private Settings game_settings;
    private ArrayList<Song> songs;
    private ArrayList<Question> questions = new ArrayList<Question>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        // Get game settings and selected songs from previous activity

        Bundle bundle = getIntent().getExtras();

        game_settings = bundle.getParcelable("game_settings");
        songs = bundle.getParcelableArrayList("selected_songs");

        Log.d("_3EditQuestion","Game type now is: " + game_settings.getGame_type());
        DatabaseHandler db = new DatabaseHandler(this);

        /*
        Question generation
         */
        for (final Song song : songs) {
            Log.d("_3EditQuestion",song.toString());
            questions.add(AnswersGenerator.generate(db,song,game_settings.getGame_type()));
        }


        spSongSelect = (Spinner) findViewById(R.id.spSongSelector);



        etAnswer[0] = (EditText) findViewById(R.id.etFirstAnswer);
        etAnswer[1] = (EditText) findViewById(R.id.etSecondAnswer);
        etAnswer[2] = (EditText) findViewById(R.id.etThirdAnswer);
        btSave = (Button) findViewById(R.id.btSave);

        // TODO This is ugly version. later need to put in in method
        /*
        Save user edited answer to object array
         */
        etAnswer[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //SAVE THE DATA
                    Question selected = (Question) spSongSelect.getSelectedItem();
                    selected.getAnswer(0).setText(etAnswer[0].getText().toString());
                }

            }
        });
        etAnswer[1].setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //SAVE THE DATA
                    Question selected = (Question) spSongSelect.getSelectedItem();
                    selected.getAnswer(1).setText(etAnswer[1].getText().toString());
                }

            }
        });
        etAnswer[2].setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //SAVE THE DATA
                    Question selected = (Question) spSongSelect.getSelectedItem();
                    selected.getAnswer(2).setText(etAnswer[2].getText().toString());
                }

            }
        });
        //end of text focus listeners


        //setRandomAnswers((Song) spSongSelect.getSelectedItem());

        //ArrayAdapter<Song> songAdapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, android.R.id.text1, songs);
        //spSongSelect.setAdapter(songAdapter);

        //Changed ArrayadAdapter to Question type
        ArrayAdapter<Question> questionAdapter = new ArrayAdapter<Question>(this, android.R.layout.simple_list_item_1, android.R.id.text1, questions);
        spSongSelect.setAdapter(questionAdapter);

        spSongSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Question selected = (Question) spSongSelect.getSelectedItem();
                setRandomAnswers(selected);//get answers from question object
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate() == true) {

                    // TODO shuffle it in other place to avoid unnecessary loop
                    for (final Question q : questions) {
                       q.shuffle();
                    }

                    // TODO Validate if every song has 3 answers
                    // TODO Change activity, store data in intents, etc.
                    Intent intent = new Intent(_3EditQuestion.this, _4GameRoom.class);

                    //send game settings and game questions (instead of songs) to next activity
                    intent.putExtra("game_settings", game_settings);
                    intent.putParcelableArrayListExtra("questions", questions);

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

    private void setRandomAnswers(Question q) {
        //Set the random generated answers
        // TODO not very nice solution
        int i = 0;
        for (EditText et : etAnswer) {
            if ( q.getAnswer(i).isCorrect() ){
                i++;
            }
            et.setText(q.getAnswer(i).getText());
            i++;
        }
    }

}
