package com.example.noahh_000.starthack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentTranslatorModel;

public class TranslatorIsReadyActivity extends AppCompatActivity {

    Context context;
    private CurrentTranslatorModel currentTranslatorModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator_main);
        context = this;

        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        findViewById(R.id.helpButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intentApp = new Intent(v.getContext(), TranslatorLanguagePickerActivity.class);
                v.getContext().startActivity(intentApp);
            }
        });

        currentTranslatorModel = new CurrentTranslatorModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ready_translator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_role:
                // User chose the "Settings" item, show the app settings UI...
                currentTranslatorModel.reset();
                ActivityNavigationModel.InitializationAsUndecided.makeTransition(context);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
