package com.example.noahh_000.starthack.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CountryParser;
import com.example.noahh_000.starthack.models.CurrentTranslatorModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.Language;
import com.example.noahh_000.starthack.models.LanguageParser;
import com.example.noahh_000.starthack.models.ListParser;
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
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        context = this;

        currentUserModel = new CurrentUserModel();

        isChoosongFirstLanguage = ActivityNavigationModel.UserLanguagePickerStart.getTransition(this.getIntent());

        final Button butt = (Button) findViewById(R.id.fab);
        butt.setVisibility(View.GONE);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView toolbarTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

        if (isChoosongFirstLanguage)
            toolbarTitle.setText(R.string.user_language_picker_select_first);
        else
            toolbarTitle.setText(R.string.user_language_picker_select_country);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                savePickedLanguages(parent, position);
                returnToCallingActivity();
            }
        });
    }

    protected int getListStyle()
    {
        return android.R.layout.simple_list_item_1;
    }

    public ListParser getParserInstance()
    {
        isChoosongFirstLanguage = ActivityNavigationModel.UserLanguagePickerStart.getTransition(this.getIntent());
        if (isChoosongFirstLanguage)
            return new LanguageParser(this);
        else
            return new CountryParser(this);
    }

    protected void returnToCallingActivity()
    {
        if (isChoosongFirstLanguage)
            ActivityNavigationModel.UserLanguagePickerStart.makeTransition(context, false); // Choose second Language
        else {
            Intent intent = new Intent(getApplication(), UserHomeScreenActivity.class); // Start translator activity
            context.startActivity(intent);
        }
    }

    protected void savePickedLanguages(AdapterView<?> parent, int position)
    {
        String lang = parent.getItemAtPosition(position).toString();
        lang = languageParser.ShowToData(lang);

        if (isChoosongFirstLanguage)
            currentUserModel.setFirstLanguage(lang);
        else {
            String[] split = lang.split("\\|");

            currentUserModel.setSecondLanguage(split[1]);
            currentUserModel.setCountry(split[0]);
        }
    }
}
