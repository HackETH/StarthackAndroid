package com.example.noahh_000.starthack.activities;

import android.os.Bundle;

import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.IncomingInvite;
import com.twilio.conversations.TwilioConversationsException;

public class UserVideoCallActivity extends VideoCallActivity {

    private static final String TAG = UserVideoCallActivity.class.getName();
    private String contactTwilioId;
    private String conversationId;
    private CurrentUserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the data that was transmitted to the activity, that is the conversationId of the conversation
        // that was started an where a user was found for
        String[] intent = ActivityNavigationModel.TranslatorAcceptVideoCall.getTransition(this.getIntent());
        conversationId = intent[0];

        currentUserModel = new CurrentUserModel();
    }

    // Handle all errors that were not specially treated
    protected void handleUncaughtError(Exception e)
    {

    }

    // This is called after the AccessToken was received by server and all listeners were started
    protected void initializationDone()
    {

    }

    // This is called after we ended the conversation
    protected void handleConversationEnded(Conversation conversation, TwilioConversationsException e)
    {

    }

    // This is called when the other participant disconnected
    protected void handleConversationDisconnected(Conversation conversation)
    {

    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleIncomingInviteCancelled(ConversationsClient conversationsClient, IncomingInvite incomingInvite)
    {

    }
}
