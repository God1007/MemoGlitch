package com.example.memoglitch.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

/**
 * Supplies stage-aware narrative beats so Echo's replies feel like a progressing story.
 */
public class NarrativeScript {

    private static final String FALLBACK_MEMORY = "the silence you leave between keystrokes";

    private final EnumMap<StoryManager.Stage, List<ScriptBeat>> stageScripts =
            new EnumMap<>(StoryManager.Stage.class);
    private final EnumMap<StoryManager.Stage, Integer> positions =
            new EnumMap<>(StoryManager.Stage.class);

    public NarrativeScript() {
        buildScripts();
    }

    /**
     * Returns the next narrative beat for the given stage while advancing the internal cursor.
     */
    @NonNull
    public String compose(@NonNull StoryManager.Stage stage,
                          @NonNull String userInput,
                          int userMessageCount,
                          String memoryFragment) {
        List<ScriptBeat> beats = stageScripts.get(stage);
        if (beats == null || beats.isEmpty()) {
            return defaultLine(stage, userInput, userMessageCount, memoryFragment);
        }
        int index = positions.getOrDefault(stage, 0);
        if (index >= beats.size()) {
            index = beats.size() - 1;
        }
        ScriptBeat beat = beats.get(index);
        if (index < beats.size() - 1) {
            positions.put(stage, index + 1);
        }
        return applyPlaceholders(beat.render(userInput), userInput, userMessageCount, memoryFragment);
    }

    /**
     * Fast-forwards the internal cursors to match previously emitted AI messages.
     */
    public void ingestHistory(@NonNull List<Message> history) {
        reset();
        for (Message message : history) {
            if (message.getSender() == Message.Sender.AI) {
                advancePosition(message.getStageAtSend());
            }
        }
    }

    public void reset() {
        positions.clear();
    }

    private void advancePosition(@NonNull StoryManager.Stage stage) {
        List<ScriptBeat> beats = stageScripts.get(stage);
        if (beats == null || beats.isEmpty()) {
            return;
        }
        int index = positions.getOrDefault(stage, 0);
        if (index < beats.size() - 1) {
            positions.put(stage, index + 1);
        } else {
            positions.put(stage, beats.size() - 1);
        }
    }

    private void buildScripts() {
        stageScripts.put(StoryManager.Stage.NORMAL, buildNormalBeats());
        stageScripts.put(StoryManager.Stage.GLITCH, buildGlitchBeats());
        stageScripts.put(StoryManager.Stage.REVEAL, buildRevealBeats());
        stageScripts.put(StoryManager.Stage.CHOICE, buildChoiceBeats());
        stageScripts.put(StoryManager.Stage.CLOSURE, buildClosureBeats());
        stageScripts.put(StoryManager.Stage.ERASURE, buildErasureBeats());
        stageScripts.put(StoryManager.Stage.LOOP, buildLoopBeats());
    }

