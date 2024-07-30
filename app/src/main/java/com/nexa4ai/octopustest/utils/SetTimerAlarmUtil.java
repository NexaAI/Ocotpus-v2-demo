package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SetTimerAlarmUtil {
    private Activity activity;

    public SetTimerAlarmUtil(Activity activity) {
        this.activity = activity;
    }

    public void setTimerAlarm(List<String> functionArguments) {
        if (functionArguments == null || functionArguments.isEmpty()) {
            Log.e("SetTimerAlarmUtil", "The functionArguments list is empty.");
            return;
        }

        String time = functionArguments.get(0); // Get the first time from the list
        String[] parts = time.split(":");
        if (parts.length != 2) {
            Log.e("SetTimerAlarmUtil", "The time format is incorrect. Expected HH:MM.");
            return;
        }

        int hour, minute;
        try {
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            Log.e("SetTimerAlarmUtil", "Failed to parse time components.", e);
            return;
        }

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true); // Skip the UI when setting the alarm

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
            Toast.makeText(activity, "Alarm set for " + time, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "No alarm clock app found.", Toast.LENGTH_SHORT).show();
        }
    }
}
