package com.example.noahh_000.starthack.models;

import com.parse.ParseUser;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
public class CurrentApplicationModel {
    String TAG = "CurrentUserModel";

    ParseUser currentUser;

    public enum Role {
        TRANSLATOR, USER, UNDECIDED
    }

    public CurrentApplicationModel()
    {
        currentUser = ParseUser.getCurrentUser();
    }

    /* Gezs the users role
     *
     * Will either be one of the roles or an error will occur
     * and will return null
     */
    public Role getRole()
    {
        String role = currentUser.getString("type");
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
        currentUser.put("languages", "");
        currentUser.put("firstLanguage", "");
        currentUser.put("secondLanguage", "");
        currentUser.put("type", "");
    }

    public ParseUser getCurrentUser()
    {
        return this.currentUser;
    }

    public String getTwilioId()
    {
        return currentUser.getString("twilioId");
    }
}
