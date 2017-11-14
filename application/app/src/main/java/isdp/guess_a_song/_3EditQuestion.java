package isdp.guess_a_song;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import isdp.guess_a_song.controller.AnswersGenerator;
import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Settings;
import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Constants;
import isdp.guess_a_song.utils.Validation;

public class _3EditQuestion extends AppCompatActivity {

    public Spinner spSongSelect;
    private EditText etAnswer[] = new EditText[4];
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

        //Log.d("_3EditQuestion","HostGame type now is: " + game_settings.getGame_type());
        DatabaseHandler db = new DatabaseHandler(this);

        /*
        Question generation
         */
        for (final Song song : songs) {
            //Log.d("_3EditQuestion",song.toString());
            questions.add(AnswersGenerator.generate(db, song, game_settings.getGame_type()));
        }

        spSongSelect = (Spinner) findViewById(R.id.spSongSelector);
        etAnswer[0] = (EditText) findViewById(R.id.etFirstAnswer);
        etAnswer[1] = (EditText) findViewById(R.id.etSecondAnswer);
        etAnswer[2] = (EditText) findViewById(R.id.etThirdAnswer);
        etAnswer[3] = (EditText) findViewById(R.id.etFourthAnswer);
        btSave = (Button) findViewById(R.id.btSave);

        for (int i = 0; i <= 3; i++) {
            etAnswer[i].addTextChangedListener(myTextWatcher);
        }

//
//        for (int i=0; i <=3;i++){
//            final int i_ = i;
//            etAnswer[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(!hasFocus) {
//                        Log.d(Constants.LOGT, "!hasFocus");
//                        //SAVE THE DATA
//                        Question selected = (Question) spSongSelect.getSelectedItem();
//                        Log.d(Constants.LOGT, "selected="+selected.getAnswer(i_).toString());
//                        selected.getAnswer(i_).setText(etAnswer[i_].getText().toString());
//
//                        //save changes
//                        Log.d(Constants.LOGT, "questions.get="+questions.get(0).getAnswer(i_).toString());
//
//                        //((Question) spSongSelect.getSelectedItem()).getAnswer(i_).setText(etAnswer[i_].getText().toString());
//                    }
//
//                }
//            });
//        }

        //Changed ArrayadAdapter to Question type
        ArrayAdapter<Question> questionAdapter = new ArrayAdapter<Question>(this, android.R.layout.simple_list_item_1, android.R.id.text1, questions);
        spSongSelect.setAdapter(questionAdapter);

        spSongSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Question selected = (Question) spSongSelect.getSelectedItem();
                setAnswersToFields(selected);//get answers from questions object
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate() == true) {

                    Intent intent = new Intent(_3EditQuestion.this, _4GameRoom.class);
                    intent.putExtra("game_settings", game_settings);
                    intent.putParcelableArrayListExtra("questions", questions);
                    startActivity(intent);
                }
            }
        });
    }
    private TextWatcher myTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            //Log.d(Constants.LOGT, "afterTextChanged= " + s.toString());

            if (s == etAnswer[0].getEditableText()) {
                ((Question) spSongSelect.getSelectedItem()).getAnswer(0).setText(etAnswer[0].getText().toString());
            } else if (s == etAnswer[1].getEditableText()) {
                ((Question) spSongSelect.getSelectedItem()).getAnswer(1).setText(etAnswer[0].getText().toString());
            } else if (s == etAnswer[2].getEditableText()) {
                ((Question) spSongSelect.getSelectedItem()).getAnswer(2).setText(etAnswer[2].getText().toString());
            } else if (s == etAnswer[3].getEditableText()) {
                ((Question) spSongSelect.getSelectedItem()).getAnswer(3).setText(etAnswer[3].getText().toString());
            }
        }
    };

    private boolean validate() {
        boolean val = true;
        for (EditText et : etAnswer) {
            if (!Validation.hasText(et)) val = false;
        }
        return val;
    }


    private void setAnswersToFields(Question q) {

        for(int i=0; i<=3;i++){
            if ( q.getAnswer(i).isCorrect() ){
                if(game_settings.getGame_type()== Constants.GAME_TYPE_TITLE){
                    etAnswer[i].setText(q.getSong().getTitle());
                }else{
                    etAnswer[i].setText(q.getSong().getArtist());
                }
            }
            etAnswer[i].setText(q.getAnswer(i).getText());
        }
    }

}
