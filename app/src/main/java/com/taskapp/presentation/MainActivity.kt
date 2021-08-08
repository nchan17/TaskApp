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

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()

        binding.registerTextView.setOnClickListener(this)
        binding.logInButton.setOnClickListener(this)
        binding.forgotPasswTextView.setOnClickListener(this)
        setContentView(binding.root)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_textView -> {
                    startActivity(Intent(this, RegisterUserActivity::class.java))
                }
                R.id.log_in_button -> {
                    userLogin()
                }
                R.id.forgot_passw_textView -> {
                    startActivity(Intent(this, PasswordRecoveryActivity::class.java))
                }
            }
        }
    }

    private fun userLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

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
        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password is required"
            binding.passwordEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.passwordEditText.error = "Password should be at least 6 characters"
            binding.passwordEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(this, HomePageActivity::class.java))
            } else {
                Toast.makeText(this, "Failed to log in", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}