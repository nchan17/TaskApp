package com.taskapp.presentation.authorization

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentPasswordRecoveryBinding

class PasswordRecoveryFragment : Fragment(R.layout.fragment_password_recovery) {

    private val binding by viewBinding(FragmentPasswordRecoveryBinding::bind)

    private val viewModel: AuthorisationViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        binding.resetButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = binding.emailEditText.editText?.text.toString().trim()
        viewModel.validateEmail(email)?.let { errorText ->
            binding.emailEditText.editText?.error = errorText
            binding.emailEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(getString(R.string.check_email_for_password_reset_text))
                binding.progressBar.visibility = View.GONE
                view?.let { it1 -> Navigation.findNavController(it1).popBackStack() }
            } else {
                showToast(getString(R.string.something_wrong_error_text))
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}