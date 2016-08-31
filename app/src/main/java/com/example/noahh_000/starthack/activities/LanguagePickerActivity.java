package com.example.noahh_000.starthack.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ListElement;
import com.example.noahh_000.starthack.models.ListParser;

import java.util.ArrayList;


public abstract class LanguagePickerActivity extends ListActivity {
    ListParser languageParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        languageParser = getParserInstance();

        setContentView(R.layout.activity_both_language_picker);
        String[] languagesOriginal = new String[languageParser.getList().size()];
        int i = 0;
        for (ListElement language : languageParser.getList())
        {
            languagesOriginal[i] = language.getShow();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                getListStyle(), languagesOriginal);
        setListAdapter(adapter);
    }

    abstract protected int getListStyle();

    abstract protected ListParser getParserInstance();

    protected ArrayList<String> getPickedLanguageList()
    {
        // Perform action on click
        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        ArrayList<String> userslanguages = new ArrayList<String>();
        for (int i = 0; i < getListView().getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                // Do something
                Log.d("HelperIntro", String.valueOf(languageParser.getList().get(i).getShow()));
                userslanguages.add(languageParser.getList().get(i).getData());
            }
        }

        Log.d("HelperIntro", "ListReady");

        return userslanguages;
    }
}