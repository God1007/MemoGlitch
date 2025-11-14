package com.example.memoglitch.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoglitch.R;
import com.example.memoglitch.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter responsible for rendering user and AI messages within the RecyclerView.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private float textSizeSp = 14f;

    public void submitList(@NonNull List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void setTextSize(float textSizeSp) {
        this.textSizeSp = textSizeSp;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messageadapter, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position), textSizeSp, timeFormat);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout userMessageFrame;
        private final TextView userMessageText;
        private final FrameLayout aiMessageFrame;
        private final TextView aiMessageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageFrame = itemView.findViewById(R.id.userMessageFrame);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            aiMessageFrame = itemView.findViewById(R.id.aiMessageFrame);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);
        }

        void bind(@NonNull Message message, float textSizeSp, SimpleDateFormat timeFormat) {
            String decorated = decorateWithTimestamp(message.getText(), message.getTimestamp(), timeFormat);
            if (message.getSender() == Message.Sender.USER) {
                userMessageFrame.setVisibility(View.VISIBLE);
                aiMessageFrame.setVisibility(View.GONE);
                userMessageText.setText(decorated);
                userMessageText.setTextSize(textSizeSp);
            } else {
                aiMessageFrame.setVisibility(View.VISIBLE);
                userMessageFrame.setVisibility(View.GONE);
                aiMessageText.setText(decorated);
                aiMessageText.setTextSize(textSizeSp);
                int color = message.isGlitch()
                        ? ContextCompat.getColor(itemView.getContext(), R.color.glitchAccent)
                        : ContextCompat.getColor(itemView.getContext(), R.color.deepText);
                aiMessageText.setTextColor(color);
            }
        }

        private String decorateWithTimestamp(@NonNull String text, long timestamp, SimpleDateFormat format) {
            String time = format.format(new Date(timestamp));
            return text + "\n" + time;
        }
    }
}
