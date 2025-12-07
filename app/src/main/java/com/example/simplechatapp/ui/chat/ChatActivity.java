package com.example.simplechatapp.ui.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapp.R;
import com.example.simplechatapp.adapter.ChatAdapter;
import com.example.simplechatapp.model.ChatMessage;
import com.example.simplechatapp.model.User;
import com.example.simplechatapp.utils.ChatHistoryManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1001;

    private RecyclerView recyclerViewChat;
    private EditText inputMessage;
    private Button buttonSend, buttonImage;
    private TextView textUserName;
    private ProgressBar progressBar;
    private ImageView btnBack;

    private User receiverUser;
    private FirebaseFirestore db;
    private String myId;
    private String chatRoomId;

    private List<ChatMessage> messages;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverUser = (User) getIntent().getSerializableExtra("user");
        if (receiverUser == null) {
            Toast.makeText(this, "Error: User tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        chatRoomId = getChatRoomId(myId, receiverUser.id);

        initViews();
        textUserName.setText(receiverUser.name);
        setupRecyclerView();

        buttonSend.setOnClickListener(v -> sendMessage());
        buttonImage.setOnClickListener(v -> selectImage());
        btnBack.setOnClickListener(v -> finish());

        listenMessages();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        inputMessage = findViewById(R.id.inputMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonImage = findViewById(R.id.buttonImage);
        textUserName = findViewById(R.id.textUserName);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        messages = ChatHistoryManager.loadMessages(this, receiverUser.id);
        chatAdapter = new ChatAdapter(messages, myId);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
        if (!messages.isEmpty()) {
            recyclerViewChat.scrollToPosition(messages.size() - 1);
        }
    }

    private String getChatRoomId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) > 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    private void listenMessages() {
        db.collection("conversations")
            .whereEqualTo("chatRoomId", chatRoomId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat pesan.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value == null) return;

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        ChatMessage msg = dc.getDocument().toObject(ChatMessage.class);
                        if (messages.stream().noneMatch(m -> m.timestamp == msg.timestamp)) {
                            messages.add(msg);
                        }
                    }
                }
                
                Collections.sort(messages, Comparator.comparingLong(m -> m.timestamp));
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.smoothScrollToPosition(messages.size() - 1);

                ChatHistoryManager.saveMessages(this, receiverUser.id, messages);
            });
    }

    private void sendMessage() {
        String text = inputMessage.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        inputMessage.setText("");

        ChatMessage msg = new ChatMessage();
        msg.senderId = myId;
        msg.receiverId = receiverUser.id;
        msg.message = text;
        msg.timestamp = System.currentTimeMillis();
        msg.chatRoomId = chatRoomId;

        db.collection("conversations").add(msg)
            .addOnSuccessListener(docRef -> Toast.makeText(this, "Pesan terkirim", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal mengirim pesan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(text);
            });
    }
    
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadImage(data.getData());
        }
    }

    private void uploadImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference ref = FirebaseStorage.getInstance().getReference("chat_images/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                progressBar.setVisibility(View.GONE);
                sendMessageWithImage(uri.toString());
            }))
            .addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Upload gambar gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void sendMessageWithImage(String imageUrl) {
        ChatMessage msg = new ChatMessage();
        msg.senderId = myId;
        msg.receiverId = receiverUser.id;
        msg.message = "";
        msg.timestamp = System.currentTimeMillis();
        msg.imageUrl = imageUrl;
        msg.chatRoomId = chatRoomId;

        db.collection("conversations").add(msg)
            .addOnFailureListener(e -> Toast.makeText(this, "Gagal mengirim gambar", Toast.LENGTH_SHORT).show());
    }
}
