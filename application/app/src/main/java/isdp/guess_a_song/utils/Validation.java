package isdp.guess_a_song.utils;

import android.util.Log;
import android.widget.EditText;

/**
 * Created on 10/5/2017, 10:20 PM
 */


/**
 * Simple Validation class to check input fields
 */

//TODO maybe add validation to check if input is number

public class Validation {

    private static final String REQUIRED_MSG = "required";

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            Log.d("Validation", "no text");
            return false;
        }
        Log.d("Validation", text);
        return true;
    }
}