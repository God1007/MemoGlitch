package com.example.memoglitch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;
import com.example.memoglitch.model.DataRepository;

/**
 * Handles session persistence values exposed in the settings screen.
 */
public class SessionViewModel extends AndroidViewModel {

    private final DataRepository repository;
    private final ConversationStateStore stateStore;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        ConversationEngine engine = ConversationEngine.getInstance(application);
        repository = engine.getRepository();
        stateStore = engine.getStateStore();
        stateStore.setTextSize(repository.loadTextSize());
        stateStore.setVibrationEnabled(repository.isVibrationEnabled());
    }

    public LiveData<Float> getTextSize() {
        return stateStore.getTextSizeLiveData();
    }

    public LiveData<Boolean> getVibrationEnabled() {
        return stateStore.getVibrationEnabledLiveData();
    }

    public void updateTextSize(float textSize) {
        repository.saveTextSize(textSize);
        stateStore.setTextSize(textSize);
    }

    public void updateVibrationEnabled(boolean enabled) {
        repository.saveVibrationEnabled(enabled);
        stateStore.setVibrationEnabled(enabled);
    }

    public void resetConversation(DialogueViewModel dialogueViewModel) {
        repository.clearSession();
        dialogueViewModel.resetSession();
    }
}
