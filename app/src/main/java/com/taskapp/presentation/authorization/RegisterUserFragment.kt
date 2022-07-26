package com.taskapp.presentation.authorization

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentRegisterUserBinding
import com.taskapp.domain.User

class RegisterUserFragment : Fragment(R.layout.fragment_register_user), View.OnClickListener {

    private val binding by viewBinding(FragmentRegisterUserBinding::bind)

    private val viewModel: AuthorisationViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerTextView.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        addObservers()
    }

    private fun addObservers() {
        viewModel.isCreateUserSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast(getString(R.string.user_was_created_text))
                binding.progressBar.visibility = GONE
                view?.let { it1 -> Navigation.findNavController(it1).popBackStack() }
            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = GONE
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_button -> {
                    registerUser()
                }
            }
        }
    }

    private fun registerUser() {
        val email = binding.emailEditText.editText?.text.toString().trim()
        val password = binding.passwordEditText.editText?.text.toString().trim()
        val phone = binding.phoneNumberEditText.editText?.text.toString().trim()
        val fullName = binding.fullNameEditText.editText?.text.toString().trim()

        if (validateRegisterFields(email, password, phone, fullName)) {
            val user = User(fullName, phone, email)
            binding.progressBar.visibility = VISIBLE
            viewModel.registerUser(user, password)
        }
    }

    // returns false if invalid field was found
    private fun validateRegisterFields(
        email: String,
        password: String,
        phone: String,
        fullName: String
    ): Boolean {
        viewModel.validateFullName(fullName)?.let { errorText ->
            binding.fullNameEditText.editText?.error = errorText
            binding.fullNameEditText.requestFocus()
            return false
        }
        viewModel.validatePhoneNumber(phone)?.let { errorText ->
            binding.phoneNumberEditText.editText?.error = errorText
            binding.phoneNumberEditText.requestFocus()
            return false
        }
        viewModel.validateEmail(email)?.let { errorText ->
            binding.emailEditText.editText?.error = errorText
            binding.emailEditText.requestFocus()
            return false
        }
        viewModel.validatePassword(password)?.let { errorText ->
            binding.passwordEditText.editText?.error = errorText
            binding.passwordEditText.requestFocus()
            return false
        }
        return true
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}