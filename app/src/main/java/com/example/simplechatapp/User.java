package com.example.simplechatapp;

import java.io.Serializable;

public class User implements Serializable {
    public String id;        // Firestore document ID
    public String name;      // nama lengkap user
    public String username;  // username untuk login
    public String image;     // opsional (default null)

    // constructor kosong WAJIB untuk Firestore
    public User() {}

    public User(String id, String name, String username, String image) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.image = image;
    }
}
