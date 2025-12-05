package com.example.simplechatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputName, inputUsername, inputPassword;
    private Button buttonSignUp, buttonBackToLogin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();

        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        buttonSignUp.setOnClickListener(v -> registerUser());
        buttonBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = inputName.getText().toString().trim();
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Data user disimpan ke Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("username", username);
        user.put("password", password); // NOTE: masih plain text (untuk demo)
        user.put("image", "");
        user.put("token", "");

        db.collection("users")
                .add(user)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this,
                            "Registrasi berhasil, silakan login",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Gagal registrasi: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
