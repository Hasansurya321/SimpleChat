package com.example.simplechatapp.model;

import java.io.Serializable;

public class User implements Serializable {
    public String id;
    public String name;
    public String username;
    public String image;

    public User() {
        // Diperlukan oleh Firestore
    }

    public User(String id, String name, String username, String image) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.image = image;
    }
}
