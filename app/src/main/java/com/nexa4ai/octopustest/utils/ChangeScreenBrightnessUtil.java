package com.nexa4ai.octopustest.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

public class ChangeScreenBrightnessUtil {
    private Context context;

    public ChangeScreenBrightnessUtil(Context context) {
        this.context = context;
    }

    public void changeScreenBrightness(List<String> functionArguments) {
        if (functionArguments == null || functionArguments.isEmpty()) {
            Log.e("ChangeScreenBrightnessUtil", "No arguments provided.");
            return;
        }
        Log.d("ChangeScreenBrightnessUtil", "Changing brightness with arguments: " + functionArguments);
        int brightnessLevelInput;
        try {
            brightnessLevelInput = Integer.parseInt(functionArguments.get(0));
        } catch (NumberFormatException e) {
            Log.e("ChangeScreenBrightnessUtil", "Brightness level input must be an integer.", e);
            return;
        }

        if (!Settings.System.canWrite(context)) {
            Log.e("ChangeScreenBrightnessUtil", "WRITE_SETTINGS permission not granted.");
            return;
        }

        int brightnessLevel = mapBrightnessLevel(brightnessLevelInput);

        boolean result = Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessLevel);
        if (result) {
            Log.d("ChangeScreenBrightnessUtil", "Brightness successfully set to " + brightnessLevel);
        } else {
            Log.e("ChangeScreenBrightnessUtil", "Failed to set brightness.");
        }
    }

    private int mapBrightnessLevel(int inputLevel) {
        inputLevel = Math.max(0, Math.min(inputLevel, 10));
        return (int) (inputLevel * 25.5);
    }
}
