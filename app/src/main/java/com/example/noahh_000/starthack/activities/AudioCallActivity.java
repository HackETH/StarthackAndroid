package com.example.noahh_000.starthack.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noahh_000.starthack.R;
import com.parse.Parse;
import  com.parse.ParseUser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.client.Connection;
import com.twilio.client.ConnectionListener;
import com.twilio.client.Device;
import com.twilio.client.DeviceListener;
import com.twilio.client.PresenceEvent;
import com.twilio.client.Twilio;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuelmueller on 28.07.16.
 */
public class AudioCallActivity extends AppCompatActivity implements DeviceListener, ConnectionListener {
    private static final int MIC_PERMISSION_REQUEST_CODE = 1;

    /*
     * You must provide a publicly accessible server to generate a Capability Token to connect to the Client service
     * Refer to website documentation for additional details: https://www.twilio.com/docs/quickstart/php/android-client
     */
    private static final String TOKEN_SERVICE_URL = "https://helpingvoice.herokuapp.com/token";

    /*
     * A Device is the primary entry point to Twilio Services
     */
    private Device clientDevice;

    /*
     * A Connection represents a connection between a Device and Twilio Services.
     * Connections are either outgoing or incoming, and not created directly.
     * An outgoing connection is created by Device.connect()
     * An incoming connection are created internally by a Device and hanged to the registered PendingIntent
     */
    private Connection connection;
    private Connection pendingConnection;
    protected  Boolean speakerPhone;
    protected  Boolean muteMicrophone;
    protected AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;
    protected String TAG = this.getClass().getName();
    protected FloatingActionButton muteActionFab;
    protected FloatingActionButton speakerActionFab;
    protected FloatingActionButton hangUpActionFab;
    protected class ClientProfile {
        private String name;
        private boolean allowOutgoing = true;
        private boolean allowIncoming = true;


        public ClientProfile(String name, boolean allowOutgoing, boolean allowIncoming) {
            this.name = name;
            this.allowOutgoing = allowOutgoing;
            this.allowIncoming = allowIncoming;
        }

        public String getName() {
            return name;
        }

        public boolean isAllowOutgoing() {
            return allowOutgoing;
        }

        public boolean isAllowIncoming() {
            return allowIncoming;
        }
    }

