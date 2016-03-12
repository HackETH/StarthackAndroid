package com.example.noahh_000.starthack;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HelperIntroActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_intro);
        ArrayList<Language> languages = LanguageParser.getLanguageList(getApplicationContext());
        String[] languagesOriginal = new String[languages.size()];
        int i = 0;
        for (Language language : languages)
        {
            languagesOriginal[i] = language.getOriginal();
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, languagesOriginal);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        setListAdapter(adapter);

        final Button butt = (Button) findViewById(R.id.fab);
        butt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                SparseBooleanArray checked = getListView().getCheckedItemPositions();

                for (int i = 0; i < getListView().getAdapter().getCount(); i++) {
                    if (checked.get(i)) {
                        // Do something
                        
                    }
                }
                Log.d("HelperIntro", "ListReady");
            }
        });
    }
}