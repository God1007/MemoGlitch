package com.example.memoglitch.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memoglitch.model.ConversationEngine;
import com.example.memoglitch.model.ConversationStateStore;
import com.example.memoglitch.model.DataRepository;
import com.example.memoglitch.model.DialogueSystem;
import com.example.memoglitch.model.EmotionCurve;
import com.example.memoglitch.model.GlitchEffect;
import com.example.memoglitch.model.Message;
import com.example.memoglitch.model.StoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Primary ViewModel that sends and receives dialogue messages.
 */
public class DialogueViewModel extends AndroidViewModel {

    private final ConversationEngine engine;
    private final ConversationStateStore stateStore;
    private final DataRepository repository;
    private final DialogueSystem dialogueSystem;
    private final StoryManager storyManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final List<Message> messages = new ArrayList<>();

    public DialogueViewModel(@NonNull Application application) {
        super(application);
        engine = ConversationEngine.getInstance(application);
        stateStore = engine.getStateStore();
        repository = engine.getRepository();
        dialogueSystem = engine.getDialogueSystem();
        storyManager = engine.getStoryManager();
        restoreSession();
    }

    private void restoreSession() {
        messages.clear();
        messages.addAll(repository.loadMessages());
        storyManager.setUserMessageCount(countUserMessages(messages));
        StoryManager.Stage savedStage = repository.loadStage();
        storyManager.setStage(savedStage);
        storyManager.setFirstFalseMemoryShared(repository.wasFalseMemoryShared());
        stateStore.setMessages(new ArrayList<>(messages));
        stateStore.setStage(savedStage);
        stateStore.setEmotion(engine.getEmotionCurve().stateForStage(savedStage));
        updateDissonance();
    }

    private int countUserMessages(List<Message> messageList) {
        int count = 0;
        for (Message message : messageList) {
            if (message.getSender() == Message.Sender.USER) {
                count++;
            }
        }
        return count;
    }

    public LiveData<List<Message>> getMessagesLiveData() {
        return stateStore.getMessagesLiveData();
    }

    public LiveData<StoryManager.Stage> getStageLiveData() {
        return stateStore.getStageLiveData();
    }

    public LiveData<GlitchEffect.GlitchState> getGlitchLiveData() {
        return stateStore.getGlitchLiveData();
    }

    public LiveData<EmotionCurve.EmotionState> getEmotionLiveData() {
        return stateStore.getEmotionLiveData();
    }

    public LiveData<Boolean> getTypingLiveData() {
        return stateStore.getTypingLiveData();
    }

    public void sendUserMessage(@NonNull final String text) {
        if (text.trim().isEmpty()) {
            return;
        }
        Message message = new Message(Message.Sender.USER, text.trim(), false,
                System.currentTimeMillis(), storyManager.getCurrentStage());
        messages.add(message);
        stateStore.setMessages(new ArrayList<>(messages));
        repository.saveMessages(messages);
        scheduleAiResponse(text);
    }

    private void scheduleAiResponse(@NonNull final String userInput) {
        stateStore.setTyping(true);
        final DialogueSystem.DialogueResult result = dialogueSystem.buildResponse(userInput);
        long delay = result.getEmotionState().getTypingDelayMillis();
        stateStore.setEmotion(result.getEmotionState());
        stateStore.setGlitch(result.getGlitchState());
        if (result.getFalseMemory() != null) {
            repository.saveFalseMemoryShared(true);
        }
        repository.saveStage(storyManager.getCurrentStage());
        stateStore.setStage(storyManager.getCurrentStage());
        updateDissonance();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messages.add(result.getMessage());
                stateStore.setMessages(new ArrayList<>(messages));
                repository.saveMessages(messages);
                stateStore.setTyping(false);
            }
        }, delay);
    }

    public void resetSession() {
        repository.clearSession();
        engine.getStoryManager().reset();
        engine.getMemorySystem().reset();
        messages.clear();
        stateStore.setMessages(new ArrayList<>(messages));
        stateStore.setStage(StoryManager.Stage.NORMAL);
        stateStore.setGlitch(new GlitchEffect.GlitchState(false, ""));
        stateStore.setEmotion(engine.getEmotionCurve().stateForStage(StoryManager.Stage.NORMAL));
        updateDissonance();
        repository.saveStage(StoryManager.Stage.NORMAL);
        repository.saveMessages(messages);
        repository.saveFalseMemoryShared(false);
    }

    private void updateDissonance() {
        int level = Math.min(100, storyManager.getUserMessageCount() * 15
                + storyManager.getCurrentStage().ordinal() * 25);
        stateStore.setDissonance(level);
    }

    @NonNull
    public String buildTranscript() {
        if (messages.isEmpty()) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            builder.append(senderLabel(message.getSender()))
                    .append(" [")
                    .append(format.format(new Date(message.getTimestamp())))
                    .append("] ")
                    .append(stageLabel(message.getStageAtSend()))
                    .append(':')
                    .append('\n')
                    .append(message.getText().trim());
            if (i < messages.size() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    private String senderLabel(@NonNull Message.Sender sender) {
        return sender == Message.Sender.USER ? "You" : "Echo";
    }

    private String stageLabel(@NonNull StoryManager.Stage stage) {
        String lower = stage.name().toLowerCase(Locale.US);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
