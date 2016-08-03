package com.example.noahh_000.starthack.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseUser;
import com.twilio.client.Connection;
import com.twilio.client.Device;

/**
 * Created by samuelmueller on 28.07.16.
 */
public class TranslatorAudioCallActivity extends AudioCallActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser user = ParseUser.getCurrentUser();
        clientProfile = new ClientProfile(user.getObjectId(), true, false);

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
    }

    @Override
    protected void deviceCreated() {
        super.deviceCreated();
        Intent intent = getIntent();
        String callThisId = intent.getStringExtra("reachHimHere");
        connect(callThisId,false);
    }

}
