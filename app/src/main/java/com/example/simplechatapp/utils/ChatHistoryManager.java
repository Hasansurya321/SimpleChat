package com.example.simplechatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.simplechatapp.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas utilitas untuk mengelola penyimpanan dan pemuatan riwayat obrolan
 * ke SharedPreferences. Menggunakan Gson untuk serialisasi/deserialisasi.
 */
public class ChatHistoryManager {

    private static final String PREFS_NAME = "ChatHistoryPrefs";
    private static final String KEY_PREFIX = "chat_history_";

    public static void saveMessages(Context context, String receiverId, List<ChatMessage> messages) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonMessages = gson.toJson(messages);
        editor.putString(KEY_PREFIX + receiverId, jsonMessages);
        editor.apply();
    }

    public static List<ChatMessage> loadMessages(Context context, String receiverId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonMessages = sharedPreferences.getString(KEY_PREFIX + receiverId, null);

        if (jsonMessages == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<ChatMessage>>() {}.getType();
        return gson.fromJson(jsonMessages, type);
    }
}