    /*
     * Android application UI elements
     */
    protected ClientProfile clientProfile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);
        hangUpActionFab = (FloatingActionButton) findViewById(R.id.hang_up_fab);
        muteActionFab = (FloatingActionButton) findViewById(R.id.mute_fab);
        speakerActionFab = (FloatingActionButton) findViewById(R.id.speaker_fab);
        hangUpActionFab.setOnClickListener(hangupActionFabClickListener());
        muteActionFab.setOnClickListener(muteMicrophoneFabClickListener());
        speakerActionFab.setOnClickListener(toggleSpeakerPhoneFabClickListener());

    }

    protected void initializeTwilioClientSDK() {

        if (!Twilio.isInitialized()) {
            Twilio.initialize(getApplicationContext(), new Twilio.InitListener() {

                /*
                 * Now that the SDK is initialized we can register using a Capability Token.
                 * A Capability Token is a JSON Web Token (JWT) that specifies how an associated Device
                 * can interact with Twilio services.
                 */
                @Override
                public void onInitialized() {
                    Twilio.setLogLevel(Log.DEBUG);
                    /*
                     * Retrieve the Capability Token from your own web server
                     */
                    retrieveCapabilityToken(clientProfile);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(AudioCallActivity.this, "Failed to initialize the Twilio Client SDK", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /*
     * Create a Device or update the capabilities of the current Device
     */
    private void createDevice(String capabilityToken) {
        try {
            if (clientDevice == null) {
                clientDevice = Twilio.createDevice(capabilityToken, this);

                /*
                 * Providing a PendingIntent to the newly create Device, allowing you to receive incoming calls
                 *
                 *  What you do when you receive the intent depends on the component you set in the Intent.
                 *
                 *  If you're using an Activity, you'll want to override Activity.onNewIntent()
                 *  If you're using a Service, you'll want to override Service.onStartCommand().
                 *  If you're using a BroadcastReceiver, override BroadcastReceiver.onReceive().
                 */

                Intent intent = new Intent(getApplicationContext(), AudioCallActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                clientDevice.setIncomingIntent(pendingIntent);
            } else {
                clientDevice.updateCapabilityToken(capabilityToken);
            }


        } catch (Exception e) {
            Log.e(TAG, "An error has occured updating or creating a Device: \n" + e.toString());
            Toast.makeText(AudioCallActivity.this, "Device error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent != null) {
            /*
             * Determine if the receiving Intent has an extra for the incoming connection. If so,
             * remove it from the Intent to prevent handling it again next time the Activity is resumed
             */
            Device device = intent.getParcelableExtra(Device.EXTRA_DEVICE);
            Connection incomingConnection = intent.getParcelableExtra(Device.EXTRA_CONNECTION);

            if (incomingConnection == null && device == null) {
                return;
            }
            intent.removeExtra(Device.EXTRA_DEVICE);
            intent.removeExtra(Device.EXTRA_CONNECTION);

            pendingConnection = incomingConnection;

            //do soemthing with pendingConnection which is now the incoming one
            answer();
            //answers always since only users can get called
        }
    }
    /*
     * Receive intent for incoming call from Twilio Client Service
     * Android will only call Activity.onNewIntent() if `android:launchMode` is set to `singleTop`.
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /*
     * Request a Capability Token from your public accessible server
     */
    protected void retrieveCapabilityToken(final ClientProfile newClientProfile) {

        // Correlate desired properties of the Device (from ClientProfile) to properties of the Capability Token
        Uri.Builder b = Uri.parse(TOKEN_SERVICE_URL).buildUpon();
        if (newClientProfile.isAllowOutgoing()) {
            b.appendQueryParameter("allowOutgoing", newClientProfile.allowOutgoing ? "true" : "false");
        }
        if (newClientProfile.isAllowIncoming() && newClientProfile.getName() != null) {
            b.appendQueryParameter("client", newClientProfile.getName());
        }

        Ion.with(getApplicationContext())
                .load(b.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String capabilityToken) {
                        if (e == null) {
                            Log.d(TAG, capabilityToken);

                            // Update the current Client Profile to represent current properties
                            AudioCallActivity.this.clientProfile = newClientProfile;

                            // Create a Device with the Capability Token
                            createDevice(capabilityToken);
                        } else {
                            Log.e(TAG, "Error retrieving token: " + e.toString());
                            Toast.makeText(AudioCallActivity.this, "Error retrieving token", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
     * Create an outgoing connection
     */
    protected void connect(String contact, boolean isPhoneNumber) {
        // Determine if you're calling another client or a phone number
        if (!isPhoneNumber){
            contact = "client:" + contact.trim();

        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("To", contact);

        if (clientDevice != null) {
            // Create an outgoing connection
            connection = clientDevice.connect(params, this);
            //setCallUI();
        } else {
            Toast.makeText(AudioCallActivity.this, "No existing device", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Disconnect an active connection
     */
    private void disconnect() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    /*
     * Accept an incoming connection
     */
    private void answer() {
        //always accept, but what if already have connection???
        pendingConnection.accept();
        pendingConnection.setConnectionListener(this);
        connection = pendingConnection;
        pendingConnection = null;
    }
    /* Device Listener */
    @Override
    public void onStartListening(Device device) {
        Log.d(TAG, "Device has started listening for incoming connections");
    }

    /* Device Listener */
    @Override
    public void onStopListening(Device device) {
        Log.d(TAG, "Device has stopped listening for incoming connections");
    }

    /* Device Listener */
    @Override
    public void onStopListening(Device device, int errorCode, String error) {
        Log.e(TAG, String.format("Device has encountered an error and has stopped" +
                " listening for incoming connections: %s", error));
    }

    /* Device Listener */
    @Override
    public boolean receivePresenceEvents(Device device) {
        return false;
    }

    /* Device Listener */
    @Override
    public void onPresenceChanged(Device device, PresenceEvent presenceEvent) {
    }

    /* Connection Listener */
    @Override
    public void onConnecting(Connection connection) {
        Log.d(TAG, "Attempting to connect");
    }

    /* Connection Listener */
    @Override
    public void onConnected(Connection connection) {
        Log.d(TAG, "Connected");
    }

    /* Connection Listener */
    @Override
    public void onDisconnected(Connection inConnection) {
        if (connection != null && inConnection != null) {
            if (connection == inConnection) {
                connection = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            Log.d(TAG, "Disconnect");
        }
    }

    /* Connection Listener */
    @Override
    public void onDisconnected(Connection inConnection, int errorCode, String error) {
        // A connection other than active connection could have errored out.
        if (connection != null && inConnection != null) {
            if (connection == inConnection) {
                connection = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            Log.e(TAG, String.format("Connection error: %s", error));
        }
    }
    protected boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (resultMic == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    protected void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    "Microphone permissions needed. Please allow in App Settings for additional functionality.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            boolean granted = true;
            if (granted) {
                /*
                * Initialize the Twilio Client SDK
                */
                initializeTwilioClientSDK();
            } else {
                Toast.makeText(this,
                        "Microphone permissions needed. Please allow in App Settings for additional functionality.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }


    private View.OnClickListener muteMicrophoneFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 *  Mute/unmute microphone
                 */
                muteMicrophone = !muteMicrophone;
                if (connection != null) {
                    connection.setMuted(muteMicrophone);
                }
                if (muteMicrophone) {
                    muteActionFab.setImageDrawable(ContextCompat.getDrawable(AudioCallActivity.this, R.drawable.ic_mic_off_red_24px));
                } else {
                    muteActionFab.setImageDrawable(ContextCompat.getDrawable(AudioCallActivity.this, R.drawable.ic_mic_green_24px));
                }
            }
        };
    }

    private View.OnClickListener toggleSpeakerPhoneFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Audio routing to speakerphone or headset
                 */
                speakerPhone = !speakerPhone;

                setAudioFocus(true);
                audioManager.setSpeakerphoneOn(speakerPhone);

                if (speakerPhone) {
                    speakerActionFab.setImageDrawable(ContextCompat.getDrawable(AudioCallActivity.this, R.drawable.ic_videocam_green_24px));
                } else {
                    speakerActionFab.setImageDrawable(ContextCompat.getDrawable(AudioCallActivity.this, R.drawable.ic_videocam_off_red_24px));
                }
            }
        };
    }

    private View.OnClickListener hangupActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
                finish();

            }
        };
    }
}