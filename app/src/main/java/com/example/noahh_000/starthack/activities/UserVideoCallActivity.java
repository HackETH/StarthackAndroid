package com.example.noahh_000.starthack.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.example.noahh_000.starthack.models.ErrorModel;
import com.example.noahh_000.starthack.models.UserModel;
import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.IncomingInvite;
import com.twilio.conversations.TwilioConversationsException;

import org.json.JSONObject;

import java.util.List;

public class UserVideoCallActivity extends VideoCallActivity {

    private static final String TAG = UserVideoCallActivity.class.getName();
    private String contactTwilioId;
    private String conversationId;
    private CurrentUserModel currentUserModel;
    private Context context;
    private TextView loadTypeTextView;
    private ParseObject currentConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = this.getApplicationContext();
        // Get the data that was transmitted to the activity, that is the conversationId of the conversation
        // that was started an where a user was found for
        String[] intent = ActivityNavigationModel.TranslatorAcceptVideoCall.getTransition(this.getIntent());
        conversationId = intent[0];

        currentUserModel = new CurrentUserModel();
    }

    private void contactUserListCallback(List<ParseUser> helperList)
    {
        if (helperList.size() == 0) { // No users were found
            contactUserListEmpty();
        } else { // Some users were found
            contactUserListFull(helperList);
        }
    }

    private void contactUserListEmpty()
    {
        Context context = getApplicationContext();
        CharSequence text = "Sorry there are no matching users.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void contactUserListFull(List<ParseUser> helperList)
    {
        // Convert User List to User Array
        String[] helperArray = new String[helperList.size()];
        int i = 0;
        for (ParseUser partner : helperList) {
            helperArray[i] = partner.getString("pushID");
            i++;
        }

        String conversationId = currentConversation.getObjectId();
        String twilioId = currentUserModel.getTwilioId();
        String strhelperlist = new String("");
        // For each matching user send out a push notification to that conversation
        for (String helper : helperArray)
        {
            if (helper != null && conversationId != null) {
                strhelperlist = strhelperlist  + ",'" + helper+"'";

            }
        }
        strhelperlist = strhelperlist.substring(1);
        try {
            String jsonstring = "{" +
                    "'contents': {'en':'Someone needs your help! Open the app now to translate.'}" +
                    ", 'data': {" +
                    "'conversationId': '" + conversationId + "'" +
                    ", 'twilioId':'" + twilioId + "'}" +
                    ", 'include_player_ids': [" + strhelperlist + "]" +
                    "}";
            OneSignal.postNotification(new JSONObject(jsonstring), null);
        } catch (Exception err) {
            err.printStackTrace();
        }
        waitOnTranslatorToConnect();

    }

    private void waitOnTranslatorToConnect()
    {
        ParseQuery.getQuery("Conversations").getInBackground(currentConversation.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject conversation, ParseException e) {
                if (currentConversation.getObjectId() != null){
                    Log.d("CallLoadingAct", "ID:" + currentConversation.getObjectId());

                }else{
                    Log.d("CallLoadingAct","No ObjectId of currenctConversation");

                }
                if (conversation != null && conversation.get("translator") != null)
                {
                    // finish waiting
                }
                else
                {
                    waitOnTranslatorToConnect();
                }
            }
        });
    }

    // Handle all errors that were not specially treated
    protected void handleUncaughtError(String tag, Exception e)
    {
        Log.e(TAG, "Video call: " + tag + "  " + e.toString());
        //ActivityNavigationModel.InitializationAsUser.makeTransition(context);
    }

    // This is called after the AccessToken was received by server and all listeners were started
    protected void initializationDone()
    {
        //loadTypeTextView = (TextView) findViewById(R.id.loadTypeTextView);
        //setLoadingText(R.string.call_loading_search_translator);

        // Initialize Data
        currentUserModel = new CurrentUserModel();

        // Create a new Comversation
        currentConversation = new ParseObject("Conversations");
        currentConversation = new ParseObject("Conversations");
        currentConversation.put("user", ParseUser.getCurrentUser());
        try {
            currentConversation.save();
        } catch (Exception e){
            ErrorModel.e(TAG, "error saving conversation" + e);
        }
        String id = currentConversation.getObjectId();
        // Get a list of users with the languages that are spoken by one self
        // and send all of them a push notification
        UserModel.getTranslatorsWithPreferredLanguages(
                currentUserModel.getFirstLanguage()
                , currentUserModel.getSecondLanguage()
                , new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> helperList, ParseException e) {
                        if (e == null) { // Successfully fetched Users
                            Log.d("callloadingactivity", "Query Result: " + helperList.toString());
                            contactUserListCallback(helperList);
                        } else {
                            Log.d("callloadingactivity", "callloadingactivity error in query " + e.getMessage());
                        }
                    }
                });
    }

    private void setLoadingText(int text_resource)
    {
        loadTypeTextView.setText(text_resource);
    }

    // This is called after we ended the conversation
    protected void handleConversationEnded(Conversation conversation, TwilioConversationsException e)
    {
        super.logout();
        ActivityNavigationModel.InitializationAsUser.makeTransition(context);
    }

    // This is called when the other participant disconnected
    protected void handleConversationDisconnected(Conversation conversation)
    {
        super.logout();
        ActivityNavigationModel.InitializationAsUser.makeTransition(context);
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleIncomingInviteCancelled(ConversationsClient conversationsClient, IncomingInvite incomingInvite)
    {
        super.logout();
        ActivityNavigationModel.InitializationAsUser.makeTransition(context);
    }
}
