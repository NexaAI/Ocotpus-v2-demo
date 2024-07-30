package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

public class CallPhoneUtil {
    private Activity activity;

    public CallPhoneUtil(Activity activity) {
        this.activity = activity;
    }

    public void makePhoneCall(List<String> functionArguments) {
        if (functionArguments.isEmpty()) return;
        String phoneNumber = functionArguments.get(0);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }
}
