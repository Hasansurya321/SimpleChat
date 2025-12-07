package com.example.simplechatapp.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplechatapp.R;
import com.example.simplechatapp.adapter.UsersAdapter;
import com.example.simplechatapp.model.User;
import com.example.simplechatapp.ui.auth.SignInActivity;
import com.example.simplechatapp.ui.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private ImageView btnBack;
    private Button btnLogout;
    private ProgressBar progressBarUsers;

    private FirebaseFirestore db;

    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        progressBarUsers = findViewById(R.id.progressBarUsers);

        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        users = new ArrayList<>();
        adapter = new UsersAdapter(users, user -> {
            Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        progressBarUsers.setVisibility(View.VISIBLE);
        String myId = FirebaseAuth.getInstance().getUid();

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressBarUsers.setVisibility(View.GONE);
                    users.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String id = doc.getId();
                        if (id.equals(myId)) continue;

                        User u = doc.toObject(User.class);
                        u.id = id;
                        users.add(u);
                    }

                    adapter.notifyDataSetChanged();

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
