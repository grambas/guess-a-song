package isdp.guess_a_song.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.Answer;
import isdp.guess_a_song.model.Question;
import isdp.guess_a_song.model.Song;
import isdp.guess_a_song.utils.Constants;

/**
 * Created on 10/9/2017, 2:29 PM
 */


/**
 * This is an answer generator for songs
 *
 * @Author Mindaugas Milius
 */


public class AnswersGenerator {

    /**
     * @param db
     * @param s
     * @param type game type (guess Artist or Title)
     * @return Full Question object with answers
     * @see Question
     */
    public static Question generate(DatabaseHandler db, Song s, int type) {

        Answer temp_ans;

        List<Song> answer_songs = db.getRandomSongs(s);

        ArrayList<Answer> tempAns = new ArrayList<Answer>();
        for (int i = 0; i < 3; i++) {
            if (Constants.DEBUG_MODE){Log.d(Constants.LOGT, answer_songs.get(i).toString());}
            temp_ans = new Answer(answer_songs.get(i).songToAnswer(type), false);
            tempAns.add(temp_ans);
        }

        tempAns.add(new Answer(s.songToAnswer(type), true));
        Question result = new Question(tempAns, type, s);

        return result;
    }
}
