package isdp.guess_a_song.utils;

import java.util.Random;


/**
 * Created on 10/6/2017, 12:08 PM
 */

public class Helpers {

    public static int randomInt(int min,int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

}
