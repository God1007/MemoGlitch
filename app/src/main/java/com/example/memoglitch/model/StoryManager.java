package com.example.memoglitch.model;

import androidx.annotation.NonNull;

/**
 * Controls narrative progression through the Normal → Glitch → Revelation phases.
 */
public class StoryManager {

    public enum Stage {
        NORMAL,
        GLITCH,
        REVEAL
    }

    private Stage currentStage = Stage.NORMAL;
    private int userMessageCount;
    private boolean firstFalseMemoryShared;

    public StoryManager() {
    }

    public void reset() {
        currentStage = Stage.NORMAL;
        userMessageCount = 0;
        firstFalseMemoryShared = false;
    }

    public void registerUserMessage(@NonNull String userInput) {
        userMessageCount++;
        if (currentStage == Stage.NORMAL && shouldEnterGlitchPhase(userInput)) {
            currentStage = Stage.GLITCH;
        } else if (currentStage == Stage.GLITCH && shouldEnterRevealPhase()) {
            currentStage = Stage.REVEAL;
        }
    }

    private boolean shouldEnterGlitchPhase(@NonNull String userInput) {
        String normalized = userInput.trim().toLowerCase();
        return userMessageCount >= 3 || normalized.contains("memory") || normalized.contains("dream");
    }

    private boolean shouldEnterRevealPhase() {
        return userMessageCount >= 7 && firstFalseMemoryShared;
    }

    public void setFirstFalseMemoryShared() {
        this.firstFalseMemoryShared = true;
    }

    @NonNull
    public Stage getCurrentStage() {
        return currentStage;
    }

    public int getUserMessageCount() {
        return userMessageCount;
    }

    public void setStage(@NonNull Stage stage) {
        this.currentStage = stage;
    }

    public void setUserMessageCount(int count) {
        this.userMessageCount = count;
    }

    public void setFirstFalseMemoryShared(boolean shared) {
        this.firstFalseMemoryShared = shared;
    }
}
