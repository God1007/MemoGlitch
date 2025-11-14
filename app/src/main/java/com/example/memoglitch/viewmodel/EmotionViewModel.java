package com.example.memoglitch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;
import com.example.memoglitch.model.EmotionCurve;

/**
 * Shares the current emotional tone / typing delay values.
 */
public class EmotionViewModel extends AndroidViewModel {

    private final ConversationStateStore stateStore;

    public EmotionViewModel(@NonNull Application application) {
        super(application);
        ConversationEngine engine = ConversationEngine.getInstance(application);
        stateStore = engine.getStateStore();
    }

    public LiveData<EmotionCurve.EmotionState> getEmotionState() {
        return stateStore.getEmotionLiveData();
    }
}
