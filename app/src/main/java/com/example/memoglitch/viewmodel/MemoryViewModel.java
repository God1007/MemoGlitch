package com.example.memoglitch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;

/**
 * Exposes pseudo-memory intensity for UI indicators.
 */
public class MemoryViewModel extends AndroidViewModel {

    private final ConversationStateStore stateStore;

    public MemoryViewModel(@NonNull Application application) {
        super(application);
        ConversationEngine engine = ConversationEngine.getInstance(application);
        stateStore = engine.getStateStore();
    }

    public LiveData<Integer> getDissonanceLevel() {
        return stateStore.getDissonanceLiveData();
    }
}
