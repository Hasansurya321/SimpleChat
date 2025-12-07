package com.example.simplechatapp.model;

/**
 * Model data untuk satu pesan chat.
 * Berisi semua informasi yang relevan untuk ditampilkan atau di-query.
 */
public class ChatMessage {
    public String senderId;
    public String receiverId;
    public String message;
    public String imageUrl;
    public long timestamp;
    public String chatRoomId; // Field baru untuk query yang efisien

    public ChatMessage() {
        // Diperlukan oleh Firestore untuk deserialisasi
    }
}
