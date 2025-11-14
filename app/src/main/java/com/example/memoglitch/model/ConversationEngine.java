package com.example.memoglitch.model;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Coordinates all model-layer components for the conversation experience.
 */
public class ConversationEngine {

    private static ConversationEngine instance;

    private final DataRepository repository;
    private final StoryManager storyManager;
    private final MemorySystem memorySystem;
    private final EmotionCurve emotionCurve;
    private final GlitchEffect glitchEffect;
    private final DialogueSystem dialogueSystem;
    private final ConversationStateStore stateStore;

    private ConversationEngine(@NonNull Context context) {
        repository = new DataRepository(context.getApplicationContext());
        storyManager = new StoryManager();
        memorySystem = new MemorySystem();
        emotionCurve = new EmotionCurve();
        glitchEffect = new GlitchEffect();
        dialogueSystem = new DialogueSystem(storyManager, memorySystem, emotionCurve, glitchEffect);
        stateStore = new ConversationStateStore();
    }

    public static synchronized ConversationEngine getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new ConversationEngine(context);
        }
        return instance;
    }

    @NonNull
    public DataRepository getRepository() {
        return repository;
    }

    @NonNull
    public StoryManager getStoryManager() {
        return storyManager;
    }

    @NonNull
    public MemorySystem getMemorySystem() {
        return memorySystem;
    }

    @NonNull
    public EmotionCurve getEmotionCurve() {
        return emotionCurve;
    }

    @NonNull
    public GlitchEffect getGlitchEffect() {
        return glitchEffect;
    }

    @NonNull
    public DialogueSystem getDialogueSystem() {
        return dialogueSystem;
    }

    @NonNull
    public ConversationStateStore getStateStore() {
        return stateStore;
    }
}
