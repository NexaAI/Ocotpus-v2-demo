package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SendSMSMessageUtil {

    private static final String TAG = "ChatApp";

    private final Context context;

    public SendSMSMessageUtil(Context context) {
        this.context = context;
    }

    public void sendSmsMessage(List<String> functionArguments) {
        if (functionArguments.size() < 2) return;

        String phoneNumber = functionArguments.get(0);
        String message = functionArguments.get(1);
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String resultMessage = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        resultMessage = "SMS Sent Successfully!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        resultMessage = "Generic Failure";
                        Log.d(TAG, "SMS send failure: Generic failure.");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        resultMessage = "No Service";
                        Log.d(TAG, "SMS send failure: No service.");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        resultMessage = "Null PDU";
                        Log.d(TAG, "SMS send failure: Null PDU.");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        resultMessage = "Radio Off";
                        Log.d(TAG, "SMS send failure: Radio off.");
                        break;
                }
                Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(SENT));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String deliveryResultMessage = "Delivery result unknown";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        deliveryResultMessage = "SMS Delivered";
                        break;
                    case Activity.RESULT_CANCELED:
                        deliveryResultMessage = "SMS Not Delivered";
                        Log.d(TAG, "SMS delivery failure: SMS not delivered.");
                        break;
                }
                Toast.makeText(context, deliveryResultMessage, Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(DELIVERED));

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        } catch (Exception e) {
            Log.e(TAG, "SMS Failed to Send!", e);
            Toast.makeText(context, "SMS Failed to Send! Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
