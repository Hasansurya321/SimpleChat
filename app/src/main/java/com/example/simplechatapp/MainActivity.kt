package com.example.simplechatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var textWelcome: TextView
    private lateinit var buttonOpenUsers: Button
    private lateinit var buttonLogout: Button

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(applicationContext)

        // Ambil view dari XML
        textWelcome = findViewById(R.id.textWelcome)
        buttonOpenUsers = findViewById(R.id.buttonOpenUsers)
        buttonLogout = findViewById(R.id.buttonLogout)

        val name = preferenceManager.getString("userName") ?: ""
        textWelcome.text = "Halo, $name"

        buttonOpenUsers.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }

        buttonLogout.setOnClickListener {
            preferenceManager.clear()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
