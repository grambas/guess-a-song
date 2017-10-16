package isdp.guess_a_song.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


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
     * @return random int number
     */
    public static int randomInt(int min,int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    /**
     *
     * @param min min value for random number
     * @param max max value for random number
     * @return random int as string
     */
    public static String randomNumberString(int min,int max){
        Random r = new Random();
        return String.valueOf(r.nextInt(max - min + 1) + min);
    }

    /**
     *
     * @return get current timestamp in UTC date format
     */
    public static String getTimeStampUtc() {
        return DateTime.now(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTime());
    }

    /**
     *  This function needed for PubNub Subscriber
     *  channels(List<String>)
     * @param numb
     * @return String as array list item
     */
    public static List<String> numberToStringList(int numb){
        return Arrays.asList(String.valueOf(numb));
    }

    /**
     *  This function needed for PubNub Subscriber
     *  channels(List<String>)
     * @param numb
     * @return String as array list item
     */
    public static List<String> numberToStringList(String numb){
        return Arrays.asList(numb);
    }
}



