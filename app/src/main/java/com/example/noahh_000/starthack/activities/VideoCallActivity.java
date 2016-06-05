package com.example.noahh_000.starthack.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashSet;
import java.util.Set;

import com.example.noahh_000.starthack.R;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseUser;
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

import org.json.JSONObject;

public abstract class VideoCallActivity extends AppCompatActivity {

    private static final String TAG = VideoCallActivity.class.getName();

    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;

    /*
     * Twilio Conversations Client allows a client to create or participate in a conversation.
     */
    private ConversationsClient conversationsClient;

    /*
     * A Conversation represents communication between the client and one or more participants.
     */
    private Conversation conversation;

    /*
     * An OutgoingInvite represents an invitation to start or join a conversation with one or more participants
     */
    private OutgoingInvite outgoingInvite;

    /*
     * A VideoViewRenderer receives frames from a local or remote video track and renders the frames to a provided view
     */
    private VideoViewRenderer participantVideoRenderer;
    private VideoViewRenderer localVideoRenderer;

    /*
     * Android application UI elements
     */
    private FrameLayout previewFrameLayout;
    private ViewGroup localContainer;
    private ViewGroup participantContainer;
    private TextView conversationStatusTextView;
    private TwilioAccessManager accessManager;
    private CameraCapturer cameraCapturer;
    private FloatingActionButton callActionFab;
    private FloatingActionButton switchCameraActionFab;
    private FloatingActionButton muteActionFab;
    private FloatingActionButton speakerActionFab;
    private FloatingActionButton hangUpFab;
    private android.support.v7.app.AlertDialog alertDialog;

    private boolean muteMicrophone;
    private boolean pauseVideo;

    private boolean wasPreviewing;
    private boolean wasLive;

    private final String SERVER_ACCESSTOKEN_REQUEST_URL = "http://murmuring-everglades-87090.herokuapp.com/token.php";

    // Handle all errors that were not specially treated
    protected abstract void handleUncaughtError(Exception e);

    // This is called after the AccessToken was received by server and all listeners were started
    protected abstract void initializationDone();

    // This is called after we ended the conversation
    protected abstract void handleConversationEnded(Conversation conversation, TwilioConversationsException e);

    // This is called when the other participant disconnected
    protected abstract void handleConversationDisconnected(Conversation conversation);

    // This is called when an incoming invite was cancelled by the current user
    protected abstract void handleIncomingInviteCancelled(ConversationsClient conversationsClient, IncomingInvite incomingInvite);

    // This is called after we ended the conversation
    protected void handleStartListeningError(TwilioConversationsException e) {
        handleUncaughtError(e);
    }

