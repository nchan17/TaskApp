package com.taskapp.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.databinding.FragmentPasswordRecoveryBinding

class PasswordRecoveryFragment : Fragment() {
    private var _binding: FragmentPasswordRecoveryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordRecoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        binding.resetButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = binding.emailEditText.editText?.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailEditText.editText?.error = getString(R.string.empty_email_error)
            binding.emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.editText?.error = getString(R.string.invalid_email_error)
            binding.emailEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    context,
                    getString(R.string.check_email_for_password_reset_text),
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.progressBar.visibility = View.GONE
            } else {
                Toast.makeText(context, getString(R.string.something_wrong_error_text), Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}