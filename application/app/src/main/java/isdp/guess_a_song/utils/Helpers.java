package isdp.guess_a_song.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * Created on 10/6/2017, 12:08 PM
 */


/**
 * Simple class to hold miscellaneous help functions
 * and methods
 *
 * @Author Mindaugas Milius
 */
public class Helpers {

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    /**
     * @param min min value for random number
     * @param max max value for random number
     * @return random int number
     */
    public static int randomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    /**
     * @param min min value for random number
     * @param max max value for random number
     * @return random int as string
     */
    public static String randomNumberString(int min, int max) {
        Random r = new Random();
        return String.valueOf(r.nextInt(max - min + 1) + min);
    }

    /**
     * @return get current timestamp in UTC date format
     */
    public static String getTimeStampUtc() {
        return DateTime.now(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTime());
    }

    /**
     * This function needed for PubNub Subscriber
     * channels(List<String>)
     *
     * @param numb
     * @return String as array list item
     */
    public static List<String> numberToStringList(int numb) {
        return Arrays.asList(String.valueOf(numb));
    }

    /**
     * This function needed for PubNub Subscriber
     * channels(List<String>)
     *
     * @param numb
     * @return String as array list item
     */
    public static List<String> numberToStringList(String numb) {
        return Arrays.asList(numb);
    }


    /**
     * Get user ID
     *
     * @param context
     * @return
     */
    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }

        return uniqueID;
    }

    /**
     * Function to put meta information, that
     * the message gonna be sended from Host
     *
     * @return
     */
    public static Map<String, Object> signHostMeta() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("from", Constants.HOST_USERNAME);
        return meta;
    }
}



