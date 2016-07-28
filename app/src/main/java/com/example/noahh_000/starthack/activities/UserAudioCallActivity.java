package com.example.noahh_000.starthack.activities;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.ErrorModel;
import com.example.noahh_000.starthack.models.UserModel;
import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.twilio.client.Connection;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by samuelmueller on 28.07.16.
 */
public class UserAudioCallActivity extends AudioCallActivity{
    private ParseObject currentConversation;
    private CurrentUserModel currentUserModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser user = ParseUser.getCurrentUser();
        clientProfile = new ClientProfile(user.getObjectId(), false, true);
        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        } else {
            /*
             * Initialize the Twilio Client SDK
             */
            initializeTwilioClientSDK();
        }
        currentUserModel = new CurrentUserModel();

        currentConversation = new ParseObject("Conversations");
        currentConversation.put("user", ParseUser.getCurrentUser());
        try {
            currentConversation.save();
        } catch (Exception e){
            ErrorModel.e(TAG, "error saving conversation"+e);
        }
        String id = currentConversation.getObjectId();
        // Get a list of users with the languages that are spoken by one self
        // and send all of them a push notification
        UserModel.getTranslatorsWithPreferredLanguages(
                currentUserModel.getFirstLanguage()
                , currentUserModel.getSecondLanguage()
                , new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> helperList, ParseException e) {
                        if (e == null) { // Successfully fetched Users
                            Log.d("callloadingactivity", "Query Result: " + helperList.toString());
                            contactUserListCallback(helperList);
                        } else {
                            Log.d("callloadingactivity", "callloadingactivity error in query " + e.getMessage());
                        }
                    }
                });

    }
    private void contactUserListCallback(List<ParseUser> helperList)
    {
        if (helperList.size() == 0) { // No users were found
            contactUserListEmpty();
        } else { // Some users were found
            contactUserListFull(helperList);
        }
    }
    private void contactUserListEmpty()
    {
        Context context = getApplicationContext();
        CharSequence text = "Sorry there are no matching users.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    private void contactUserListFull(List<ParseUser> helperList)
    {
        // Convert User List to User Array
        String[] helperArray = new String[helperList.size()];
        int i = 0;
        for (ParseUser partner : helperList) {
            helperArray[i] = partner.getString("pushID");
            i++;
        }

        String conversationId = currentConversation.getObjectId();
        String twilioId = currentUserModel.getTwilioId();
        String strhelperlist = new String("");
        // For each matching user send out a push notification to that conversation
        for (String helper : helperArray)
        {
            if (helper != null && conversationId != null) {
                strhelperlist = strhelperlist  + ",'" + helper+"'";

            }
        }
        strhelperlist = strhelperlist.substring(1);
        try {
            String jsonstring = "{" +
                    "'contents': {'en':'Someone needs your help in audio! Open the app now to translate.'}" +
                    ", 'data': {" +
                    "'conversationId': '" + conversationId + "'" +
                    ", 'twilioId':'" + twilioId + "'" +
                    ", 'reachMeHere':'" + ParseUser.getCurrentUser().getObjectId() +"'}" +
                    ", 'include_player_ids': [" + strhelperlist + "]" +
                    "}";
            OneSignal.postNotification(new JSONObject(jsonstring), null);
        } catch (Exception err) {
            err.printStackTrace();
        }

    }
}
