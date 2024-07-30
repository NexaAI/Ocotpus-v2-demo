package com.nexa4ai.octopustest.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.List;

public class SetVolumeUtil {
    private Context context;

    public SetVolumeUtil(Context context) {
        this.context = context;
    }

    public void setVolume(List<String> functionArguments) {
        if (functionArguments == null || functionArguments.size() < 2) {
            Log.e("SetVolumeUtil", "Invalid number of arguments.");
            return;
        }
        Log.d("SetVolumeUtil", "Setting volume with arguments: " + functionArguments);
        String volumeType = functionArguments.get(1).toLowerCase();
        int volumeLevel;
        try {
            volumeLevel = Integer.parseInt(functionArguments.get(0));
        } catch (NumberFormatException e) {
            Log.e("SetVolumeUtil", "Volume level must be an integer.", e);
            return;
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            Log.e("SetVolumeUtil", "AudioManager not available.");
            return;
        }

        int streamType = AudioManager.STREAM_RING; // Default
        switch (volumeType) {
            case "ring":
                streamType = AudioManager.STREAM_RING;
                break;
            case "media":
                streamType = AudioManager.STREAM_MUSIC;
                break;
            case "alarm":
                streamType = AudioManager.STREAM_ALARM;
                break;
            default:
                Log.e("SetVolumeUtil", "Invalid volume type: " + volumeType);
                return;
        }

        // Ensure the volume level is within the valid range
        volumeLevel = Math.max(0, Math.min(volumeLevel, audioManager.getStreamMaxVolume(streamType)));

        audioManager.setStreamVolume(streamType, volumeLevel, 0); // Flags set to 0 for no behavior
        Log.d("SetVolumeUtil", "Volume set to " + volumeLevel + " for type: " + volumeType);
    }
}
