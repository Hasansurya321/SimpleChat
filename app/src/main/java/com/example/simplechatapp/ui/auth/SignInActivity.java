package com.example.simplechatapp.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

// Import MainActivity dari paket yang benar
import com.example.simplechatapp.MainActivity;
import com.example.simplechatapp.utils.PreferenceManager;
import com.example.simplechatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button buttonSignIn, buttonGoToSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        // Auto-login jika user sudah ada
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_in);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonGoToSignUp = findViewById(R.id.buttonGoToSignUp);
        progressBar = findViewById(R.id.progressBar);

        buttonSignIn.setOnClickListener(v -> signIn());
        buttonGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void signIn() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Ambil data user dari Firestore untuk disimpan di SharedPreferences
                        FirebaseFirestore.getInstance().collection("users").document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    String name = documentSnapshot.getString("name");
                                    preferenceManager.putBoolean("isSignedIn", true);
                                    preferenceManager.putString("userId", mAuth.getCurrentUser().getUid());
                                    preferenceManager.putString("userName", name != null ? name : "");

                                    Toast.makeText(this, "Selamat datang, " + name, Toast.LENGTH_SHORT).show();

                                    // Arahkan ke MainActivity
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Jika gagal ambil data dari Firestore, tetap lanjutkan
                                    Toast.makeText(this, "Login berhasil, gagal sinkronisasi data profil", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Login gagal: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
