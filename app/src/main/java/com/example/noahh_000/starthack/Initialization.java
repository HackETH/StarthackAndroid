package com.example.noahh_000.starthack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class Initialization extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new CallAcceptNotificationOpenedHandler())
                .init();

        setContentView(R.layout.activity_initialization);

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseUser.enableAutomaticUser();
        OneSignal.idsAvailable(
                new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        ParseUser.getCurrentUser().put("pushID", userId);

                    }
                }
        );
        setContentView(R.layout.activity_initialization);
        Log.d("Init", "2");
         // Creates an anonymous user if not existent before, user is always logged in afterwards
        Log.d("Init", "3");
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
        Log.d("init","4");
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
            int i = 5;
            Toast t =  Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT);
            t.show();
        }
    }

    protected void switchToReadyTranslatorActivity()
    {
        Intent intent = new Intent(this, ReadyTranslatorActivity.class); // Start user activity
        this.startActivity(intent);
    }

    protected void switchToConversationActivity()
    {
        Intent intent = new Intent(this, ConversationActivity.class); // Start user activity
        this.startActivity(intent);
    }

    private class CallAcceptNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("Conversation") && additionalData.has("twilioId")) { // TODO Naming
                        String conversationId = additionalData.getString("conversationId");
                        String twilioId = additionalData.getString("twilioId");

                        Log.d("One Signal Push Accept", "ConversationId: " + conversationId + " twilioId:" + twilioId);
                        ParseObject conversation = new ParseObject("Conversation");
                        conversation.get(conversationId);
                        if (conversation.get("translator") == null) // TODO Can this be done?
                        {
                            conversation.put("translator", ParseUser.getCurrentUser().getObjectId());
                            conversation.saveInBackground();
                        }
                        else // if call was already taken
                        {
                            Toast t =  Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG);
                            t.show();
                            switchToReadyTranslatorActivity();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
