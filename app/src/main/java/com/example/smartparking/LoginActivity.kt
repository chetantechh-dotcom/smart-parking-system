package com.example.smartparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Login Button (assume ID: btnLogin)
        findViewById<android.widget.Button>(R.id.btnLogin)?.setOnClickListener {
            val email = findViewById<android.widget.EditText>(R.id.etEmail)?.text.toString().trim()
            val password = findViewById<android.widget.EditText>(R.id.etPassword)?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, GuardActivity::class.java))
                            finish()
                        } else {
                            val error = when (task.exception) {
                                is FirebaseAuthInvalidUserException -> "User not found"
                                is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                                else -> "Login failed"
                            }
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}