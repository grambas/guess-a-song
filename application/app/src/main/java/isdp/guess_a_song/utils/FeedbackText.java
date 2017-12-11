package isdp.guess_a_song.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mindau on 08/12/2017.
 * This function gives random Salut for player
 * for guess feedback
 */

public class FeedbackText {
        final static List<String>  correctSalut = Arrays.asList(
                "You were right",
                "Keep going!",
                "Nice try!",
                "Very good!",
                "Awesome!",
                "Well played!"
                );
        final static List<String> wrongSalut = Arrays.asList(
                "You were wrong",
                "Try next time",
                "Nice try!",
                "Sorry...",
                "Back luck :(",
                "Don't give up!"
        );

        public static String getRandom(boolean right){
            if(right){
                return correctSalut.get(Helpers.randomInt(0,correctSalut.size()-1));
            }
        return wrongSalut.get(Helpers.randomInt(0,wrongSalut.size()-1));
        }

}
