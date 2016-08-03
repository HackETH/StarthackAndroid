package com.example.noahh_000.starthack.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
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
        query.orderByAscending("timesCalled");
        query.whereContainsAll("languages", queryLanguages);
        query.whereNotEqualTo("pushID", ParseUser.getCurrentUser().get("pushID"));
        query.whereEqualTo("type", "translator");
        query.setLimit(20);

        Log.v(TAG, "Query for matching users [" + queryLanguages.toString()+"]");

        query.findInBackground(fcallback);
    }
}
