package isdp.guess_a_song.model;

import java.util.ArrayList;
import java.util.Random;

import isdp.guess_a_song.model.Song;

/**
 * Question model as described in the UML.
 *
 * Needs more work, but this will get us by for now.
 */

public class Question {

    private ArrayList<String> answers;
    private int correct;
    private int type; /* 0 is song, 1 is artist*/
    private Song song;

    /*Constructors*/

    /*All parameters*/
    public Question(ArrayList<String> ans, int cor, int ty, Song son){
        this.answers = ans;
        this.correct = cor;
        this.type = ty;
        this.song = son;
    }

    /*Just the song. Auto builds the question*/
    /*Right now, doesn't generate proper random wrong answers. Will add functionality later.*/
    public Question(Song son){
        Random rand = new Random();
        this.correct = rand.nextInt(4);

        answers = new ArrayList<String>();
        this.answers.add("Wrong Answer");
        this.answers.add("Wrong Answer");
        this.answers.add("Wrong Answer");
        this.answers.add(this.correct, son.getTitle());

        this.type = 0;
        this.song = son;
    }


    /*Getters and setters*/
    public ArrayList<String> getAnswers(){return answers;}

    public String getAnswer(int index){return answers.get(index);}

    public void setAnswer(int index, String newAnswer){this.answers.set(index, newAnswer);}

    public int getCorrect(){return correct;}

    public void setCorrect(int newCorrect){this.correct = newCorrect;}

    public int getType(){return type;}

    public void setType(int newType){this.type = newType;}

    public Song getSong(){return song;}
}
