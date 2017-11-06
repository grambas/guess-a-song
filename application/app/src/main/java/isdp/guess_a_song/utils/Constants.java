package isdp.guess_a_song.utils;

/**
 * Created on 10/5/2017, 11:02 PM
 */

/**
 * This is the HostGame general constants
 *
 * @author Mindaugas Milius
 */

public class Constants {


    //messages

    public final static String TRUE = "true";
    public final static String FALSE = "false";
    public final static String A_LOG_IN  = "log_in";
    public final static String A_AUTH_RESPONSE = "auth_check";
    public final static String A_START_GAME = "start_game";
    public final static String A_ASK = "ask";
    public final static String A_ANSWER = "answer";
    public final static String A_FOR_ALL = "all";



    public final static String DEFAULT_SONGS_NUMBER = "2";
    public final static String DEFAULT_GUESS_TIME = "15";

    public final static int REWARD_CORRECT = 2;
    public final static int REWARD_WRONG = -1;



    public final static String LOGT = "mylogger";
    public final static String HOST_USERNAME = "THE BOSS";
    public final static String DEMO_CHANNEL = "111111";


    public final static boolean WITH_PRESENCE = true;
    public final static boolean NO_PRESENCE = false;


    // HostGame Types. Guess an Artist or guess a title
    public final static int GAME_TYPE_ARTIST = 1;
    public final static int GAME_TYPE_TITLE = 2;

    // HostGame statuses
    public final static int GAME_STATUS_STARTED = 0;
    public final static int GAME_STATUS_FINISHED = 1;
    public final static int GAME_STATUS_ON_QUESTION = 2;
    public final static int GAME_STATUS_PAUSE = 3;
    public final static int GAME_STATUS_TIME_OVER = 4;
    public final static int GAME_STATUS_READY = 5;


    // HostGame ID/KEY random number size
    public final static int RANDOM_MIN = 1000;
    public final static int RANDOM_MAX = 9999;

    //PubNub
    public static final String PKEY = "pub-c-0959a035-d1bc-47fb-92f9-6bfc3b6f01c6";
    public static final String SKEY = "sub-c-bdc45456-9d3d-11e7-96f6-d664df0bd9f6";

}
