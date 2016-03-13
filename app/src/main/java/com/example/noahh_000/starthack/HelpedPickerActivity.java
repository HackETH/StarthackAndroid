package com.example.noahh_000.starthack;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;

public class HelpedPickerActivity extends ListActivity {

    boolean isFirstLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        isFirstLanguage = intent.getBooleanExtra("HelpedPickerActivity_isFirstLanguage", false);

        setContentView(R.layout.activity_helper_intro);
        final ArrayList<Language> languages = LanguageParser.getLanguageList(getApplicationContext());
        String[] languagesOriginal = new String[languages.size()];
        int i = 0;
        for (Language language : languages)
        {
            languagesOriginal[i] = language.getOriginal();
            i++;
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, languagesOriginal);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        setListAdapter(adapter);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lang = parent.getItemAtPosition(position).toString();
                if (isFirstLanguage)
                    ParseUser.getCurrentUser().put("firstLanguage", LanguageParser.OriginalToInternational(lang, languages));
                else
                    ParseUser.getCurrentUser().put("secondLanguage", LanguageParser.OriginalToInternational(lang, languages));
                ParseUser.getCurrentUser().put("languages", new ArrayList<String>());
                ParseUser.getCurrentUser().saveInBackground();


                Intent intent = new Intent(getApplication(), HelpedMain.class); // Start translator activity

                view.getContext().startActivity(intent);

            }
        });
    }

}
