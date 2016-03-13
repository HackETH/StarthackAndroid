package com.example.noahh_000.starthack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CallLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call_loading);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseObject conversation = new ParseObject("Conversations");
        conversation.put("user", currentUser);
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                ArrayList<String> queryLanguages = new ArrayList<String>();
                queryLanguages.add(currentUser.getString("firstLanguage"));
                queryLanguages.add(currentUser.getString("secondLanguage"));
                query.whereContainsAll("languages", queryLanguages);

                Log.d("callloadingactivity", "Query for matching users: " + queryLanguages.toString());

                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> helperList, ParseException e) {
                        if (e == null) {
                            Log.d("callloadingactivity", "Query Result: " + helperList.toString());
                            if (helperList.size() == 0) {
                                Context context = getApplicationContext();
                                CharSequence text = "Sorry there are no matching users.";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else {
                                String[] helperArray = new String[helperList.size()];
                                int i = 0;
                                for (ParseUser partner : helperList) {
                                    helperArray[i] = partner.getString("pushID");
                                    i++;
                                }

                                String conversationId = conversation.getObjectId();
                                Context context = getApplicationContext();

                                Intent intent = new Intent(getApplication(), ConversationActivity.class); // Start translator activity
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("isInvited", false);
                                intent.putExtra("helperArray", helperArray);

                                intent.putExtra("conversationId", conversationId);
                                context.startActivity(intent);
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
