package com.taskapp.presentation.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.taskapp.R
import com.taskapp.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taskapp.databinding.FragmentRegisterUserBinding


class RegisterUserFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthorisationViewModel by viewModels()
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

        if (!validateRegisterFields(email, password, phone, fullName)) {
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
                        showToast(getString(R.string.user_was_created_text))
                        binding.progressBar.visibility = GONE
                    } else {
                        showToast(getString(R.string.general_error))
                        binding.progressBar.visibility = GONE
                    }
                }

            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = GONE
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}