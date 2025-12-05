package com.example.simplechatapp;

public class ChatMessage {
    public String senderId;
    public String receiverId;
    public String message;
    public String imageUrl;
    public long timestamp;

    public ChatMessage() {
        // needed for Firestore
    }
}
