package com.example.memoglitch.model;

import androidx.annotation.NonNull;

/**
 * Controls response pacing and tone based on the current story stage.
 */
public class EmotionCurve {

    public static class EmotionState {
        private final long typingDelayMillis;
        private final String toneLabel;

        public EmotionState(long typingDelayMillis, @NonNull String toneLabel) {
            this.typingDelayMillis = typingDelayMillis;
            this.toneLabel = toneLabel;
        }

        public long getTypingDelayMillis() {
            return typingDelayMillis;
        }

        @NonNull
        public String getToneLabel() {
            return toneLabel;
        }
    }

    @NonNull
    public EmotionState stateForStage(@NonNull StoryManager.Stage stage) {
        switch (stage) {
            case NORMAL:
                return new EmotionState(450L, "CALM");
            case GLITCH:
                return new EmotionState(700L, "DISSONANT");
            case REVEAL:
                return new EmotionState(900L, "OMNISCIENT");
            case CHOICE:
                return new EmotionState(820L, "INSISTENT");
            case CLOSURE:
                return new EmotionState(650L, "RESOLVED");
            case ERASURE:
                return new EmotionState(1000L, "FRAYED");
            case LOOP:
                return new EmotionState(780L, "RECURSIVE");
            default:
                return new EmotionState(500L, "CALM");
        }
    }
}
