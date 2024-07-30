package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class EnableDoNotDisturbUtil {
    private final Activity activity;

    public EnableDoNotDisturbUtil(Activity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = 23)
    public void setDoNotDisturb(List<String> functionArguments) {
        if (functionArguments.size() < 1) return;

        boolean enabled = functionArguments.get(0).equals("True") ? true : false;

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            activity.startActivity(intent);
            return;
        }

        int interruptionFilter = enabled ? NotificationManager.INTERRUPTION_FILTER_NONE :
                NotificationManager.INTERRUPTION_FILTER_ALL;
        notificationManager.setInterruptionFilter(interruptionFilter);

        String toastText = enabled ? "Do not disturb on" : "Do not disturb off";
        Toast.makeText(activity, toastText, Toast.LENGTH_LONG).show();
    }
}
