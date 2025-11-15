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
    private final NarrativeScript narrativeScript;

    public DialogueSystem(@NonNull StoryManager storyManager,
                          @NonNull MemorySystem memorySystem,
                          @NonNull EmotionCurve emotionCurve,
                          @NonNull GlitchEffect glitchEffect,
                          @NonNull NarrativeScript narrativeScript) {
        this.storyManager = storyManager;
        this.memorySystem = memorySystem;
        this.emotionCurve = emotionCurve;
        this.glitchEffect = glitchEffect;
        this.narrativeScript = narrativeScript;
    }

    public DialogueResult buildResponse(@NonNull String userInput) {
        String memoryFragment = memorySystem.peekUserFragment();
        storyManager.registerUserMessage(userInput);
        StoryManager.Stage stage = storyManager.getCurrentStage();
        memorySystem.recordUserInput(userInput);

        List<String> lines = new ArrayList<>();
        lines.add(narrativeScript.compose(stage, userInput, storyManager.getUserMessageCount(), memoryFragment));

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
