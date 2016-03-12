package com.example.noahh_000.starthack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class HelpedMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helped_main);

        ArrayList<Language> languages = LanguageParser.getLanguageList(getApplicationContext());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.getString("firstLanguage") != null)
        {
            ((TextView)findViewById(R.id.firstLanguage)).setText(LanguageParser.InternationalToOriginal(currentUser.getString("firstLanguage"), languages));
        }
        if (currentUser.getString("secondLanguage") != null)
        {
            ((TextView)findViewById(R.id.secondLanguage)).setText(LanguageParser.InternationalToOriginal(currentUser.getString("secondLanguage"), languages));
        }

        ((TextView)findViewById(R.id.firstLanguage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), HelpedPickerActivity.class); // Start translator activity
                intent.putExtra("HelpedPickerActivity_isFirstLanguage", true);
                v.getContext().startActivity(intent);
            }
        });

        ((TextView)findViewById(R.id.secondLanguage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), HelpedPickerActivity.class); // Start translator activity
                intent.putExtra("HelpedPickerActivity_isFirstLanguage", false);
                v.getContext().startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.helpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CallLoadingActivity.class); // Start translator activity
                v.getContext().startActivity(intent);
            }
        });
    }



}
