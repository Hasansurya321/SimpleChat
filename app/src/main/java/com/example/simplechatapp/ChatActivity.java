package com.example.simplechatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1001;

    private User receiverUser;
    private FirebaseFirestore db;

    private RecyclerView recyclerViewChat;
    private EditText inputMessage;
    private Button buttonSend, buttonImage;
    private TextView textUserName;
    private ProgressBar progressBar;

    private List<ChatMessage> messages;
    private ChatAdapter chatAdapter;

    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverUser = (User) getIntent().getSerializableExtra("user");
        if (receiverUser == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myId = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        inputMessage = findViewById(R.id.inputMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonImage = findViewById(R.id.buttonImage);
        textUserName = findViewById(R.id.textUserName);
        progressBar = findViewById(R.id.progressBar);

        textUserName.setText(receiverUser.name);

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages, myId);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());
        buttonImage.setOnClickListener(v -> selectImage());

        listenMessages();
    }

    private void sendMessage() {
        String text = inputMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage();
        msg.senderId = myId;
        msg.receiverId = receiverUser.id;
        msg.message = text;
        msg.timestamp = System.currentTimeMillis();
        msg.imageUrl = null;

        db.collection("conversations")
                .add(msg)
                .addOnSuccessListener(r -> inputMessage.setText(""))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal kirim: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void listenMessages() {

        db.collection("conversations")
                .whereEqualTo("senderId", myId)
                .whereEqualTo("receiverId", receiverUser.id)
                .addSnapshotListener((value, error) -> onMessagesEvent(value, error));

        db.collection("conversations")
                .whereEqualTo("senderId", receiverUser.id)
                .whereEqualTo("receiverId", myId)
                .addSnapshotListener((value, error) -> onMessagesEvent(value, error));
    }

    private void onMessagesEvent(@Nullable QuerySnapshot value, @Nullable Exception error) {
        if (error != null || value == null) return;

        int oldSize = messages.size();

        for (DocumentChange dc : value.getDocumentChanges()) {
            if (dc.getType() == DocumentChange.Type.ADDED) {
                ChatMessage msg = dc.getDocument().toObject(ChatMessage.class);
                messages.add(msg);
            }
        }

        if (messages.size() > oldSize) {
            Collections.sort(messages, Comparator.comparingLong(m -> m.timestamp));
            chatAdapter.notifyDataSetChanged();
            recyclerViewChat.smoothScrollToPosition(messages.size() - 1);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) uploadImage(uri);
        }
    }

    private void uploadImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("chat_images/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot ->
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        progressBar.setVisibility(View.GONE);
                        sendMessageWithImage(uri.toString());

                    }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Gagal ambil URL gambar", Toast.LENGTH_SHORT).show();
                    })
            )
            .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void sendMessageWithImage(String imageUrl) {
        ChatMessage msg = new ChatMessage();
        msg.senderId = myId;
        msg.receiverId = receiverUser.id;
        msg.message = "";
        msg.timestamp = System.currentTimeMillis();
        msg.imageUrl = imageUrl;

        db.collection("conversations")
                .add(msg)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal kirim gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
