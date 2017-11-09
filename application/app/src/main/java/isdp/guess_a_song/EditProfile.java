package isdp.guess_a_song;

/* Edit Profile Screen
    Andrew Burns
 */

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import isdp.guess_a_song.model.UserProfile;

public class EditProfile extends Activity {

    EditText nameField = (EditText)findViewById(R.id.profileNameEditText);
    TextView uuidText = (TextView)findViewById(R.id.profileUUIDTextView);
    Button saveButton = (Button)findViewById(R.id.profileSaveButton);
    UserProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //replace with devices userprofile
        profile = new UserProfile();


        nameField.setText(profile.getName());
        uuidText.setText("UUID: " + profile.getUuid());
    }

    private void saveProfile (){ profile.setName(nameField.toString());}

}
