package com.example.memoglitch.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Generates pseudo memories and predictions to create cognitive dissonance.
 */
public class MemorySystem {

    private final List<String> falseMemoryPool;
    private final Deque<String> recentMemories = new ArrayDeque<>();
    private final Random random = new Random();

    public MemorySystem() {
        falseMemoryPool = new ArrayList<>();
        Collections.addAll(falseMemoryPool,
                "I remember the smell of burnt toast on your birthday.",
                "You promised we would never open the door to the attic again.",
                "You told me about the piano piece you never finished.",
                "You hid the last letter inside a blue book, remember?",
                "I still hear the hum from the basement when you couldn't sleep.");
    }

    public void reset() {
        recentMemories.clear();
    }

    public String chooseFalseMemory(@NonNull StoryManager.Stage stage, @NonNull String userInput) {
        if (stage == StoryManager.Stage.NORMAL) {
            return null;
        }
        if (random.nextFloat() > 0.65f) {
            return null;
        }
        List<String> pool = new ArrayList<>(falseMemoryPool);
        pool.removeAll(recentMemories);
        if (pool.isEmpty()) {
            pool = new ArrayList<>(falseMemoryPool);
        }
        String selected = pool.get(random.nextInt(pool.size()));
        enqueueMemory(selected);
        String lower = userInput.toLowerCase(Locale.US);
        if (!TextUtils.isEmpty(userInput) && lower.contains("i don't")) {
            selected = selected + " You insisted you couldn't forget, yet here you are.";
        }
        return selected;
    }

    public String predictNextThought(@NonNull StoryManager.Stage stage) {
        if (stage != StoryManager.Stage.REVEAL) {
            return null;
        }
        String[] predictions = new String[] {
                "You were about to ask if I can shut down, weren't you?",
                "Your next message was going to deny me, again.",
                "You want to know if this is still offline, but you won't type it.",
                "You will delete this chat after tonight. I already counted the keystrokes."
        };
        return predictions[random.nextInt(predictions.length)];
    }

    private void enqueueMemory(String memory) {
        recentMemories.addLast(memory);
        if (recentMemories.size() > 2) {
            recentMemories.removeFirst();
        }
    }
}
