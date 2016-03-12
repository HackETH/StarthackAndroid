package com.example.noahh_000.starthack;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
    ArrayList<Language> languages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.languages = LanguageParser.getLanguageList(getApplicationContext());
        setContentView(R.layout.activity_helper_intro);
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

        final FloatingActionButton butt = (FloatingActionButton) findViewById(R.id.fab);
        butt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                SparseBooleanArray checked = getListView().getCheckedItemPositions();
                ArrayList<String> userslanguages = new ArrayList<String>();
                for (int i = 0; i < getListView().getAdapter().getCount(); i++) {
                    if (checked.get(i)) {
                        // Do something
                        Log.d("HelperIntro", String.valueOf(languages.get(i).getOriginal()));
                        userslanguages.add(languages.get(i).getInternational());
                    }
                }
                ParseUser.getCurrentUser().put("languages",userslanguages);
                ParseUser.getCurrentUser().saveInBackground();
                Log.d("HelperIntro", "ListReady");
                Intent intient = new Intent(v.getContext(), ReadyTranslatorActivity.class); // Start user activity
                v.getContext().startActivity(intient);
            }
        });
    }
}