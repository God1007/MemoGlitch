package com.example.memoglitch.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles local persistence using SharedPreferences.
 */
public class DataRepository {

    private static final String PREF_NAME = "identity_glitch_prefs";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_STAGE = "stage";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_FALSE_MEMORY_SHARED = "false_memory";
    private static final float DEFAULT_TEXT_SIZE = 14f;

    private final SharedPreferences preferences;

    public DataRepository(@NonNull Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveMessages(@NonNull List<Message> messages) {
        JSONArray array = new JSONArray();
        for (Message message : messages) {
            JSONObject object = new JSONObject();
            try {
                object.put("sender", message.getSender().name());
                object.put("text", message.getText());
                object.put("glitch", message.isGlitch());
                object.put("timestamp", message.getTimestamp());
                object.put("stage", message.getStageAtSend().name());
            } catch (JSONException e) {
                // Skip this message if serialization fails.
                continue;
            }
            array.put(object);
        }
        preferences.edit().putString(KEY_MESSAGES, array.toString()).apply();
    }

    @NonNull
    public List<Message> loadMessages() {
        String json = preferences.getString(KEY_MESSAGES, null);
        List<Message> messages = new ArrayList<>();
        if (json == null) {
            return messages;
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object == null) {
                    continue;
                }
                String senderString = object.optString("sender", Message.Sender.AI.name());
                String text = object.optString("text", "");
                boolean glitch = object.optBoolean("glitch", false);
                long timestamp = object.optLong("timestamp", System.currentTimeMillis());
                String stageString = object.optString("stage", StoryManager.Stage.NORMAL.name());
                Message.Sender sender = Message.Sender.valueOf(senderString);
                StoryManager.Stage stage = StoryManager.Stage.valueOf(stageString);
                messages.add(new Message(sender, text, glitch, timestamp, stage));
            }
        } catch (JSONException ignored) {
        }
        return messages;
    }

    public void saveStage(@NonNull StoryManager.Stage stage) {
        preferences.edit().putString(KEY_STAGE, stage.name()).apply();
    }

    @NonNull
    public StoryManager.Stage loadStage() {
        String stage = preferences.getString(KEY_STAGE, StoryManager.Stage.NORMAL.name());
        return StoryManager.Stage.valueOf(stage);
    }

    public void saveFalseMemoryShared(boolean shared) {
        preferences.edit().putBoolean(KEY_FALSE_MEMORY_SHARED, shared).apply();
    }

    public boolean wasFalseMemoryShared() {
        return preferences.getBoolean(KEY_FALSE_MEMORY_SHARED, false);
    }

    public void saveTextSize(float size) {
        preferences.edit().putFloat(KEY_TEXT_SIZE, size).apply();
    }

    public float loadTextSize() {
        return preferences.getFloat(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
    }

    public void saveVibrationEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return preferences.getBoolean(KEY_VIBRATION, true);
    }

    public void clearSession() {
        preferences.edit()
                .remove(KEY_MESSAGES)
                .remove(KEY_STAGE)
                .remove(KEY_FALSE_MEMORY_SHARED)
                .apply();
    }
}
