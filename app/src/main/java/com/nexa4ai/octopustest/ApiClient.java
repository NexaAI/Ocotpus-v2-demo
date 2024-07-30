package com.nexa4ai.octopustest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiClient {
    private static final String TAG = "ChatApp";
    private static final String MODEL_ENDPOINT = "https://api.nexa4ai.com/android";
    private final OkHttpClient client;
    private final Context context;
    private final MessageHandler messageHandler;

    public ApiClient(Context context, MessageHandler messageHandler) {
        this.client = new OkHttpClient();
        this.context = context;
        this.messageHandler = messageHandler;
    }

    public void sendMessage(String userMsg, ApiResponseCallback callback) {
        Log.d(TAG, "sendMessage: Start - " + userMsg);
        messageHandler.addMessage(new MessageModal(userMsg, "user"));

        JsonObject payload = new JsonObject();
        payload.addProperty("input_text", userMsg);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(MODEL_ENDPOINT)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "sendMessage: API call failed", e);
                ((MainActivity) context).runOnUiThread(() -> callback.onFailure("API call failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "sendMessage: onResponse failed - " + response);
                    ((MainActivity) context).runOnUiThread(() -> callback.onFailure("Response unsuccessful"));
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        String jsonResponse = responseBody.string();

                        // Log the full JSON string
                        Log.d(TAG, "Full JSON Response: " + jsonResponse);

                        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
                        String botMessage = "Function: " + jsonObject.get("function_name").getAsString() +
                                "\nResult: " + jsonObject.get("result").toString() +
                                "\nLatency:" + jsonObject.get("latency").toString() + " s";
                        String functionName = jsonObject.get("function_name").getAsString();
                        Log.d(TAG, "functionName: " + functionName);
                        Type listType = new TypeToken<List<String>>() {
                        }.getType();

                        // Convert JsonArray to List<String>, assuming JSON is correctly formatted
                        List<String> functionArguments = new Gson().fromJson(jsonObject.get("function_arguments"),
                                listType);
                        ((MainActivity) context)
                                .runOnUiThread(() -> callback.onSuccess(functionName, functionArguments));

                        String argumentsAsString = TextUtils.join(", ", functionArguments);
                        Log.d(TAG, "Function Arguments: " + argumentsAsString);

                        // Use the botMessage within runOnUiThread
                        ((MainActivity) context).runOnUiThread(() -> {
                            messageHandler.addMessage(new MessageModal(botMessage, "bot"));
                        });
                    } else {
                        Log.e(TAG, "handleResponseSuccess: Response body is null");
                        handleResponseFailure();
                    }
                }
            }
        });
        Log.d(TAG, "sendMessage: End");
    }

    private void handleResponseFailure() {
        Log.e(TAG, "handleResponseFailure: Handling failure");
        ((MainActivity) context).runOnUiThread(() -> {
            Toast.makeText(context, "No response from the bot.", Toast.LENGTH_SHORT).show();
            messageHandler.addMessage(new MessageModal("Sorry, no response found", "bot"));
        });
    }

    public interface ApiResponseCallback {
        void onSuccess(String functionName, List<String> functionArguments);

        void onFailure(String message);
    }
}
