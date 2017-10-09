package isdp.guess_a_song.controller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Answer;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Song;

/**
 * Created on 10/9/2017, 2:29 PM
 */

public class AnswersGenerator {

    public static Question generate(DatabaseHandler db, Song s,int type){
        int r = 0;
        Answer temp_ans;

        List<Song> answer_songs = db.getRandomSongs(s);

        ArrayList<Answer> tempAns = new ArrayList<Answer>();
        for(int i=0;i<3;i++){
            Log.d("AnswersGenerator",answer_songs.get(i).toString());
            temp_ans = new Answer(answer_songs.get(i).songToAnswer(type),false);
            tempAns.add(temp_ans);
        }

        tempAns.add(new Answer(s.songToAnswer(type),true));
        Question result = new Question(tempAns,type,s);

       return result;
    }
}
