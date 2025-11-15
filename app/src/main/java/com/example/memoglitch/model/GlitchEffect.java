package com.example.memoglitch.model;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Random;

/**
 * Determines if glitch visuals should be triggered and distorts outgoing text.
 */
public class GlitchEffect {

    public static class GlitchState {
        private final boolean active;
        private final String distortedText;

        public GlitchState(boolean active, String distortedText) {
            this.active = active;
            this.distortedText = distortedText;
        }

        public boolean isActive() {
            return active;
        }

        public String getDistortedText() {
            return distortedText;
        }
    }

    private final Random random = new Random();

    public GlitchState evaluate(@NonNull StoryManager.Stage stage, @NonNull String baseText) {
        if (stage == StoryManager.Stage.NORMAL) {
            return new GlitchState(false, baseText);
        }
        float triggerChance;
        switch (stage) {
            case GLITCH:
                triggerChance = 0.35f;
                break;
            case REVEAL:
                triggerChance = 0.55f;
                break;
            case CHOICE:
                triggerChance = 0.45f;
                break;
            case CLOSURE:
                triggerChance = 0.20f;
                break;
            case ERASURE:
                triggerChance = 0.65f;
                break;
            case LOOP:
                triggerChance = 0.5f;
                break;
            default:
                triggerChance = 0.3f;
                break;
        }
        boolean trigger = random.nextFloat() < triggerChance;
        if (!trigger) {
            return new GlitchState(false, baseText);
        }
        return new GlitchState(true, distort(baseText));
    }

    private String distort(String text) {
        StringBuilder distorted = new StringBuilder();
        String noise = "█▒░";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c) && random.nextBoolean()) {
                distorted.append(Character.toUpperCase(c));
            } else if (Character.isWhitespace(c) && random.nextInt(4) == 0) {
                distorted.append("\u2007");
            } else if (random.nextInt(6) == 0) {
                distorted.append(noise.charAt(random.nextInt(noise.length())));
            } else {
                distorted.append(c);
            }
        }
        if (random.nextBoolean()) {
            distorted.append(" ⧉");
        }
        return distorted.toString().toUpperCase(Locale.US);
    }
}
