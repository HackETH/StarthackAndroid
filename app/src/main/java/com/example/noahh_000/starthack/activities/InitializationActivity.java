package com.example.noahh_000.starthack.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentApplicationModel;
import com.example.noahh_000.starthack.models.SettingsModel;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.List;


public class InitializationActivity extends AppCompatActivity {

    ParseUser currentUser;

    /* API INITIALIZATION
    * OneSignal, Parse */
    private void initializeAPI()
    {
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new CallAcceptNotificationOpenedHandler())
                .init();

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();

        currentUser = ParseUser.getCurrentUser();
    }

    /* This is executed after the APIS have been initialized */
    private void onInitialized()
    {
        OneSignal.idsAvailable(
                new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        currentUser.put("pushID", userId);
                    }
                }
        );
    }

    /* Executed only on first run */
    private void onFirstRun()
    {
            //deleteUserswithPushID();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_initialization);

        initializeAPI();
        onInitialized();

        SettingsModel SM = new SettingsModel(this);
        boolean isFirstRun = SM.getSettingB(SettingsModel.Setting.FIRSTRUN);
        if (isFirstRun) {
            onFirstRun();
            SM.setSettingB(SettingsModel.Setting.FIRSTRUN, false);
        }

        CurrentApplicationModel currentUserModel = new CurrentApplicationModel();
        CurrentApplicationModel.Role role = currentUserModel.getRole();

        if(role == CurrentApplicationModel.Role.UNDECIDED)
            ActivityNavigationModel.InitializationAsUndecided.makeTransition(this);
        else if (role == CurrentApplicationModel.Role.TRANSLATOR)
            ActivityNavigationModel.InitializationAsTranslator.makeTransition(this);
        else if (role == CurrentApplicationModel.Role.USER)
            ActivityNavigationModel.InitializationAsUser.makeTransition(this);
    }

    protected void switchToReadyTranslatorActivity()
    {
        Intent intent = new Intent(this, TranslatorIsReadyActivity.class); // Start user activity
        this.startActivity(intent);
    }

    protected void switchToConversationActivity(String twilioId)
    {
        Intent intent = new Intent(this, TranslatorVideoCallActivity.class); // Start user activity
        intent.putExtra("IsInvited", true);
        intent.putExtra("twilioId", twilioId);
        this.startActivity(intent);
    }
    protected  void switchToAudioConversationActivity(String reachHimHere){
        Intent intent = new Intent(this, TranslatorAudioCallActivity.class); // Start user activity
        intent.putExtra("reachHimHere", reachHimHere);
        this.startActivity(intent);
    }

    private class CallAcceptNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("conversationId") && additionalData.has("reachMeHere")) {
                        final String conversationId = additionalData.getString("conversationId");
                        final String reachMeHere = additionalData.getString("reachMeHere");
                        Log.d("One Signal Push Accept", "ConversationId: " + conversationId);
                        switchToAudioConversationActivity(reachMeHere);
                        ParseQuery.getQuery("Conversations").getInBackground(conversationId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject conversation, ParseException e) {
                                if (e == null) {
                                    if (conversation.get("translator") == null) // TODO Can this be done?
                                    {
                                        conversation.put("translator", ParseUser.getCurrentUser());
                                        conversation.saveInBackground();
                                    } else // if call was already taken
                                    {
                                        Toast t = Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG);
                                        t.show();
                                        switchToReadyTranslatorActivity();
                                    }
                                }
                                else
                                    Log.e("", e.toString());
                            }
                        });
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
