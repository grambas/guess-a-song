package isdp.guess_a_song.utils;

/**
 * Created on 10/5/2017, 11:02 PM
 */

/**
 * This is the Game general constants
 *
 * @author Mindaugas Milius
 */

public class Constants {

    public final static String LOGT = "mylogger";
    public final static String HOST_USERNAME = "THE BOSS";


    public final static boolean WITH_PRESENCE = true;
    public final static boolean NO_PRESENCE = false;


    // Game Types. Guess an Artist or guess a title
    public final static int GAME_TYPE_ARTIST = 1;
    public final static int GAME_TYPE_TITLE = 2;

    // Game statuses
    public final static int GAME_STATUS_STARTED = 0;
    public final static int GAME_STATUS_FINISHED = 1;
    public final static int GAME_STATUS_ON_QUESTION = 2;
    public final static int GAME_STATUS_PAUSE = 3;


    // Game ID/KEY random number size
    public final static int RANDOM_MIN = 1000;
    public final static int RANDOM_MAX = 9999;

    //PubNub
    public static final String PKEY = "pub-c-0959a035-d1bc-47fb-92f9-6bfc3b6f01c6";
    public static final String SKEY = "sub-c-bdc45456-9d3d-11e7-96f6-d664df0bd9f6";

}
