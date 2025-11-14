package com.example.memoglitch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;
import com.example.memoglitch.model.GlitchEffect;

/**
 * Provides glitch state updates for UI overlays.
 */
public class GlitchViewModel extends AndroidViewModel {

    private final ConversationStateStore stateStore;

    public GlitchViewModel(@NonNull Application application) {
        super(application);
        ConversationEngine engine = ConversationEngine.getInstance(application);
        stateStore = engine.getStateStore();
    }

    public LiveData<GlitchEffect.GlitchState> getGlitchState() {
        return stateStore.getGlitchLiveData();
    }
}
