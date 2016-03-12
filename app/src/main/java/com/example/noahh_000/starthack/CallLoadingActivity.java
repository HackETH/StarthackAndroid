package com.example.noahh_000.starthack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CallLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_loading);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        ParseObject conversation = new ParseObject("Conversations");
        conversation.put("user", currentUser);
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                ArrayList<String> queryLanguages = new ArrayList<String>();
                queryLanguages.add(currentUser.getString("firstLanguage"));
                queryLanguages.add(currentUser.getString("secondLanguage"));
                query.whereContainsAll("languages", queryLanguages);

                Log.d("callloadingactivity", "Query for matching users: "+queryLanguages.toString());

                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> helperList, ParseException e) {
                        if (e == null) {
                            Log.d("callloadingactivity", "Query Result: "+helperList.toString());
                            if (helperList.size() == 0) {
                                Context context = getApplicationContext();
                                CharSequence text = "Sorry there are no matching users.";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                //Intent intent = new Intent(getApplication(), HelpedMain.class); // Start translator activity
                                //getContext().startActivity(intent);
                            }
                            else
                            {

                                ((TextView)findViewById(R.id.waitText)).setText(helperList.size()+" users were contacted.");
                            }
                        } else {
                            Log.d("callloadingactivity", "callloadingactivity error in query " + e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
