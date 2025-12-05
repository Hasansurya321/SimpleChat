package com.example.simplechatapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "simple_chat_prefs";
    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return prefs.getString(key, null);
    }

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
