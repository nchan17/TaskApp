package com.taskapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.databinding.ActivityMainBinding
import com.taskapp.databinding.ActivityPasswordRecoveryBinding

class PasswordRecoveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordRecoveryBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()
        binding.resetButton.setOnClickListener {
            resetPassword()
        }

        setContentView(binding.root)
    }

    private fun resetPassword() {
        val email = binding.emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailEditText.error = "Email is required"
            binding.emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Please provide valid email"
            binding.emailEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Check your email to reset your password", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            } else {
                Toast.makeText(this, "Try again, something went wrong!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}