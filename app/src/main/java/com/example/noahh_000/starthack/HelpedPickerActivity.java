package com.example.noahh_000.starthack;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class HelpedPickerActivity extends ListActivity {

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
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        setListAdapter(adapter);
    }
}
