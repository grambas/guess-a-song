package isdp.guess_a_song;

/* Edit Profile Screen
    Andrew Burns
 */

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import isdp.guess_a_song.model.UserProfile;

public class EditProfile extends AppCompatActivity {

    EditText nameField;
    TextView uuidText;
    UserProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameField = (EditText)findViewById(R.id.profileNameEditText);
        uuidText = (TextView)findViewById(R.id.profileUUIDTextView);

        //replace with devices userprofile
        profile = new UserProfile();
        profile.loadProfile(getApplicationContext());


        nameField.setText(profile.getName());
        uuidText.setText("UUID: " + profile.getUuid());

        final Button saveButton = (Button) findViewById(R.id.profileSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String content = nameField.getText().toString();
                profile.setName(content);
                profile.saveProfile(getApplicationContext());
                Toast.makeText(EditProfile.this, "Profile successfully saved!", Toast.LENGTH_SHORT).show();
                onBackPressed();


            }
        });
    }
}
