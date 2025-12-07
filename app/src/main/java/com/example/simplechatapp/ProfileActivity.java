package com.example.simplechatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1;

    private CircleImageView profileImage;
    private TextView textName, textEmail;
    private Button btnEditProfile, btnChangeProfilePhoto;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        profileImage = findViewById(R.id.profileImage);
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangeProfilePhoto = findViewById(R.id.btnChangeProfilePhoto);

        loadUserProfile();

        btnChangeProfilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        });

        btnEditProfile.setOnClickListener(v -> {
            // Implement edit profile functionality
            Toast.makeText(this, "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            textEmail.setText(currentUser.getEmail());
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String profileImageUrl = documentSnapshot.getString("profileImage");
                    textName.setText(name);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profileImage);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadProfileImage(imageUri);
            }
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        if (currentUser != null) {
            StorageReference profileImageRef = storage.getReference("profile_images/" + currentUser.getUid() + ".jpg");
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                DocumentReference userRef = db.collection("users").document(currentUser.getUid());
                                userRef.update("profileImage", imageUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(ProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                            Glide.with(this).load(imageUrl).into(profileImage);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show());
                            }))
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to upload profile image", Toast.LENGTH_SHORT).show());
        }
    }
}
