package com.example.noahh_000.starthack.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noahh_000.starthack.R;
import com.example.noahh_000.starthack.models.ActivityNavigationModel;
import com.example.noahh_000.starthack.models.CurrentTranslatorModel;
import com.example.noahh_000.starthack.models.CurrentUserModel;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.conversations.AudioOutput;
import com.twilio.conversations.AudioTrack;
import com.twilio.conversations.CameraCapturer;
import com.twilio.conversations.CameraCapturerFactory;
import com.twilio.conversations.CapturerErrorListener;
import com.twilio.conversations.CapturerException;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationCallback;
import com.twilio.conversations.ConversationListener;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.ConversationsClientListener;
import com.twilio.conversations.IncomingInvite;
import com.twilio.conversations.LocalMedia;
import com.twilio.conversations.LocalMediaFactory;
import com.twilio.conversations.LocalMediaListener;
import com.twilio.conversations.LocalVideoTrack;
import com.twilio.conversations.LocalVideoTrackFactory;
import com.twilio.conversations.MediaTrack;
import com.twilio.conversations.OutgoingInvite;
import com.twilio.conversations.Participant;
import com.twilio.conversations.ParticipantListener;
import com.twilio.conversations.TwilioConversations;
import com.twilio.conversations.TwilioConversationsException;
import com.twilio.conversations.VideoRendererObserver;
import com.twilio.conversations.VideoTrack;
import com.twilio.conversations.VideoViewRenderer;

import java.util.HashSet;
import java.util.Set;

public class TranslatorVideoCallActivity extends VideoCallActivity {

    private static final String TAG = TranslatorVideoCallActivity.class.getName();
    private String contactTwilioId;
    private String conversationId;
    private CurrentTranslatorModel currentTranslatorModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get the data that was transmitted to the activity, that is the conversationId of the accepted Converation and the
        // users twilioId to send him an invite
        String[] intent = ActivityNavigationModel.TranslatorAcceptVideoCall.getTransition(this.getIntent());
        contactTwilioId = intent[0];
        conversationId = intent[1];

        context = this.getApplicationContext();

        currentTranslatorModel = new CurrentTranslatorModel();
        super.onCreate(savedInstanceState);
    }

    // Handle all errors that were not specially treated
    protected void handleUncaughtError(String tag, Exception e) {
        Log.e(TAG, "Video call: "+tag+"  "+e.toString());
        //ActivityNavigationModel.InitializationAsTranslator.makeTransition(context);
    }

    // This is called after the AccessToken was received by server and all listeners were started
    protected void initializationDone()
    {
        sendOutGoingInvite(contactTwilioId);
    }

    // This is called after we ended the conversation
    protected void handleConversationEnded(Conversation conversation, TwilioConversationsException e)
    {
        super.logout();
    }

    // This is called when the other participant disconnected
    protected void handleConversationDisconnected(Conversation conversation)
    {
        super.logout();
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleIncomingInviteCancelled(ConversationsClient conversationsClient, IncomingInvite incomingInvite)
    {
        super.logout();
    }
}
