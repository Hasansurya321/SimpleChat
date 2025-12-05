package com.example.simplechatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class SignInActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private Button buttonSignIn, buttonGoToSignUp;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================
        // INISIALISASI PREF
        // ================
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Auto-login jika sudah login
        if (preferenceManager.getBoolean("isSignedIn")) {
            startActivity(new Intent(this, UsersActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_in);

        // ================
        // FIRESTORE
        // ================
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)     // MODE OFFLINE AKTIF
                .build();
        db.setFirestoreSettings(settings);

        // UI binding
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonGoToSignUp = findViewById(R.id.buttonGoToSignUp);

        buttonSignIn.setOnClickListener(v -> signIn());
        buttonGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void signIn() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {

                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        String name = doc.getString("name");
                        String userId = doc.getId();

                        // Simpan Session Login
                        preferenceManager.putBoolean("isSignedIn", true);
                        preferenceManager.putString("userId", userId);
                        preferenceManager.putString("userName", name != null ? name : "");

                        Toast.makeText(this, "Selamat datang, " + name, Toast.LENGTH_SHORT).show();

                        // Masuk ke MainActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
