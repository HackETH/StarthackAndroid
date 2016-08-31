package com.example.noahh_000.starthack.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.CurrentTranslatorModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.ErrorModel;
import com.example.noahh_000.starthack.models.Language;
import com.example.noahh_000.starthack.models.LanguageParser;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;



public class TranslatorLanguagePickerActivity extends LanguagePickerActivity {
    private static String TAG = TranslatorLanguagePickerActivity.class.getName();

    ArrayList<Language> languages;
    CurrentTranslatorModel currentTranslatorModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        context = this;

        currentTranslatorModel = new CurrentTranslatorModel();

        for (String language : currentTranslatorModel.getLanguages())
        {
            int position = getAdapterItemPosition(language);
            if (position >= 0)
                getListView().setItemChecked(position, true);
            else
                ErrorModel.e(TAG, "A Language was chosen that is not part of the list");
        }

        final Button butt = (Button) findViewById(R.id.fab);
        butt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                savePickedLanguages();
                returnToCallingActivity();
            }
        });
    }

    protected int getListStyle()
    {
        return android.R.layout.simple_list_item_multiple_choice;
    }

    public LanguageParser getParserInstance()
    {
        return new LanguageParser(this);
    }

    private int getAdapterItemPosition(String name)
    {
        for (int position=0; position<getListView().getCount(); position++) {
            String item = getListView().getItemAtPosition(position).toString();
            item = languageParser.ShowToData(item);
            if (item.equals(name))
                return position;
        }
        return -1;
    }

    protected void returnToCallingActivity()
    {
        Intent intent = new Intent(getApplication(), TranslatorIsReadyActivity.class); // Start translator activity
        context.startActivity(intent);
    }

    protected void savePickedLanguages()
    {
        ArrayList<String> pickedLanguageList = getPickedLanguageList();
        currentTranslatorModel.setLanguages(pickedLanguageList);
    }
}