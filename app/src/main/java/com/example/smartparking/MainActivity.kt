package com.example.smartparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        setContentView(R.layout.activity_main)

        // Customer Button (assume ID: btnCustomer)
        findViewById<android.widget.Button>(R.id.btnCustomer)?.setOnClickListener {
            startActivity(Intent(this, CustomerActivity::class.java))
        }

        // Guard Button (assume ID: btnGuard)
        findViewById<android.widget.Button>(R.id.btnGuard)?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}