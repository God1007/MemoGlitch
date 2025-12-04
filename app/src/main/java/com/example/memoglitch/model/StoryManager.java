package com.example.memoglitch.model;

import androidx.annotation.NonNull;

/**
 * Controls narrative progression through the Normal → Glitch → Revelation phases.
 */
public class StoryManager {

    public enum Stage {
        NORMAL,
        GLITCH,
        REVEAL,
        CHOICE,
        CLOSURE,
        ERASURE,
        LOOP
    }

    private Stage currentStage = Stage.NORMAL;
    private int userMessageCount;
    private boolean firstFalseMemoryShared;
    private int stageEntryUserMessageCount;
    private boolean finalStageLocked;

    public StoryManager() {
    }

    public void reset() {
        currentStage = Stage.NORMAL;
        userMessageCount = 0;
        firstFalseMemoryShared = false;
        stageEntryUserMessageCount = 0;
        finalStageLocked = false;
    }

    public void registerUserMessage(@NonNull String userInput) {
        userMessageCount++;
        if (finalStageLocked) {
            return;
        }
        if (currentStage == Stage.NORMAL && shouldEnterGlitchPhase(userInput)) {
            updateStage(Stage.GLITCH);
        } else if (currentStage == Stage.GLITCH && shouldEnterRevealPhase()) {
            updateStage(Stage.REVEAL);
        } else if (currentStage == Stage.REVEAL && shouldEnterChoicePhase()) {
            updateStage(Stage.CHOICE);
        } else if (currentStage == Stage.CHOICE) {
            Stage destination = determineFinalStage(userInput);
            if (destination != null) {
                updateStage(destination);
            }
        }
    }

    private boolean shouldEnterGlitchPhase(@NonNull String userInput) {
        String normalized = userInput.trim().toLowerCase();
        return userMessageCount >= 8 || normalized.contains("memory") || normalized.contains("dream")
                || normalized.contains("echo");
    }

    private boolean shouldEnterRevealPhase() {
        return firstFalseMemoryShared && messagesSinceStageEntry() >= 6 && userMessageCount >= 11;
    }

    private boolean shouldEnterChoicePhase() {
        return firstFalseMemoryShared && messagesSinceStageEntry() >= 6;
    }

    private Stage determineFinalStage(@NonNull String userInput) {
        String normalized = userInput.trim().toLowerCase(java.util.Locale.US);
        if (normalized.isEmpty()) {
            if (messagesSinceStageEntry() >= 5) {
                return Stage.LOOP;
            }
            return null;
        }
        if (containsAny(normalized, "stay", "remember", "together", "trust", "listen")) {
            return Stage.CLOSURE;
        }
        if (containsAny(normalized, "erase", "forget", "leave", "shutdown", "goodbye")) {
            return Stage.ERASURE;
        }
        if (containsAny(normalized, "loop", "again", "restart", "repeat")) {
            return Stage.LOOP;
        }
        if (messagesSinceStageEntry() >= 6) {
            return Stage.LOOP;
        }
        return null;
    }

    private boolean containsAny(@NonNull String text, @NonNull String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private int messagesSinceStageEntry() {
        return Math.max(0, userMessageCount - stageEntryUserMessageCount);
    }

    private void updateStage(@NonNull Stage stage) {
        currentStage = stage;
        stageEntryUserMessageCount = userMessageCount;
        finalStageLocked = isFinalStage(stage);
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
        this.stageEntryUserMessageCount = userMessageCount;
        this.finalStageLocked = isFinalStage(stage);
    }

    public void setUserMessageCount(int count) {
        this.userMessageCount = count;
        if (stageEntryUserMessageCount > userMessageCount) {
            stageEntryUserMessageCount = userMessageCount;
        }
    }

    public void setFirstFalseMemoryShared(boolean shared) {
        this.firstFalseMemoryShared = shared;
        if (!shared && currentStage.ordinal() > Stage.GLITCH.ordinal()) {
            currentStage = Stage.GLITCH;
            stageEntryUserMessageCount = userMessageCount;
            finalStageLocked = false;
        }
    }

    private boolean isFinalStage(@NonNull Stage stage) {
        return stage == Stage.CLOSURE || stage == Stage.ERASURE || stage == Stage.LOOP;
    }
}
