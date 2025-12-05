package com.example.simplechatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private ProgressBar progressBarUsers;

    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;

    private List<User> users;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBarUsers = findViewById(R.id.progressBarUsers);

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users, user -> {
            Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(usersAdapter);

        loadUsers();
    }

    private void loadUsers() {
        progressBarUsers.setVisibility(View.VISIBLE);

        String myId = preferenceManager.getString("userId");

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    users.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String id = doc.getId();
                        if (id.equals(myId)) continue;

                        User u = new User();
                        u.id = id;
                        u.name = doc.getString("name");
                        u.username = doc.getString("username");
                        u.image = doc.getString("image");
                        users.add(u);
                    }

                    usersAdapter.notifyDataSetChanged();
                    progressBarUsers.setVisibility(View.GONE);

                    if (users.isEmpty()) {
                        Toast.makeText(this, "Belum ada user lain", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBarUsers.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal load user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
