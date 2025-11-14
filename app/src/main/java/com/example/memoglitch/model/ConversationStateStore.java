package com.example.memoglitch.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared store that exposes LiveData objects consumed by multiple ViewModels.
 */
public class ConversationStateStore {

    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<StoryManager.Stage> stageLiveData = new MutableLiveData<>(StoryManager.Stage.NORMAL);
    private final MutableLiveData<GlitchEffect.GlitchState> glitchLiveData = new MutableLiveData<>(new GlitchEffect.GlitchState(false, ""));
    private final MutableLiveData<EmotionCurve.EmotionState> emotionLiveData = new MutableLiveData<>(new EmotionCurve().stateForStage(StoryManager.Stage.NORMAL));
    private final MutableLiveData<Boolean> typingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Float> textSizeLiveData = new MutableLiveData<>(14f);
    private final MutableLiveData<Boolean> vibrationEnabledLiveData = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> dissonanceLiveData = new MutableLiveData<>(0);

    public LiveData<List<Message>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<StoryManager.Stage> getStageLiveData() {
        return stageLiveData;
    }

    public LiveData<GlitchEffect.GlitchState> getGlitchLiveData() {
        return glitchLiveData;
    }

    public LiveData<EmotionCurve.EmotionState> getEmotionLiveData() {
        return emotionLiveData;
    }

    public LiveData<Boolean> getTypingLiveData() {
        return typingLiveData;
    }

    public LiveData<Float> getTextSizeLiveData() {
        return textSizeLiveData;
    }

    public LiveData<Boolean> getVibrationEnabledLiveData() {
        return vibrationEnabledLiveData;
    }

    public LiveData<Integer> getDissonanceLiveData() {
        return dissonanceLiveData;
    }

    public void setMessages(@NonNull List<Message> messages) {
        messagesLiveData.setValue(messages);
    }

    public void setStage(@NonNull StoryManager.Stage stage) {
        stageLiveData.setValue(stage);
    }

    public void setGlitch(@NonNull GlitchEffect.GlitchState glitchState) {
        glitchLiveData.setValue(glitchState);
    }

    public void setEmotion(@NonNull EmotionCurve.EmotionState emotionState) {
        emotionLiveData.setValue(emotionState);
    }

    public void setTyping(boolean typing) {
        typingLiveData.setValue(typing);
    }

    public void setTextSize(float textSize) {
        textSizeLiveData.setValue(textSize);
    }

    public void setVibrationEnabled(boolean enabled) {
        vibrationEnabledLiveData.setValue(enabled);
    }

    public void setDissonance(int dissonance) {
        dissonanceLiveData.setValue(dissonance);
    }
}
