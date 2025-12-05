package com.example.simplechatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<ChatMessage> messages;
    private final String currentUserId;

    public ChatAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.senderId != null && message.senderId.equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (viewType == TYPE_SENT) {
            // pakai nama layout yang ADA di folder res/layout
            layout = R.layout.item_message_sent;
        } else {
            layout = R.layout.item_message_received;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Teks
        if (message.message != null && !message.message.isEmpty()) {
            holder.textMessage.setVisibility(View.VISIBLE);
            holder.textMessage.setText(message.message);
        } else {
            holder.textMessage.setVisibility(View.GONE);
        }

        // Gambar (kalau ada imageUrl)
        if (message.imageUrl != null && !message.imageUrl.isEmpty()) {
            holder.imageMessage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.imageUrl)
                    .into(holder.imageMessage);
        } else {
            holder.imageMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        ImageView imageMessage;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            imageMessage = itemView.findViewById(R.id.imageMessage);
        }
    }
}
