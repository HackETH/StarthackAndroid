package com.example.noahh_000.starthack.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.CurrentApplicationModel;
import com.example.noahh_000.starthack.models.Language;
import com.example.noahh_000.starthack.models.LanguageParser;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;


public abstract class LanguagePickerActivity extends ListActivity {
    LanguageParser languageParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        languageParser = new LanguageParser(this.getApplicationContext());

        setContentView(R.layout.activity_helper_intro);
        String[] languagesOriginal = new String[languageParser.getLanguages().size()];
        int i = 0;
        for (Language language : languageParser.getLanguages())
        {
            languagesOriginal[i] = language.getOriginal();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, languagesOriginal);
        setListAdapter(adapter);
    }

    protected ArrayList<String> getPickedLanguageList()
    {
        // Perform action on click
        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        ArrayList<String> userslanguages = new ArrayList<String>();
        for (int i = 0; i < getListView().getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                // Do something
                Log.d("HelperIntro", String.valueOf(languageParser.getLanguages().get(i).getOriginal()));
                userslanguages.add(languageParser.getLanguages().get(i).getInternational());
            }
        }

        Log.d("HelperIntro", "ListReady");

        return userslanguages;
    }
}