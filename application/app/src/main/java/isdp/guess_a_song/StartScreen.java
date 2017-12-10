package isdp.guess_a_song;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import isdp.guess_a_song.db.DatabaseHandler;
import isdp.guess_a_song.model.UserProfile;
import isdp.guess_a_song.ui.EditProfile;
import isdp.guess_a_song.ui.JoinGame;
import isdp.guess_a_song.ui.MusicLibrary;
import isdp.guess_a_song.ui.experimental.GameCreationTab;
import isdp.guess_a_song.ui.roomcreation._1HostGame;
import isdp.guess_a_song.ui.roomcreation._4GameRoom;

public class StartScreen extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);



        /********************************************
         * Check and Grand storage access permission
         *******************************************/
        if (ContextCompat.checkSelfPermission(StartScreen.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartScreen.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(StartScreen.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(StartScreen.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        UserProfile user = new UserProfile();
        user.loadProfile(getApplicationContext());
        if (!user.isNameChanged()) {
            Toast.makeText(StartScreen.this, "Please change your name before start", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, EditProfile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }


    }
    /**
     * Storage permission stuff
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (ContextCompat.checkSelfPermission(StartScreen.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    //SYNC DB SONGS
                    ContentResolver contentResolver = getContentResolver();
                    DatabaseHandler db = new DatabaseHandler(this);
                    MusicLibrary.syncSongToDB(contentResolver,db);
                } else {
                    Toast.makeText(this, "No Permission Granted. Restart App!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
    public void hostGame(View v) {
        Intent intent = new Intent(this, _1HostGame.class);
        startActivity(intent);
    }

    public void joinGame(View v) {
        Intent intent = new Intent(this, JoinGame.class);
        startActivity(intent);
    }

    public void MusicLibrary(View v) {
        Intent intent = new Intent(this, MusicLibrary.class);
        startActivity(intent);
    }
    public void GameRoomActivity(View v) {
        Intent intent = new Intent(this, _4GameRoom.class);
        startActivity(intent);
    }

    public void profile(View v) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }
}
