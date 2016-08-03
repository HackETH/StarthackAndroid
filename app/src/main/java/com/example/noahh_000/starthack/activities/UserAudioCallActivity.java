package com.example.noahh_000.starthack.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.ErrorModel;
import com.example.noahh_000.starthack.models.UserModel;
import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.twilio.client.Connection;
import com.twilio.client.impl.sound.SoundPool;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by samuelmueller on 28.07.16.
 */
public class UserAudioCallActivity extends AudioCallActivity{
    private ParseObject currentConversation;
    private CurrentUserModel currentUserModel;
    private List<ParseUser> helpers;
    private MediaPlayer mPlayer;
    int streamId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser user = ParseUser.getCurrentUser();
        //clientProfile = new ClientProfile("Hans", true, true);
        clientProfile = new ClientProfile(user.getObjectId(), true, true);
        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager.setMode(AudioManager.STREAM_MUSIC);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setStreamVolume (AudioManager.STREAM_MUSIC,10,0);

        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep); // in 2nd param u have to pass your desire ringtone
        //mPlayer.prepare();

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.start();
        mPlayer.setLooping(true);

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
                            helpers = helperList;
                        } else {
                            Log.d("callloadingactivity", "callloadingactivity error in query " + e.getMessage());
                        }
                    }
                });


    }
    @Override
    public void onConnecting(Connection connection) {
        super.onConnecting(connection);
        mPlayer.stop();
        String[] helperArray = new String[helpers.size()];
        int i = 0;
        for (ParseUser partner : helpers) {
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
                    ", 'deleteAll':true}" +
                    ", 'include_player_ids': [" + strhelperlist + "]" +
                    ", 'content_available': true"+
                    "}";
            OneSignal.postNotification(new JSONObject(jsonstring), null);
        } catch (Exception err) {
            err.printStackTrace();
        }
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
    @Override
    protected void leavingWindow(){
        if (mPlayer.isPlaying()){
            mPlayer.stop();
        }
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
