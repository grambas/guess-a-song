package isdp.guess_a_song.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import isdp.guess_a_song.model.Song;


/**
 * Created on 10/6/2017, 12:08 PM
 */


/**
 * Simple class to hold miscellaneous help functions
 * and methods
 * @Author Mindaugas Milius
 */
public class Helpers {
    /**
     *
     * @param min min value for random number
     * @param max max value for random number
     * @return random number
     */
    public static int randomInt(int min,int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }


}
