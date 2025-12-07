package com.example.simplechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.simplechatapp.R;
import com.example.simplechatapp.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_TEXT_SENT = 1;
    private static final int VIEW_TYPE_TEXT_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    private final List<ChatMessage> messages;
    private final String currentUserId;

    public ChatAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        boolean isSent = message.senderId != null && message.senderId.equals(currentUserId);
        boolean isImage = message.imageUrl != null && !message.imageUrl.isEmpty();

        if (isSent) {
            return isImage ? VIEW_TYPE_IMAGE_SENT : VIEW_TYPE_TEXT_SENT;
        } else {
            return isImage ? VIEW_TYPE_IMAGE_RECEIVED : VIEW_TYPE_TEXT_RECEIVED;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            case VIEW_TYPE_TEXT_SENT:
                layoutRes = R.layout.item_chat_right;
                break;
            case VIEW_TYPE_TEXT_RECEIVED:
                layoutRes = R.layout.item_chat_left;
                break;
            case VIEW_TYPE_IMAGE_SENT:
                layoutRes = R.layout.item_chat_image_right;
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                layoutRes = R.layout.item_chat_image_left;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder.textMessage != null) {
            holder.textMessage.setText(message.message);
        }

        if (holder.imageMessage != null) {
            Glide.with(holder.itemView.getContext())
                    .load(message.imageUrl)
                    .into(holder.imageMessage);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.textTimestamp.setText(sdf.format(new Date(message.timestamp)));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTimestamp;
        ImageView imageMessage;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            imageMessage = itemView.findViewById(R.id.imageMessage);
        }
    }
}
