package com.example.memoglitch.model;

import androidx.annotation.NonNull;

/**
 * Represents a single message in the conversation between the player and Echo.
 */
public class Message {

    public enum Sender {
        USER,
        AI
    }

    private final Sender sender;
    private final String text;
    private final boolean glitch;
    private final long timestamp;
    private final StoryManager.Stage stageAtSend;

    public Message(@NonNull Sender sender, @NonNull String text, boolean glitch,
                   long timestamp, @NonNull StoryManager.Stage stageAtSend) {
        this.sender = sender;
        this.text = text;
        this.glitch = glitch;
        this.timestamp = timestamp;
        this.stageAtSend = stageAtSend;
    }

    @NonNull
    public Sender getSender() {
        return sender;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public boolean isGlitch() {
        return glitch;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NonNull
    public StoryManager.Stage getStageAtSend() {
        return stageAtSend;
    }
}
