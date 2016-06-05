package com.example.noahh_000.starthack.models;

import android.util.Log;

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
        String fl = currentUser.getString("firstLanguage");
        if (fl == null){
            return null;
        }else{
            return (fl.equals("")) ? null : fl;

        }
    }

    public String getSecondLanguage()
    {
        String sl = currentUser.getString("secondLanguage");
        if(sl == null){
            return null;
        }else{
            return (sl.equals("")) ? null : sl;

        }
    }

    public void setFirstLanguage(String firstLanguage)
    {
        currentUser.put("firstLanguage", firstLanguage);
        currentUser.saveInBackground();
    }

    public void setSecondLanguage(String secondLanguage)
    {
        currentUser.put("secondLanguage", secondLanguage);
        currentUser.saveInBackground();
    }
}
