package com.example.memoglitch.ui.glitch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.memoglitch.R;

import java.util.Random;

/**
 * A lightweight fragment that flashes glitch visuals when Echo destabilises.
 */
public class GlitchEffectFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "arg_message";
    private static final long AUTO_DISMISS_DELAY = 1800L;

    public static GlitchEffectFragment newInstance(String distortedText) {
        GlitchEffectFragment fragment = new GlitchEffectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, distortedText);
        fragment.setArguments(bundle);
        fragment.setStyle(STYLE_NO_TITLE, R.style.ThemeOverlay_AppCompat_Dialog);
        return fragment;
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.glitcheffectfragment, container, false);
        TextView glitchText = view.findViewById(R.id.glitchText);
        TextView distortedMessage = view.findViewById(R.id.distortedMessage);
        FrameLayout containerView = view.findViewById(R.id.glitchContainer);
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : null;
        if (message != null) {
            distortedMessage.setText(message);
        }
        startFlicker(containerView);
        startFlicker(glitchText);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    dismissAllowingStateLoss();
                }
            }
        }, AUTO_DISMISS_DELAY);
        return view;
    }

    private void startFlicker(View view) {
        AlphaAnimation animation = new AlphaAnimation(0.4f, 1f);
        animation.setDuration(120);
        animation.setRepeatMode(AlphaAnimation.REVERSE);
        animation.setRepeatCount(6 + random.nextInt(4));
        view.startAnimation(animation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
