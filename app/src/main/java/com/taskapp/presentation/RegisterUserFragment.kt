package com.taskapp.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.taskapp.R
import com.taskapp.core.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taskapp.databinding.FragmentRegisterUserBinding


class RegisterUserFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerTextView.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
//                R.id.banner_textView -> {
//                    startActivity(Intent(this, MainActivity::class.java))
//                }
                R.id.register_button -> {
                    registerUser()
                }
            }
        }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val phone = binding.phoneNumberEditText.text.toString().trim()
        val fullName = binding.fullNameEditText.text.toString().trim()


        if (fullName.isEmpty()) {
            binding.fullNameEditText.error = "Full name is required"
            binding.fullNameEditText.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            binding.phoneNumberEditText.error = "Phone number is required"
            binding.phoneNumberEditText.requestFocus()
            return
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            binding.phoneNumberEditText.error = "Please provide valid phone"
            binding.phoneNumberEditText.requestFocus()
            return
        }
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

        binding.progressBar.visibility = VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(fullName, phone, email)
                val ref =
                    mAuth.currentUser?.let {
                        FirebaseFirestore.getInstance().collection("users").document(
                            it.uid
                        )
                    }
                ref?.set(user)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "user was created", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = GONE
                    } else {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = GONE
                    }
                }

            } else {
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = GONE
            }
        }


    }
}