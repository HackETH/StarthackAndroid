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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CountryParser;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.LanguageParser;

public class UserHomeScreenActivity extends AppCompatActivity {
    private static String TAG = UserHomeScreenActivity.class.getName();
    private TextView firstLanguageTextView;
    private TextView secondLanguageTextView;

    private CurrentUserModel currentUserModel;
    private LanguageParser languageParser;
    private CountryParser countryParser;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_main);

        context = this;

        languageParser = new LanguageParser(getApplicationContext());
        countryParser = new CountryParser(getApplicationContext());
        currentUserModel = new CurrentUserModel();

        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        firstLanguageTextView = ((TextView)findViewById(R.id.firstLanguage));
        secondLanguageTextView = ((TextView)findViewById(R.id.secondLanguage));

        if (currentUserModel.getFirstLanguage() != null)
            firstLanguageTextView.setText(languageParser.DataToShow(currentUserModel.getFirstLanguage()));
        else
            firstLanguageTextView.setText(R.string.user_home_screen_choose_native);

        if (currentUserModel.getSecondLanguage() != null) {
            String data = currentUserModel.getCountry() + "|" + currentUserModel.getSecondLanguage();
            secondLanguageTextView.setText(countryParser.DataToShow(data));
        }
        else
            secondLanguageTextView.setText(R.string.user_home_screen_choose_alien);

        firstLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigationModel.UserLanguagePickerStart.makeTransition(context, true);
            }
        });

        secondLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigationModel.UserLanguagePickerStart.makeTransition(context, false);
            }
        });

        ((Button)findViewById(R.id.helpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), UserAudioCallActivity.class); // Start translator activity
                v.getContext().startActivity(intent);
            }
        });
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
                currentUserModel.reset();
                ActivityNavigationModel.InitializationAsUndecided.makeTransition(context);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
