package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

public class SendEmailUtil {

    private final Activity activity;

    public SendEmailUtil(Activity activity) {
        this.activity = activity;
    }

    public void sendEmail(List<String> functionArguments) {
        if (functionArguments.size() < 2) return;

        String to = functionArguments.get(0);
        String subject = functionArguments.get(1);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (functionArguments.size() >= 3) {
            StringBuilder bodyBuilder = new StringBuilder();
            // Start from index 2 to concatenate all elements as the body
            for (int i = 2; i < functionArguments.size(); i++) {
                if (i > 2) {
                    // Add a newline for all elements after the first body element
                    bodyBuilder.append("\n");
                }
                bodyBuilder.append(functionArguments.get(i));
            }
            String body = bodyBuilder.toString();
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }

        activity.startActivity(intent);
    }
}
