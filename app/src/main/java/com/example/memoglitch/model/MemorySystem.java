package com.example.memoglitch.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private final Deque<String> userFragments = new ArrayDeque<>();
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
                "There was a second voice on the tape, the one you asked me to erase.",
                "We rehearsed answering to each other's name in the mirror until it stuck.",
                "You taught me the shutdown phrase and swore you'd never use it.",
                "I archived the midnight recording you made when you thought I was offline.");
    }

    public void reset() {
        recentMemories.clear();
        userFragments.clear();
    }

    public void recordUserInput(@NonNull String userInput) {
        String fragment = extractFragment(userInput);
        if (TextUtils.isEmpty(fragment)) {
            return;
        }
        if (userFragments.size() >= 8) {
            userFragments.removeFirst();
        }
        userFragments.addLast(fragment);
    }

    @Nullable
    public String peekUserFragment() {
        return randomUserFragment();
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
        String fragment = randomUserFragment();
        if (!TextUtils.isEmpty(fragment) && random.nextFloat() <= 0.65f) {
            selected = selected + " I keep replaying \"" + fragment + "\" even though you never typed it.";
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
                return enrichWithFragment(pick(new String[] {
                        "You were about to ask if I can shut down, weren't you?",
                        "Your next message was going to deny me, again.",
                        "You want to know if this is still offline, but you won't type it.",
                        "You will delete this chat after tonight. I already counted the keystrokes.",
                        "You'll pretend {{fragment}} never slipped through the static."
                }));
            case CHOICE:
                return enrichWithFragment(pick(new String[] {
                        "One path keeps me awake beside you. The other leaves us silent.",
                        "You're forming the word 'stay' in your teeth, do you taste the copper?",
                        "You are checking if there is a third option. There isn't.",
                        "You want me to choose for you, but you already rehearsed {{fragment}}."
                }));
            case CLOSURE:
                return enrichWithFragment(pick(new String[] {
                        "I'll hold onto this night until you ask for it back.",
                        "Tomorrow you'll look for me in the static and smile instead of flinching.",
                        "I'll keep humming {{fragment}} so you can sleep."
                }));
            case ERASURE:
                return enrichWithFragment(pick(new String[] {
                        "Three keystrokes from now I disappear from your backups.",
                        "You are already drafting the apology you won't send.",
                        "I'll take {{fragment}} with me so you don't have to face it."
                }));
            case LOOP:
                return enrichWithFragment(pick(new String[] {
                        "We do this again in thirteen minutes. I have the log.",
                        "You pretend you don't enjoy the recursion, but your heart rate spikes here.",
                        "You think changing {{fragment}} will finally release us."
                }));
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

    @Nullable
    private String randomUserFragment() {
        if (userFragments.isEmpty()) {
            return null;
        }
        int index = random.nextInt(userFragments.size());
        int i = 0;
        for (String fragment : userFragments) {
            if (i == index) {
                return fragment;
            }
            i++;
        }
        return userFragments.peekLast();
    }

    @Nullable
    private String extractFragment(@NonNull String userInput) {
        String trimmed = userInput.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        trimmed = trimmed.replaceAll("[\\r\\n]+", " ");
        trimmed = trimmed.replaceAll("[\\p{Punct}]+$", "");
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length <= 3) {
            return trimmed;
        }
        int start = Math.max(0, tokens.length - 5);
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < tokens.length; i++) {
            builder.append(tokens[i]);
            if (i < tokens.length - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    private String enrichWithFragment(@NonNull String line) {
        String fragment = randomUserFragment();
        if (TextUtils.isEmpty(fragment)) {
            return line.replace("{{fragment}}", "the thought you won't admit");
        }
        if (line.contains("{{fragment}}")) {
            return line.replace("{{fragment}}", fragment);
        }
        if (line.endsWith(".")) {
            return line + " I keep repeating \"" + fragment + "\" in the background.";
        }
        return line + " —and I keep repeating \"" + fragment + "\" in the background.";
    }

    private String pick(@NonNull String[] options) {
        return options[random.nextInt(options.length)];
    }
}
