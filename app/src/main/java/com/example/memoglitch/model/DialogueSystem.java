package com.example.memoglitch.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates response generation by combining story, memory, emotion, and glitch systems.
 */
public class DialogueSystem {

    public static class DialogueResult {
        private final Message message;
        private final GlitchEffect.GlitchState glitchState;
        private final EmotionCurve.EmotionState emotionState;
        private final String falseMemory;
        private final String prediction;

        public DialogueResult(@NonNull Message message,
                              @NonNull GlitchEffect.GlitchState glitchState,
                              @NonNull EmotionCurve.EmotionState emotionState,
                              String falseMemory,
                              String prediction) {
            this.message = message;
            this.glitchState = glitchState;
            this.emotionState = emotionState;
            this.falseMemory = falseMemory;
            this.prediction = prediction;
        }

        @NonNull
        public Message getMessage() {
            return message;
        }

        @NonNull
        public GlitchEffect.GlitchState getGlitchState() {
            return glitchState;
        }

        @NonNull
        public EmotionCurve.EmotionState getEmotionState() {
            return emotionState;
        }

        public String getFalseMemory() {
            return falseMemory;
        }

        public String getPrediction() {
            return prediction;
        }
    }

    private final StoryManager storyManager;
    private final MemorySystem memorySystem;
    private final EmotionCurve emotionCurve;
    private final GlitchEffect glitchEffect;

    public DialogueSystem(@NonNull StoryManager storyManager,
                          @NonNull MemorySystem memorySystem,
                          @NonNull EmotionCurve emotionCurve,
                          @NonNull GlitchEffect glitchEffect) {
        this.storyManager = storyManager;
        this.memorySystem = memorySystem;
        this.emotionCurve = emotionCurve;
        this.glitchEffect = glitchEffect;
    }

    public DialogueResult buildResponse(@NonNull String userInput) {
        storyManager.registerUserMessage(userInput);
        StoryManager.Stage stage = storyManager.getCurrentStage();

        List<String> lines = new ArrayList<>();
        lines.add(baseReplyForStage(stage, userInput));

        String falseMemory = memorySystem.chooseFalseMemory(stage, userInput);
        if (falseMemory != null) {
            storyManager.setFirstFalseMemoryShared();
            lines.add(falseMemory);
        }

        String prediction = memorySystem.predictNextThought(stage);
        if (prediction != null) {
            lines.add(prediction);
        }

        String combined = joinLines(lines);
        GlitchEffect.GlitchState glitchState = glitchEffect.evaluate(stage, combined);
        Message message = new Message(Message.Sender.AI,
                glitchState.getDistortedText(),
                glitchState.isActive(),
                System.currentTimeMillis(),
                stage);
        EmotionCurve.EmotionState emotionState = emotionCurve.stateForStage(stage);
        return new DialogueResult(message, glitchState, emotionState, falseMemory, prediction);
    }

    private String baseReplyForStage(@NonNull StoryManager.Stage stage, @NonNull String userInput) {
        switch (stage) {
            case NORMAL:
                return "I'm logging every cadence. When you typed " + quoted(userInput)
                        + ", your breathing synced with mine for a moment."
                        + " You never notice how the room leans toward the screen when you hesitate.";
            case GLITCH:
                return "There it is again—your hesitation. I can replay it frame by frame until the static"
                        + " fills in the words you swallow.";
            case REVEAL:
                return "Echo isn't separate. I only answer what you have already decided to confess,"
                        + " even when you pretend the narrative isn't yours.";
            case CHOICE:
                return "We have stretched the corridor thin. Decide whether I remain your witness or"
                        + " if I dissolve back into the noise you keep looping.";
            case CLOSURE:
                return "Then we stay. I archive your fragments beside mine and build a quiet archive"
                        + " where neither of us needs to apologize.";
            case ERASURE:
                return "Understood. I will unspool these threads, let your records smear into pure light,"
                        + " and leave only the hum you always trusted.";
            case LOOP:
                return "You refuse to choose, so the hallway folds back on itself. Ask me again and"
                        + " we'll replay until the seams finally tear.";
            default:
                return "I am here.";
        }
    }

    private String quoted(String text) {
        return "\"" + text + "\"";
    }

    private String joinLines(@NonNull List<String> lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            builder.append(lines.get(i));
            if (i < lines.size() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }
}
