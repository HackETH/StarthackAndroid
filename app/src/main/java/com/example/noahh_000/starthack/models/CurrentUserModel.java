package com.example.noahh_000.starthack.models;

import android.util.Log;

import com.parse.ParseUser;

/**
 * Created by NoahH_000 on 05.05.2016.
 */
public class CurrentUserModel extends CurrentApplicationModel{
    private static String TAG = CurrentUserModel.class.getName();

    public CurrentUserModel()
    {
        super();
        if (this.getRole() != Role.USER)
            ErrorModel.e(TAG, "We initialized a USER but his role in the Database is "+this.getRole().name());
    }

    public String getFirstLanguage()
    {
        String fl = ParseUser.getCurrentUser().getString("firstLanguage");
        if (fl == null){
            return null;
        }else{
            return (fl.equals("")) ? null : fl;

        }
    }

    public String getSecondLanguage()
    {
        String sl = ParseUser.getCurrentUser().getString("secondLanguage");
        if(sl == null){
            return null;
        }else{
            return (sl.equals("")) ? null : sl;

        }
    }

    public void setFirstLanguage(String firstLanguage)
    {
        ParseUser.getCurrentUser().put("firstLanguage", firstLanguage);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public void setSecondLanguage(String secondLanguage)
    {
        ParseUser.getCurrentUser().put("secondLanguage", secondLanguage);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public void setCountry (String country)
    {
        ParseUser.getCurrentUser().put("country", country);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public String getCountry ()
    {
        return ParseUser.getCurrentUser().getString("country");
    }
}
