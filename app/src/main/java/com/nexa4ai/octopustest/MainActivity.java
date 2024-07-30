package com.nexa4ai.octopustest;

import static com.nexa4ai.octopustest.utils.TakePhotoUtil.REQUEST_TAKE_PHOTO;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nexa4ai.octopustest.utils.CallPhoneUtil;
import com.nexa4ai.octopustest.utils.ChangeScreenBrightnessUtil;
import com.nexa4ai.octopustest.utils.EnableDoNotDisturbUtil;
import com.nexa4ai.octopustest.utils.SendEmailUtil;
import com.nexa4ai.octopustest.utils.SendSMSMessageUtil;
import com.nexa4ai.octopustest.utils.SetTimerAlarmUtil;
import com.nexa4ai.octopustest.utils.SetVolumeUtil;
import com.nexa4ai.octopustest.utils.TakePhotoUtil;
import com.nexa4ai.octopustest.utils.WriteContactsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChatApp";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final int SMS_PERMISSION = 201;

    private static final int CALL_PHONE_PERMISSION = 202;

    private static final int READ_CONTACTS_PERMISSION = 2031;

    private static final int WRITE_CONTACTS_PERMISSION = 2032;

    private static final int WAKE_LOCK_PERMISSION = 2041;

    private static final int ACCESS_NOTIFICATION_POLICY = 208;
    private static final int CAMERA_PERMISSION = 209;

    private List<String> functionArguments;


    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;

    private LinearLayout linearLayout;
    private TextView titleAfterChatTextView;
    private RecyclerView recyclerView;

    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    private MessageHandler messageHandler;
    private ApiClient apiClient;

    private SpeechRecognizer speechRecognizer;

    // The util functions
    private SendSMSMessageUtil sendSMSMessageUtil;

    private CallPhoneUtil callPhoneUtil;

    private WriteContactsUtil writeContactsUtil;

    private SetTimerAlarmUtil setTimerAlarmUtil;

    private ChangeScreenBrightnessUtil changeScreenBrightnessUtil;

    private SetVolumeUtil setVolumeUtil;
    private EnableDoNotDisturbUtil enableDoNotDisturbUtil;

    private TakePhotoUtil takePhotoUtil;

    private SendEmailUtil sendEmailUtil;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize util function classes
        sendSMSMessageUtil = new SendSMSMessageUtil(this);
        callPhoneUtil = new CallPhoneUtil(this);
        writeContactsUtil = new WriteContactsUtil(this);
        setTimerAlarmUtil = new SetTimerAlarmUtil(this);
        changeScreenBrightnessUtil = new ChangeScreenBrightnessUtil(this);
        setVolumeUtil = new SetVolumeUtil(this);
        enableDoNotDisturbUtil = new EnableDoNotDisturbUtil(this);
        takePhotoUtil = new TakePhotoUtil(this);
        sendEmailUtil = new SendEmailUtil(this);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);

        linearLayout = findViewById(R.id.idLayoutBeforeChat);
        titleAfterChatTextView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.idRVChats);

        createSpeechRecognizerIntent();

        // Initialize your ArrayList for messages
        messageModalArrayList = new ArrayList<>();

        // Setup RecyclerView with its adapter and layout manager
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        chatsRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        chatsRV.setAdapter(messageRVAdapter);

        // Initialize your MessageHandler with the list and adapter
        messageHandler = new MessageHandler(messageModalArrayList, messageRVAdapter);

        // Finally, initialize your ApiClient with the current context and MessageHandler
        apiClient = new ApiClient(this, messageHandler);

        // Set up the button's onClickListener to send messages
        sendMsgIB.setOnClickListener(v -> sendMessage());

        Log.d(TAG, "onCreate: MainActivity setup complete");
    }

    private void updateChatBotDisplay() {
        linearLayout.setVisibility(View.GONE);
        titleAfterChatTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    // helper function to send message
    private void sendMessage() {
        updateChatBotDisplay();

        String userMessage = userMsgEdt.getText().toString().trim();
        if (!userMessage.isEmpty()) {
            Log.d(TAG, "Sending message: " + userMessage);
            // Use ApiClient to send the message
            apiClient.sendMessage(userMessage, new ApiClient.ApiResponseCallback() {
                @Override
                public void onSuccess(String functionName, List<String> functionArguments) {
                    Log.d(TAG, "API Response onSuccess: Function Name - " + functionName);
                    Log.d(TAG, "API Response onSuccess: Function Arguments - " + functionArguments);
                    MainActivity.this.functionArguments = functionArguments;
                    switch (functionName) {
                        case "send_text_message":
                            Log.d(TAG, "calling send_text_message");
                            checkSmsPermission();
                            break;
                        case "make_phone_call":
                            Log.d(TAG, "calling make_phone_call");
                            checkCallPhonePermission();
                            break;
                        case "create_contact":
                            Log.d(TAG, "calling create_contact");
                            checkReadAndWriteContactPermission();
                            break;
                        case "set_timer_alarm":
                            Log.d(TAG, "calling set_timer_alarm");
                            checkSetTimerAlarmPermission();
                            break;
                        case "change_screen_brightness":
                            Log.d(TAG, "calling change_screen_brightness");
                            checkChangeScreenBrightnessPermission();
                            break;
                        case "create_calendar_event":
                            Log.d(TAG, "calling create_calendar_event");
                            // TODO: Implement create_calendar_event
                            break;
                        case "set_volume":
                            Log.d(TAG, "calling set_volume");
                            checkSetVolumePermission();
                            break;
                        case "connect_to_bluetooth_device":
                            Log.d(TAG, "calling connect_to_bluetooth");
                            // TODO: Implement connect_to_bluetooth_device
                            break;
                        case "enable_do_not_disturb":
                            Log.d(TAG, "calling enable_do_not_disturb");
                            checkNotificationPermission();
                            break;
                        case "take_a_photo":
                            Log.d(TAG, "calling take_a_photo");
                            checkCameraPermission();
                            break;
                        case "send_email":
                            Log.d(TAG, "calling send_email");
                            sendEmailUtil.sendEmail(functionArguments);
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "API Response onFailure: " + message);
                }
            });
            userMsgEdt.setText(""); // Clear the input field after sending
        } else {
            Toast.makeText(MainActivity.this, "Please enter your message.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createSpeechRecognizerIntent() {
        requestMicrophonePermission();

        ImageButton btnStart = findViewById(R.id.btnStart);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new android.speech.RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                String errorMessage = getErrorText(error);
                Log.d("SpeechRecognition", "Error occurred: " + errorMessage);
            }

            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "Error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                return message;
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    userMsgEdt.setText(matches.get(0)); // Set the recognized text to the EditText
                    sendMessage();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // This is called for partial results
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (partialMatches != null && !partialMatches.isEmpty()) {
                    userMsgEdt.setText(partialMatches.get(0)); // Update EditText with the partial result
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button is pressed
                        speechRecognizer.startListening(speechRecognizerIntent);
                        return true; // Return true to indicate the event was handled
                    case MotionEvent.ACTION_UP:
                        // Button is released
                        speechRecognizer.stopListening();
                        return true; // Return true to indicate the event was handled
                }
                return false; // Return false for other actions
            }
        });
    }

    private void requestMicrophonePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION);
        } else {
            sendSMSMessageUtil.sendSmsMessage(functionArguments);
        }
    }

    private void checkCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
        } else {
            callPhoneUtil.makePhoneCall(functionArguments);
        }
    }


    private void checkReadAndWriteContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION);
        } else {
            writeContactsUtil.writeContact(functionArguments);
        }
    }

    private void checkSetTimerAlarmPermission() {
        setTimerAlarmUtil.setTimerAlarm(functionArguments);
    }

    private void checkChangeScreenBrightnessPermission() {
        changeScreenBrightnessUtil.changeScreenBrightness(functionArguments);
    }

    private void checkSetVolumePermission() {
        setVolumeUtil.setVolume(functionArguments);
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_NOTIFICATION_POLICY);
        } else {
            enableDoNotDisturbUtil.setDoNotDisturb(functionArguments);
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            takePhotoUtil.takePhoto(functionArguments);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
                    sendSMSMessageUtil.sendSmsMessage(functionArguments);
                } else {
                    Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case CALL_PHONE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Call phone Permission Granted", Toast.LENGTH_SHORT).show();
                    callPhoneUtil.makePhoneCall(functionArguments);
                } else {
                    Toast.makeText(this, "Call phone Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_CONTACTS_PERMISSION:
            case READ_CONTACTS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Write or read contact Permission Granted", Toast.LENGTH_SHORT).show();
                    writeContactsUtil.writeContact(functionArguments);
                } else {
                    Toast.makeText(this, "Write or read contact Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case WAKE_LOCK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Set Timer Alarm Permission Granted", Toast.LENGTH_SHORT).show();
                    setTimerAlarmUtil.setTimerAlarm(functionArguments);
                } else {
                    Toast.makeText(this, "Set Timer Alarm Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case ACCESS_NOTIFICATION_POLICY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show();
                    enableDoNotDisturbUtil.setDoNotDisturb(functionArguments);
                } else {
                    Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                    takePhotoUtil.takePhoto(functionArguments);
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            takePhotoUtil.addToGallery();
        }
    }

}