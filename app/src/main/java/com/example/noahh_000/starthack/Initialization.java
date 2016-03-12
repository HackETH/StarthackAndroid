package com.example.noahh_000.starthack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

public class Initialization extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseUser.enableAutomaticUser(); // Creates an anonymous user if not existent before, user is always logged in afterwards

        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser.getString("type") == "") // User was not set before
        {
            Intent intentApp = new Intent(this,
                    MainActivity.class);

            this.startActivity(intentApp);

        }
        else if (currentUser.getString("type") == "translator")
        {
            Intent intent = new Intent(this, HelperIntroActivity.class); // Start translator activity
            this.startActivity(intent);

        }

        else if (currentUser.getString("type") == "user")
        {
            Intent intent = new Intent(this, HelpedPickerActivity.class); // Start user activity
            this.startActivity(intent);

        }else{
            Intent intentApp = new Intent(this,
                    MainActivity.class);

            this.startActivity(intentApp);
        }
    }
}
