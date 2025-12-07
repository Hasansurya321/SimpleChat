package com.example.simplechatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.simplechatapp.ui.auth.SignInActivity
import com.example.simplechatapp.ui.profile.ProfileActivity
import com.example.simplechatapp.ui.users.UsersActivity
import com.example.simplechatapp.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var textWelcome: TextView
    private lateinit var buttonOpenUsers: Button
    private lateinit var buttonLogout: Button
    private lateinit var profileImage: CircleImageView

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi PreferenceManager dari paket utils
        preferenceManager = PreferenceManager(applicationContext)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        textWelcome = findViewById(R.id.txtWelcome)
        buttonOpenUsers = findViewById(R.id.btnUsers)
        buttonLogout = findViewById(R.id.btnLogout)
        profileImage = findViewById(R.id.profile_image)

        val name = preferenceManager.getString("userName") ?: ""
        textWelcome.text = "Halo, $name"

        loadProfileImage()

        profileImage.setOnClickListener {
            // Intent ke ProfileActivity di paket ui.profile
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        buttonOpenUsers.setOnClickListener {
            // Intent ke UsersActivity di paket ui.users
            startActivity(Intent(this, UsersActivity::class.java))
        }

        buttonLogout.setOnClickListener {
            mAuth.signOut()
            preferenceManager.clear()
            // Intent ke SignInActivity di paket ui.auth
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileImage() {
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val profileImageUrl = documentSnapshot.getString("profileImage")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(profileImage)
                        }
                    }
                }
        }
    }
}
