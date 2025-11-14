package com.example.memoglitch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;
import com.example.memoglitch.model.StoryManager;

/**
 * Exposes the current story phase to the UI.
 */
public class StoryViewModel extends AndroidViewModel {

    private final ConversationStateStore stateStore;

    public StoryViewModel(@NonNull Application application) {
        super(application);
        ConversationEngine engine = ConversationEngine.getInstance(application);
        stateStore = engine.getStateStore();
    }

    public LiveData<StoryManager.Stage> getStageLiveData() {
        return stateStore.getStageLiveData();
    }
}
