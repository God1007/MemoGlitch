package com.example.memoglitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoglitch.model.StoryManager;
import com.example.memoglitch.ui.adapter.MessageAdapter;
import com.example.memoglitch.ui.glitch.GlitchEffectFragment;
import com.example.memoglitch.ui.settings.SettingsActivity;
import com.example.memoglitch.viewmodel.DialogueViewModel;
import com.example.memoglitch.viewmodel.EmotionViewModel;
import com.example.memoglitch.viewmodel.GlitchViewModel;
import com.example.memoglitch.viewmodel.MemoryViewModel;
import com.example.memoglitch.viewmodel.SessionViewModel;
import com.example.memoglitch.viewmodel.StoryViewModel;

/**
 * Main chat screen where the player converses with Echo.
 */
public class MainActivity extends AppCompatActivity {

    private DialogueViewModel dialogueViewModel;
    private StoryViewModel storyViewModel;
    private GlitchViewModel glitchViewModel;
    private MemoryViewModel memoryViewModel;
    private SessionViewModel sessionViewModel;
    private EmotionViewModel emotionViewModel;

    private MessageAdapter messageAdapter;
    private ProgressBar dissonanceBar;
    private EditText userInput;
    private Button sendButton;
    private TextView headerSubtitle;
    private View normalCircle;
    private View glitchCircle;
    private View revealCircle;
    private TextView normalLabel;
    private TextView glitchLabel;
    private TextView revealLabel;

    private boolean vibrationEnabled = true;
    private String lastGlitchPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViewModels();
        initViews();
        observeViewModels();
    }

    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);
        dialogueViewModel = provider.get(DialogueViewModel.class);
        storyViewModel = provider.get(StoryViewModel.class);
        glitchViewModel = provider.get(GlitchViewModel.class);
        memoryViewModel = provider.get(MemoryViewModel.class);
        sessionViewModel = provider.get(SessionViewModel.class);
        emotionViewModel = provider.get(EmotionViewModel.class);
    }

    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        dissonanceBar = findViewById(R.id.dissonanceBar);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        headerSubtitle = findViewById(R.id.headerSubtitle);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        View storyIndicator = findViewById(R.id.storyIndicator);
        normalCircle = storyIndicator.findViewById(R.id.stageNormalCircle);
        glitchCircle = storyIndicator.findViewById(R.id.stageGlitchCircle);
        revealCircle = storyIndicator.findViewById(R.id.stageRevealCircle);
        normalLabel = storyIndicator.findViewById(R.id.stageNormalLabel);
        glitchLabel = storyIndicator.findViewById(R.id.stageGlitchLabel);
        revealLabel = storyIndicator.findViewById(R.id.stageRevealLabel);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            performHaptics();
            String message = userInput.getText().toString();
            dialogueViewModel.sendUserMessage(message);
            userInput.setText("");
        });

        userInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendButton.performClick();
                return true;
            }
            return false;
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        dialogueViewModel.getMessagesLiveData().observe(this, messages -> {
            messageAdapter.submitList(messages);
            recyclerView.post(() -> {
                int lastPosition = messageAdapter.getItemCount() - 1;
                if (lastPosition >= 0) {
                    recyclerView.scrollToPosition(lastPosition);
                }
            });
        });
    }

    private void observeViewModels() {
        storyViewModel.getStageLiveData().observe(this, this::renderStage);

        glitchViewModel.getGlitchState().observe(this, state -> {
            if (state == null) {
                return;
            }
            if (state.isActive() && state.getDistortedText() != null
                    && !state.getDistortedText().equals(lastGlitchPayload)) {
                lastGlitchPayload = state.getDistortedText();
                GlitchEffectFragment fragment = GlitchEffectFragment.newInstance(state.getDistortedText());
                fragment.show(getSupportFragmentManager(), "glitch");
            }
        });

        memoryViewModel.getDissonanceLevel().observe(this, level -> {
            if (level != null) {
                dissonanceBar.setProgress(level);
            }
        });

        sessionViewModel.getTextSize().observe(this, size -> {
            if (size != null) {
                messageAdapter.setTextSize(size);
            }
        });

        sessionViewModel.getVibrationEnabled().observe(this, enabled -> {
            if (enabled != null) {
                vibrationEnabled = enabled;
            }
        });

        dialogueViewModel.getTypingLiveData().observe(this, typing -> {
            if (typing != null) {
                sendButton.setEnabled(!typing);
                sendButton.setAlpha(typing ? 0.5f : 1f);
                sendButton.setText(typing ? "…" : "→");
            }
        });

        emotionViewModel.getEmotionState().observe(this, state -> {
            if (state != null) {
                headerSubtitle.setText(state.getToneLabel().toLowerCase());
            }
        });
    }

    private void renderStage(StoryManager.Stage stage) {
        if (stage == null) {
            return;
        }
        resetStageIndicators();
        switch (stage) {
            case NORMAL:
                applyStageHighlight(normalCircle, normalLabel, R.drawable.stage_circle_active, R.color.primary_blue);
                break;
            case GLITCH:
                applyStageHighlight(glitchCircle, glitchLabel, R.drawable.stage_circle_current, R.color.glitchAccent);
                break;
            case REVEAL:
                applyStageHighlight(revealCircle, revealLabel, R.drawable.stage_circle_current, R.color.gray_600);
                break;
        }
    }

    private void resetStageIndicators() {
        normalCircle.setBackgroundResource(R.drawable.stage_circle_inactive);
        glitchCircle.setBackgroundResource(R.drawable.stage_circle_inactive);
        revealCircle.setBackgroundResource(R.drawable.stage_circle_inactive);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_tertiary);
        normalLabel.setTextColor(inactiveColor);
        glitchLabel.setTextColor(inactiveColor);
        revealLabel.setTextColor(inactiveColor);
    }

    private void applyStageHighlight(View circle, TextView label, int background, int textColor) {
        circle.setBackgroundResource(background);
        label.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private void performHaptics() {
        if (vibrationEnabled) {
            sendButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }
}