    // This is called after we ended the conversation
    protected void handleConversationConnectError(Conversation conversation, TwilioConversationsException e) {
        handleUncaughtError(e);
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleStartCallFailed(TwilioConversationsException e) {
        handleUncaughtError(e);
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleInvalidParticipantCall() {
        handleUncaughtError(null);
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleFailedToInitializeTwilioSDK(Exception e) {
        handleUncaughtError(e);
    }

    // This is called when an incoming invite was cancelled by the current user
    protected void handleGetAccessTokenError(Exception e) {
        handleUncaughtError(e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        /*
         * Load views from resources
         */
        previewFrameLayout = (FrameLayout) findViewById(R.id.previewFrameLayout);
        // UI Element showing own video
        localContainer = (ViewGroup)findViewById(R.id.localContainer);
        // UI Element showing the other users video
        participantContainer = (ViewGroup)findViewById(R.id.participantContainer);

        switchCameraActionFab = (FloatingActionButton) findViewById(R.id.switch_camera_action_fab);
        muteActionFab = (FloatingActionButton) findViewById(R.id.mute_action_fab);
        hangUpFab = (FloatingActionButton) findViewById(R.id.hang_up_fab);
        hangUpFab.setOnClickListener(hangupClickListener());

        // Enables the volume up/down keys to control the calls volume
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        // Check the Camera and Microphone Permissions
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            initializeTwilioSdk(); // Initialize the SDK
        }

        // Set the initial UI State
        setCallAction();
    }

    /* Starts the listeners to wait for an incoming call
    *
    * This is called after the Initialization->TwilioInitAPI->AccessTokenFromServer was finished */
    private void startWaitingOnCalls(String accessToken)
    {
        accessManager =
                TwilioAccessManagerFactory.createAccessManager(accessToken,
                        accessManagerListener());
        conversationsClient =
                TwilioConversations
                        .createConversationsClient(accessManager,
                                conversationsClientListener());
        // Specify the audio output to use for this conversation client
        conversationsClient.setAudioOutput(AudioOutput.SPEAKERPHONE);
        // Initialize the camera capturer and start the camera preview
        cameraCapturer = CameraCapturerFactory.createCameraCapturer(
                VideoCallActivity.this,
                CameraCapturer.CameraSource.CAMERA_SOURCE_FRONT_CAMERA,
                previewFrameLayout,
                capturerErrorListener());
        startPreview();

        // Register to receive incoming invites
        conversationsClient.listen();

        initializationDone();
    }

    /*
     * The initial state when there is no active conversation.
     *
     * Initializes a button to switch the Camera and a Button to mute the conversation
     */
    private void setCallAction() {
        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());
        localContainer.setVisibility(View.GONE);
    }

    /*
     * The actions performed during hangup.
     *
     * TODO: Really? Isnt this rather to initialize the UI when in a call
     */
    private void setHangupAction() {

    }


    /* Invites Participant with twilioId participant */
    protected void sendOutGoingInvite(String participant)
    {
        if (!participant.isEmpty() && (conversationsClient != null)) {
            stopPreview();
            // Create participants set (we support only one in this example)
            Set<String> participants = new HashSet<>();
            participants.add(participant);

            // Create local media
            LocalMedia localMedia = setupLocalMedia();

            // Create outgoing invite
            outgoingInvite = conversationsClient.sendConversationInvite(participants,
                    localMedia, new ConversationCallback() {
                        @Override
                        public void onConversation(Conversation conversation, TwilioConversationsException e) {
                            if (e == null) {
                                // Participant has accepted invite, we are in active conversation
                                VideoCallActivity.this.conversation = conversation;
                                conversation.setConversationListener(conversationListener());
                            } else {
                                handleStartCallFailed(e);
                                hangup();
                                reset();
                            }
                        }
                    });
            setHangupAction();
        } else {
            handleInvalidParticipantCall();
            Log.e(TAG, "invalid participant call");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE &&
                permissions.length > 0) {
            boolean granted = true;
            /*
             * Check if all permissions are granted
             */
            for (int i=0; i < permissions.length; i++) {
                granted = granted && (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            if (granted) {
                /*
                 * Initialize the Twilio Conversations SDK
                 */
                initializeTwilioSdk();
            } else {
                Toast.makeText(this,
                        "Camera and Microphone permissions needed. Please allow in App Settings for additional functionality.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TwilioConversations.isInitialized() &&
                conversationsClient != null &&
                !conversationsClient.isListening()) {
            conversationsClient.listen();
        }
        // Resume preview
        if(cameraCapturer != null && wasPreviewing) {
            cameraCapturer.startPreview();
            wasPreviewing = false;
        }
        // Resume live video
        if(conversation != null && wasLive) {
            pauseVideo(false);
            wasLive = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (TwilioConversations.isInitialized() &&
                conversationsClient != null  &&
                conversationsClient.isListening() &&
                conversation == null) {
            conversationsClient.unlisten();
        }
        // Stop preview before going to the background
        if(cameraCapturer != null && cameraCapturer.isPreviewing()) {
            cameraCapturer.stopPreview();
            wasPreviewing = true;
        }
        // Pause live video before going to the background
        if(conversation != null && pauseVideo) {
            pauseVideo(true);
            wasLive = true;
        }
    }

    /*
     * Initialize the Twilio Conversations SDK
     */
    private void initializeTwilioSdk(){
        TwilioConversations.setLogLevel(TwilioConversations.LogLevel.DEBUG);

        if(!TwilioConversations.isInitialized()) {
            TwilioConversations.initialize(getApplicationContext(), new TwilioConversations.InitListener() {
                @Override
                public void onInitialized() {
                    /**
                     * Now that the SDK is initialized we create a ConversationsClient and
                     * register for incoming calls. The TwilioAccessManager manages the lifetime
                     * of the access token and notifies the client of token expirations.
                     */
                    retrieveAccessTokenfromServer();
                }

                @Override
                public void onError(Exception e) {
                    handleFailedToInitializeTwilioSDK(e);
                }
            });
        }
    }

    /* Starts previewing the own image */
    private void startPreview() {
        cameraCapturer.startPreview();
    }

    /* Stops previewing the own image */
    private void stopPreview() {
        if(cameraCapturer != null && cameraCapturer.isPreviewing()) {
            cameraCapturer.stopPreview();
        }
    }

    private void hangup() {
        if(conversation != null) {
            conversation.disconnect();
        } else if(outgoingInvite != null){
            outgoingInvite.cancel();
        }
        this.finish();
    }

    /*
     * Resets UI elements. Used after conversation has ended.
     */
    private void reset() {
        if(participantVideoRenderer != null) {
            participantVideoRenderer = null;
        }
        localContainer.removeAllViews();
        localContainer = (ViewGroup)findViewById(R.id.localContainer);
        participantContainer.removeAllViews();

        if(conversation != null) {
            conversation.dispose();
            conversation = null;
        }
        outgoingInvite = null;

        muteMicrophone = false;
        muteActionFab.setImageDrawable(
                ContextCompat.getDrawable(VideoCallActivity.this,
                        R.drawable.ic_mic_green_24px));

        pauseVideo = false;

        if (conversationsClient != null) {
            conversationsClient.setAudioOutput(AudioOutput.HEADSET);
        }

        setCallAction();
        startPreview();
    }

    private boolean pauseVideo(boolean pauseVideo) {
        /*
         * Enable/disable local video track
         */
        if (conversation != null) {
            LocalVideoTrack videoTrack =
                    conversation.getLocalMedia().getLocalVideoTracks().get(0);
            if(videoTrack != null) {
                return videoTrack.enable(!pauseVideo);
            }
        }
        return false;
    }

    private DialogInterface.OnClickListener callParticipantClickListener(final EditText participantEditText) {
        return new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
    }

    private DialogInterface.OnClickListener cancelCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setCallAction();
                alertDialog.dismiss();
            }
        };
    }

    private void acceptCall(final IncomingInvite invite)
    {
        LocalMedia localMedia = setupLocalMedia();

        invite.accept(localMedia, new ConversationCallback() {
            @Override
            public void onConversation(Conversation conversation, TwilioConversationsException e) {
                Log.e(TAG, "sendConversationInvite onConversation");
                if (e == null) {
                    VideoCallActivity.this.conversation = conversation;
                    conversation.setConversationListener(conversationListener());
                } else {
                    Log.e(TAG, e.getMessage());
                    hangup();
                    reset();
                }
            }
        });
        setHangupAction();
    }


    private View.OnClickListener hangupClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hangup();
                setCallAction();
            }
        };
    }

    private View.OnClickListener switchCameraClickListener() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(cameraCapturer != null) {
                    cameraCapturer.switchCamera();
                }
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Mute/unmute microphone
                 */
                muteMicrophone = !muteMicrophone;
                if (conversation != null) {
                    conversation.getLocalMedia().mute(muteMicrophone);
                }
                if (muteMicrophone) {
                    muteActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoCallActivity.this, R.drawable.ic_mic_off_red_24px));
                } else {
                    muteActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoCallActivity.this, R.drawable.ic_mic_green_24px));
                }
            }
        };
    }

    private View.OnClickListener speakerClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Audio routing to speakerphone or headset
                 */
                if (conversationsClient == null) {
                    Log.e(TAG, "Unable to set audio output, conversation client is null");
                    return;
                }
                boolean speakerOn =
                        !(conversationsClient.getAudioOutput() ==  AudioOutput.SPEAKERPHONE) ?  true : false;
                conversationsClient.setAudioOutput(speakerOn ? AudioOutput.SPEAKERPHONE : AudioOutput.HEADSET);
                if (speakerOn) {
                    speakerActionFab.setImageDrawable(
                            ContextCompat.getDrawable( VideoCallActivity.this,
                                    R.drawable.ic_volume_down_green_24px));
                } else {
                    speakerActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoCallActivity.this,
                                    R.drawable.ic_volume_down_white_24px));
                }
            }
        };
    }

    /*
     * Conversation Listener
     */
    private ConversationListener conversationListener() {
        return new ConversationListener() {
            @Override
            public void onParticipantConnected(Conversation conversation, Participant participant) {
                //((LoadingCallAnimationDrawableView)findViewById(R.id.loadViewConversation)).endAnimation();
                localContainer.setVisibility(View.VISIBLE);
                participant.setParticipantListener(participantListener());
            }

            @Override
            public void onFailedToConnectParticipant(Conversation conversation, Participant participant, TwilioConversationsException e) {
                Log.e(TAG, e.getMessage());
                handleConversationConnectError(conversation, e);
            }

            // This is called when the other participant disconnected
            @Override
            public void onParticipantDisconnected(Conversation conversation, Participant participant) {
                handleConversationDisconnected(conversation);
            }

            // This is called after we ended the conversation
            @Override
            public void onConversationEnded(Conversation conversation, TwilioConversationsException e) {
                handleConversationEnded(conversation, e);
            }
        };
    }

    /*
     * LocalMedia listener
     */
    private LocalMediaListener localMediaListener(){
        return new LocalMediaListener() {
            @Override
            public void onLocalVideoTrackAdded(LocalMedia localMedia, LocalVideoTrack localVideoTrack) {
                //conversationStatusTextView.setText("onLocalVideoTrackAdded");
                localVideoRenderer = new VideoViewRenderer(VideoCallActivity.this, localContainer);
                localVideoTrack.addRenderer(localVideoRenderer);
            }

            @Override
            public void onLocalVideoTrackRemoved(LocalMedia localMedia, LocalVideoTrack localVideoTrack) {
                localContainer.removeAllViews();
            }

            @Override
            public void onLocalVideoTrackError(LocalMedia localMedia, LocalVideoTrack localVideoTrack, TwilioConversationsException e) {
                Log.e(TAG, "LocalVideoTrackError: " + e.getMessage());
            }
        };
    }

    /*
     * Participant listener
     */
    private ParticipantListener participantListener() {
        return new ParticipantListener() {
            @Override
            // We received video from the other user
            public void onVideoTrackAdded(Conversation conversation, Participant participant, VideoTrack videoTrack) {
                Log.i(TAG, "onVideoTrackAdded " + participant.getIdentity());

                // Remote participant
                participantVideoRenderer = new VideoViewRenderer(VideoCallActivity.this, participantContainer);
                participantVideoRenderer.setObserver(new VideoRendererObserver() {

                    @Override
                    public void onFirstFrame() {
                        Log.i(TAG, "Participant onFirstFrame");
                    }

                    @Override
                    public void onFrameDimensionsChanged(int width, int height, int rotation) {
                        Log.i(TAG, "Participant onFrameDimensionsChanged " + width + " " + height + " " + rotation);
                    }

                });
                videoTrack.addRenderer(participantVideoRenderer);

            }

            @Override
            public void onVideoTrackRemoved(Conversation conversation, Participant participant, VideoTrack videoTrack) {
                Log.i(TAG, "onVideoTrackRemoved " + participant.getIdentity());
                participantContainer.removeAllViews();

            }

            @Override
            public void onAudioTrackAdded(Conversation conversation, Participant participant, AudioTrack audioTrack) {
                Log.i(TAG, "onAudioTrackAdded " + participant.getIdentity());
            }

            @Override
            public void onAudioTrackRemoved(Conversation conversation, Participant participant, AudioTrack audioTrack) {
                Log.i(TAG, "onAudioTrackRemoved " + participant.getIdentity());
            }

            @Override
            public void onTrackEnabled(Conversation conversation, Participant participant, MediaTrack mediaTrack) {
                Log.i(TAG, "onTrackEnabled " + participant.getIdentity());
            }

            @Override
            public void onTrackDisabled(Conversation conversation, Participant participant, MediaTrack mediaTrack) {
                Log.i(TAG, "onTrackDisabled " + participant.getIdentity());
            }
        };
    }

    /*
     * ConversationsClient listener
     *
     * This listener accepts an incoming call immediately if we are not in a conversation
     * Otherwise the call will be aborted
     */
    private ConversationsClientListener conversationsClientListener() {
        return new ConversationsClientListener() {
            @Override
            public void onStartListeningForInvites(ConversationsClient conversationsClient) {
            }

            @Override
            public void onStopListeningForInvites(ConversationsClient conversationsClient) {
            }

            @Override
            public void onFailedToStartListening(ConversationsClient conversationsClient, TwilioConversationsException e) {
                handleStartListeningError(e);
            }

            @Override
            /* Handles an incoming invite
            *
            * We want to accept any one who wants to connect as long as we are not in Conversation*/
            public void onIncomingInvite(ConversationsClient conversationsClient, IncomingInvite incomingInvite) {
                if (conversation == null) {
                    acceptCall(incomingInvite); // Immediately accept call
                } else {
                    Log.w(TAG, String.format("Conversation in progress. Invite from %s ignored", incomingInvite.getInvitee()));
                }
            }

            @Override
            public void onIncomingInviteCancelled(ConversationsClient conversationsClient, IncomingInvite incomingInvite) {
                handleIncomingInviteCancelled(conversationsClient, incomingInvite);
            }
        };
    }

    /*
     * CameraCapture error listener
     */
    private CapturerErrorListener capturerErrorListener() {
        return new CapturerErrorListener() {
            @Override
            public void onError(CapturerException e) {
                Log.e(TAG, "Camera capturer error: " + e.getMessage());
            }
        };
    }

    /*
     * AccessManager listener
     *
     * TODO: Find out what these do
     */
    private TwilioAccessManagerListener accessManagerListener() {
        return new TwilioAccessManagerListener() {
            @Override
            public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
            }

            @Override
            public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {
            }

            @Override
            public void onError(TwilioAccessManager twilioAccessManager, String s) {
            }
        };
    }


    /*
     * Helper methods
     */

    private LocalMedia setupLocalMedia() {
        LocalMedia localMedia = LocalMediaFactory.createLocalMedia(localMediaListener());
        LocalVideoTrack localVideoTrack = LocalVideoTrackFactory.createLocalVideoTrack(cameraCapturer);
        if (pauseVideo) {
            localVideoTrack.enable(false);
        }
        localMedia.addLocalVideoTrack(localVideoTrack);
        if (muteMicrophone) {
            localMedia.mute(true);
        }
        return localMedia;
    }

    private boolean checkPermissionForCameraAndMicrophone(){
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if ((resultCamera == PackageManager.PERMISSION_GRANTED) &&
                (resultMic == PackageManager.PERMISSION_GRANTED)){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionForCameraAndMicrophone(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){
            Toast.makeText(this,
                    "Camera and Microphone permissions needed. Please allow in App Settings for additional functionality.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void retrieveAccessTokenfromServer() {
        Ion.with(this)
                .load(SERVER_ACCESSTOKEN_REQUEST_URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            // The identity can be used to receive calls
                            String identity = result.get("identity").getAsString();

                            ParseUser.getCurrentUser().put("twilioIdentity", identity);
                            ParseUser.getCurrentUser().saveInBackground();

                            String accessToken = result.get("token").getAsString();

                            startWaitingOnCalls(accessToken);
                        } else {
                            Toast.makeText(VideoCallActivity.this,
                                    R.string.error_retrieving_access_token, Toast.LENGTH_SHORT)
                                    .show();
                            handleGetAccessTokenError(e);
                        }
                    }
                });
    }
}
