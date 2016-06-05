package com.example.noahh_000.starthack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.view.Window;
import android.widget.Button;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.ErrorModel;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.parse.ParseException;

public class UndecidedPickRoleActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ParseUser.getCurrentUser().saveInBackground();

        context = this;

        final Button button = (Button) findViewById(R.id.help);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                ParseUser.getCurrentUser().put("type", "translator");
                ParseUser.getCurrentUser().saveInBackground();

                ActivityNavigationModel.PickRoleTranslator.makeTransition(context);

                Log.d("main", "help");
            }
        });

        final Button butt = (Button) findViewById(R.id.getHelp);
        butt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                ParseUser.getCurrentUser().put("type", "user");
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ActivityNavigationModel.PickRoleUser.makeTransition(context);
                        } else {
                            ErrorModel.e(TAG, e.toString());
                        }
                    }

                });



                Log.d("main", "getHelp");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
