package com.example.noahh_000.starthack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;
import com.onesignal.OneSignal;


public class Initialization extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();
        setContentView(R.layout.activity_initialization);

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseUser.enableAutomaticUser(); // Creates an anonymous user if not existent before, user is always logged in afterwards

        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser.getString("type") == null){
            Intent intentApp = new Intent(this,
                    MainActivity.class);

            this.startActivity(intentApp);
        }
        else if (currentUser.getString("type").equals("")) // User was not set before
        {
            Intent intentApp = new Intent(this,
                    MainActivity.class);

            this.startActivity(intentApp);

        }
        else if (currentUser.getString("type").equals( "translator"))
        {
            if (currentUser.getList("languages").isEmpty()){
                Intent intent = new Intent(this, HelperIntroActivity.class); // Start translator activity
                this.startActivity(intent);
            }else{
                Intent intent = new Intent(this, ReadyTranslatorActivity.class);
                this.startActivity(intent);
            }


        }

        else if (currentUser.getString("type").equals("user"))
        {
            Intent intent = new Intent(this, HelpedMain.class); // Start user activity
            this.startActivity(intent);

        }else{
            Toast t =  Toast.makeText(getApplicationContext(),"Error",5);
            t.show();
        }
    }
}
