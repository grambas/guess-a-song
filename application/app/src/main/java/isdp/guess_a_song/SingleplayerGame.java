package isdp.guess_a_song;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Song;

/**
 * A singleplayer version of the game for Sprint #1
 */

public class SingleplayerGame extends AppCompatActivity {

    private int score;
    private Button btAns1;
    private Button btAns2;
    private Button btAns3;
    private Button btAns4;
    private TextView scoreTv;
    private Question question;
    private List<Song> allSongs;


    /*On activity start, initialize the game.*/
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_game);

        btAns1 = (Button) findViewById(R.id.answer1_button);
        btAns2 = (Button) findViewById(R.id.answer2_button);
        btAns3 = (Button) findViewById(R.id.answer3_button);
        btAns4 = (Button) findViewById(R.id.answer4_button);
        scoreTv = (TextView) findViewById(R.id.score_textView);

        DatabaseHandler db = new DatabaseHandler(this);
        allSongs = db.getAllSongs(1);

        btAns1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                guessSong(0);
            }
        });

        btAns2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                guessSong(1);
            }
        });

        btAns3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                guessSong(2);
            }
        });

        btAns4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                guessSong(3);
            }
        });

        score = 0;


        nextQuestion();
    }

    /*Button handlers for each guess*/

    /*Guess function:
    *   1) Check if guess is correct
    *       a) If correct, increment score by one
    *       b) If incorrect, decrement score by one
    *   2) Go to next question
    * */
    private void guessSong(int guess){
        if(guess == question.getCorrect())score++;
        else score--;

        scoreTv.setText(Integer.toString(score));

        nextQuestion();
    }

    /*Next Question function
    *   1) Pick next question at random
    *   2) Update all the answer options
    *   3) Start playing new song
    * */
    private void nextQuestion(){
        //pick next question
        question = pickQuestion();

        //Update answer options
        btAns1.setText(question.getAnswer(0));
        btAns2.setText(question.getAnswer(1));
        btAns3.setText(question.getAnswer(2));
        btAns4.setText(question.getAnswer(3));

        //Start playing new song
        playSong();
    }

    private void playSong(){
        /*Disabled the code in this method because right now there are not actual song files to test with*/

        /*final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(question.getSong().getPath()));
        mediaPlayer.seekTo(randomTime(mediaPlayer.getDuration()));
        mediaPlayer.start();*/
    }

    private int randomTime(int length){
        Random rand = new Random();
        return rand.nextInt(length);
    }

    /*Constructs a new Question with a random, real song from the database*/
    private Question pickQuestion(){
        Random rand = new Random();
        int index = rand.nextInt(allSongs.size());
        return new Question(allSongs.get(index));
    }
}
