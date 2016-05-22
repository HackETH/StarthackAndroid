package com.example.noahh_000.starthack.models;

import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by NoahH_000 on 05.05.2016.
 */
public class CurrentTranslatorModel extends CurrentApplicationModel{
    public CurrentTranslatorModel()
    {
        super();
        if (this.getRole() != Role.TRANSLATOR)
            ErrorModel.e(TAG, "We initialized a TRANSLATOR but his role in the Database is "+this.getRole().name());
    }

    public void setLanguages(ArrayList<String> languages)
    {
        currentUser.put("languages", languages);
        currentUser.saveInBackground();
    }

    public ArrayList<String> getLanguages()
    {
        ArrayList<String> languages = (ArrayList)currentUser.getList("languages");

        return (languages == null) ? new ArrayList<String>() : languages;
    }
}
