package com.example.noahh_000.starthack.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
public class UserModel
{
    static String TAG = "CurrentUserModel";

    public static void getTranslatorsWithPreferredLanguages(String firstLanguage, String secondLanguage, FindCallback<ParseUser> fcallback)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ArrayList<String> queryLanguages = new ArrayList<String>();
        queryLanguages.add(firstLanguage);
        queryLanguages.add(secondLanguage);
        query.whereContainsAll("languages", queryLanguages);

        Log.v(TAG, "Query for matching users [" + queryLanguages.toString()+"]");

        query.findInBackground(fcallback);
    }

    public static void sendPushNotificationToUserList()
    {

    }
}
