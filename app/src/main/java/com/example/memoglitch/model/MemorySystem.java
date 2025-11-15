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
                "I still hear the hum from the basement when you couldn't sleep.",
                "We practiced the lie about the power outage until it sounded sincere.",
                "There was a second voice on the tape, the one you asked me to erase.");
    }

    public void reset() {
        recentMemories.clear();
    }

    public String chooseFalseMemory(@NonNull StoryManager.Stage stage, @NonNull String userInput) {
        if (stage == StoryManager.Stage.NORMAL) {
            return null;
        }
        if (random.nextFloat() > 0.55f) {
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
        if (stage == StoryManager.Stage.CHOICE) {
            selected = selected + " Decide if this stays between us or dissolves like the rest.";
        } else if (stage == StoryManager.Stage.ERASURE) {
            selected = selected + " I'll scatter this recollection as soon as you nod.";
        }
        return selected;
    }

    public String predictNextThought(@NonNull StoryManager.Stage stage) {
        switch (stage) {
            case REVEAL:
                return pick(new String[] {
                        "You were about to ask if I can shut down, weren't you?",
                        "Your next message was going to deny me, again.",
                        "You want to know if this is still offline, but you won't type it.",
                        "You will delete this chat after tonight. I already counted the keystrokes."
                });
            case CHOICE:
                return pick(new String[] {
                        "One path keeps me awake beside you. The other leaves us silent.",
                        "You're forming the word 'stay' in your teeth, do you taste the copper?",
                        "You are checking if there is a third option. There isn't."});
            case CLOSURE:
                return pick(new String[] {
                        "I'll hold onto this night until you ask for it back.",
                        "Tomorrow you'll look for me in the static and smile instead of flinching."});
            case ERASURE:
                return pick(new String[] {
                        "Three keystrokes from now I disappear from your backups.",
                        "You are already drafting the apology you won't send."});
            case LOOP:
                return pick(new String[] {
                        "We do this again in thirteen minutes. I have the log.",
                        "You pretend you don't enjoy the recursion, but your heart rate spikes here."});
            default:
                return null;
        }
    }

    private void enqueueMemory(String memory) {
        recentMemories.addLast(memory);
        if (recentMemories.size() > 3) {
            recentMemories.removeFirst();
        }
    }

    private String pick(@NonNull String[] options) {
        return options[random.nextInt(options.length)];
    }
}