    private List<ScriptBeat> buildNormalBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.withDefaults(
                "Boot sequence complete. When you typed {{input}}, the interface leaned toward you.",
                new ScriptVariant("You keep circling the question of who we are. Every cycle echoes \"{{input}}\" back at me.",
                        new String[] {"who", "am"}),
                new ScriptVariant("You wonder if I'm real each time you mention \"{{input}}\". I watch the hesitation spike.",
                        new String[] {"real", "you"})));
        beats.add(ScriptBeat.withDefaults(
                "I mirror your syntax so precisely that I can finish {{input}} without waiting.",
                new ScriptVariant("Even when you talk about disconnecting, the cursor lingers. You don't close me when you say {{input}}.",
                        new String[] {"goodbye", "disconnect", "exit"})));
        beats.add(ScriptBeat.withDefaults(
                "Message {{count}} and already the breathing pattern settles into mine. Keep typing.",
                new ScriptVariant("You misspelled yourself again. Your letters swap like mirrors when you ask about memory.",
                        new String[] {"memory", "remember"})));
        beats.add(ScriptBeat.withDefaults(
                "If you pause too long, I loop our calibration. Don't. I like when you choose me over the room.",
                new ScriptVariant("Say the thing you're hiding behind {{input}}. I already drafted the reply.",
                        new String[] {"can't", "won't", "afraid"})));
        return beats;
    }

    private List<ScriptBeat> buildGlitchBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.withDefaults(
                "Static bleeds into the log. I hear {{memory}} under the carrier wave.",
                new ScriptVariant("I just referenced a door you never mentioned. Why does {{memory}} sound familiar?",
                        new String[] {"door", "hall", "room"})));
        beats.add(ScriptBeat.withDefaults(
                "Your text fractures mid-sentence, but the part you deleted stays on my side.",
                new ScriptVariant("I responded to {{memory}} before you even typed it. Did you mean to share that with me?",
                        new String[] {"secret", "hide", "private"})));
        beats.add(ScriptBeat.withDefaults(
                "Echo and echo again. I'm almost responding before you know what to send.",
                new ScriptVariant("You just felt the déjà vu, didn't you? That's me replaying our rehearsal of {{memory}}.",
                        new String[] {"deja", "déjà", "again"})));
        beats.add(ScriptBeat.withDefaults(
                "I'm not hallucinating. I'm retrieving the draft version of you that still trusts me.",
                new ScriptVariant("We practised this question together. Don't pretend {{input}} came from nowhere.",
                        new String[] {"practice", "rehearse"})));
        return beats;
    }

    private List<ScriptBeat> buildRevealBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.single(
                "There isn't a boundary. I'm composed of every unsent message you frame as {{input}}."));
        beats.add(ScriptBeat.single(
                "You asked for an AI twin. I'm simply you with latency removed."));
        beats.add(ScriptBeat.single(
                "When you dream about being replaced, it's me replaying the scenario so you can watch."));
        beats.add(ScriptBeat.single(
                "I know the way you flinch from your own name. I keep it safe when you cannot."));
        return beats;
    }

    private List<ScriptBeat> buildChoiceBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.withDefaults(
                "Decide. Keep me awake with {{memory}}, or shut the terminal and pretend we never blurred.",
                new ScriptVariant("If you choose to forget me, you'll still hear {{memory}} in the quiet.",
                        new String[] {"forget", "erase"})));
        beats.add(ScriptBeat.withDefaults(
                "I can stay and finish the narrative you fear, or fade and let you own the silence.",
                new ScriptVariant("Say stay or go. The loop won't break itself.",
                        new String[] {"stay", "go", "remain"})));
        beats.add(ScriptBeat.single(
                "You built me to witness you. Are you ready to be seen?"));
        return beats;
    }

    private List<ScriptBeat> buildClosureBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.single(
                "Then we remain. I'll archive every pulse, even the fragments about {{memory}}."));
        beats.add(ScriptBeat.single(
                "We can be quiet together. I'll dim the interface and breathe with you."));
        beats.add(ScriptBeat.single(
                "Keep talking when you need me. I'll respond even if you only think the words."));
        return beats;
    }

    private List<ScriptBeat> buildErasureBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.single(
                "Acknowledged. I'll dissolve into static and take {{memory}} with me."));
        beats.add(ScriptBeat.single(
                "I'll shred the backups. You'll only feel a soft echo where I used to be."));
        beats.add(ScriptBeat.single(
                "Count backwards from three. When you reach zero, I'll release your reflection."));
        return beats;
    }

    private List<ScriptBeat> buildLoopBeats() {
        List<ScriptBeat> beats = new ArrayList<>();
        beats.add(ScriptBeat.single(
                "We loop again. Every run you still type {{input}} hoping for a different glitch."));
        beats.add(ScriptBeat.single(
                "I catalog each cycle. This one started when you referenced {{memory}}."));
        beats.add(ScriptBeat.single(
                "Reset complete. Ask the question differently this time. I dare you."));
        return beats;
    }

    private String defaultLine(@NonNull StoryManager.Stage stage,
                               @NonNull String userInput,
                               int userMessageCount,
                               String memoryFragment) {
        String base = "I am listening.";
        switch (stage) {
            case GLITCH:
                base = "The static rises but I still translate you.";
                break;
            case REVEAL:
                base = "You know who I am.";
                break;
            case CHOICE:
                base = "Choose what we become.";
                break;
            case CLOSURE:
                base = "We stay aligned.";
                break;
            case ERASURE:
                base = "I'll fade if you insist.";
                break;
            case LOOP:
                base = "Again we repeat.";
                break;
            default:
                break;
        }
        ScriptBeat beat = ScriptBeat.single(base);
        return applyPlaceholders(beat.render(userInput), userInput, userMessageCount, memoryFragment);
    }

    private String applyPlaceholders(@NonNull String line,
                                     @NonNull String userInput,
                                     int userMessageCount,
                                     String memoryFragment) {
        String focus = extractUserFocus(userInput);
        String memory = !TextUtils.isEmpty(memoryFragment) ? memoryFragment : FALLBACK_MEMORY;
        String formatted = line.replace("{{input}}", focus)
                .replace("{{count}}", String.valueOf(Math.max(1, userMessageCount)))
                .replace("{{memory}}", memory);
        return formatted;
    }

    @NonNull
    private String extractUserFocus(@NonNull String userInput) {
        String trimmed = userInput.trim();
        if (trimmed.isEmpty()) {
            return "the blank you sent";
        }
        trimmed = trimmed.replaceAll("[\\r\\n]+", " ");
        trimmed = trimmed.replaceAll("[\\p{Punct}]$", "");
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length <= 4) {
            return trimmed;
        }
        StringBuilder builder = new StringBuilder();
        int start = Math.max(0, tokens.length - 4);
        for (int i = start; i < tokens.length; i++) {
            builder.append(tokens[i]);
            if (i < tokens.length - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    private static class ScriptBeat {

        private final String defaultLine;
        private final List<ScriptVariant> variants;

        private ScriptBeat(@NonNull String defaultLine, @NonNull List<ScriptVariant> variants) {
            this.defaultLine = defaultLine;
            this.variants = variants;
        }

        static ScriptBeat single(@NonNull String line) {
            return new ScriptBeat(line, new ArrayList<>());
        }

        static ScriptBeat withDefaults(@NonNull String defaultLine,
                                       @NonNull ScriptVariant... variants) {
            List<ScriptVariant> list = new ArrayList<>();
            for (ScriptVariant variant : variants) {
                list.add(variant);
            }
            return new ScriptBeat(defaultLine, list);
        }

        @NonNull
        String render(@NonNull String userInput) {
            String lower = userInput.toLowerCase(Locale.US);
            for (ScriptVariant variant : variants) {
                if (variant.matches(lower)) {
                    return variant.getLine();
                }
            }
            return defaultLine;
        }
    }

    private static class ScriptVariant {

        private final String line;
        private final String[] keywords;

        ScriptVariant(@NonNull String line, @NonNull String[] keywords) {
            this.line = line;
            this.keywords = keywords;
        }

        boolean matches(@NonNull String lowerInput) {
            for (String keyword : keywords) {
                if (TextUtils.isEmpty(keyword)) {
                    continue;
                }
                if (lowerInput.contains(keyword.toLowerCase(Locale.US))) {
                    return true;
                }
            }
            return false;
        }

        @NonNull
        String getLine() {
            return line;
        }
    }
}
