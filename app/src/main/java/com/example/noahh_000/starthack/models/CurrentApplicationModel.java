package com.example.noahh_000.starthack.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
public class CurrentApplicationModel {
    String TAG = "CurrentUserModel";
    public enum Role {
        TRANSLATOR, USER, UNDECIDED
    }

    public CurrentApplicationModel()
    {

    }

    public void deletUsersWithSamePushId()
    {
        try {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            String pushId = ParseUser.getCurrentUser().get("pushID").toString();
            query.whereEqualTo("pushID", pushId);
            query.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> userList, com.parse.ParseException e) {
                    if (e == null && userList != null) {
                        for (ParseUser user : userList)
                            if (!user.equals(ParseUser.getCurrentUser())) {
                                user.put("type", "duplicate");
                                user.saveInBackground();
                            }
                    }
                }
            });
        } catch (Exception e)
        {
            Log.e("", e.toString());
        }
    }

    /* Gezs the users role
     *
     * Will either be one of the roles or an error will occur
     * and will return null
     */
    public Role getRole()
    {
        String role = ParseUser.getCurrentUser().getString("type");
        if(role == null || role.equals("")){
            return Role.UNDECIDED;
        }
        else if (role.equals( "translator"))
        {
            return Role.TRANSLATOR;
        }

        else if (role.equals("user"))
        {
            return Role.USER;
        }
        else{
            ErrorModel.e(TAG, "Unknown role picked: "+role);
            return null;
        }
    }

    public void reset()
    {
        ParseUser.getCurrentUser().put("languages", new ArrayList<String>());
        ParseUser.getCurrentUser().put("firstLanguage", "");
        ParseUser.getCurrentUser().put("secondLanguage", "");
        ParseUser.getCurrentUser().put("type", "");
        ParseUser.getCurrentUser().saveInBackground();
    }

    public String getTwilioId()
    {
        return ParseUser.getCurrentUser().getString("twilioIdentity");
    }
}
