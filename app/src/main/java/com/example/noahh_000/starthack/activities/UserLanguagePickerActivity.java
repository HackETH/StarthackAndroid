package com.example.noahh_000.starthack.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentTranslatorModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.Language;
import com.example.noahh_000.starthack.models.LanguageParser;
import com.parse.ParseUser;

import java.util.ArrayList;


public class UserLanguagePickerActivity extends LanguagePickerActivity {
    private ArrayList<Language> languages;
    private CurrentUserModel currentUserModel;
    private boolean isChoosongFirstLanguage;
    private Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        context = this;

        isChoosongFirstLanguage = ActivityNavigationModel.UserLanguagePickerStart.getTransition(this.getIntent());

        currentUserModel = new CurrentUserModel();

        final FloatingActionButton butt = (FloatingActionButton) findViewById(R.id.fab);
        butt.setVisibility(View.GONE);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                savePickedLanguages(parent, position);
                returnToCallingActivity();
            }
        });
    }

    protected void returnToCallingActivity()
    {
        Intent intent = new Intent(getApplication(), UserHomeScreenActivity.class); // Start translator activity
        context.startActivity(intent);
    }

    protected void savePickedLanguages(AdapterView<?> parent, int position)
    {
        String lang = parent.getItemAtPosition(position).toString();
        lang = languageParser.OriginalToInternational(lang);

        if (isChoosongFirstLanguage)
            currentUserModel.setFirstLanguage(lang);
        else
            currentUserModel.setSecondLanguage(lang);
    }
}
