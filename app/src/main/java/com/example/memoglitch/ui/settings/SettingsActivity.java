package com.example.memoglitch.ui.settings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.memoglitch.R;
import com.example.memoglitch.viewmodel.DialogueViewModel;
import com.example.memoglitch.viewmodel.SessionViewModel;

/**
 * Simple settings screen allowing the player to control immersion parameters.
 */
public class SettingsActivity extends AppCompatActivity {

    private SessionViewModel sessionViewModel;
    private DialogueViewModel dialogueViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingscreen);
        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        dialogueViewModel = new ViewModelProvider(this).get(DialogueViewModel.class);
        setupUi();
    }

    private void setupUi() {
        TextView header = findViewById(R.id.settingsHeaderText);
        SeekBar textSizeSeekBar = findViewById(R.id.textSizeSeekBar);
        ToggleButton vibrationToggle = findViewById(R.id.vibrationToggle);
        Button resetButton = findViewById(R.id.resetButton);

        if (header != null) {
            header.setOnClickListener(v -> finish());
        }

        sessionViewModel.getTextSize().observe(this, size -> {
            if (size == null) {
                return;
            }
            textSizeSeekBar.setProgress(Math.round(size));
        });

        sessionViewModel.getVibrationEnabled().observe(this, enabled -> {
            if (enabled != null) {
                vibrationToggle.setChecked(enabled);
            }
        });

        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sessionViewModel.updateTextSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        vibrationToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                sessionViewModel.updateVibrationEnabled(isChecked));

        resetButton.setOnClickListener(v -> {
            sessionViewModel.resetConversation(dialogueViewModel);
            finish();
        });
    }
}
