package com.example.noahh_000.starthack;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.NotificationExtenderService;
import java.math.BigInteger;

import com.parse.Parse;
import com.parse.ParseUser;

public class NotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationPayload notification) {
        try {
            ParseUser user = ParseUser.getCurrentUser();
            user.increment("timesCalled");
            user.saveEventually();
            if (notification.additionalData != null && notification.additionalData.getBoolean("deleteAll")) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                return true;
            }else{
                return false;
            }
        }catch (org.json.JSONException e){
            return false;
        }
    }
}