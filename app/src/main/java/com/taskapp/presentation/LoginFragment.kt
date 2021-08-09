package com.taskapp.presentation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.databinding.FragmentLoginBinding
import com.taskapp.R


class LoginFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        binding.registerTextView.setOnClickListener(this)
        binding.logInButton.setOnClickListener(this)
        binding.forgotPasswTextView.setOnClickListener(this)

//        binding.emailEditText.editText?.doOnTextChanged { _,_,_,_ ->
//            binding.emailEditText.error = null
//        }
//
//        binding.passwordEditText.editText?.doOnTextChanged { _,_,_,_ ->
//            binding.passwordEditText.error = null
//        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_textView -> {
                    v.findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
                }
                R.id.log_in_button -> {
                    userLogin()
                }
                R.id.forgot_passw_textView -> {
                    v.findNavController().navigate(R.id.action_loginFragment_to_passwordRecoveryFragment)
                }
            }
        }
    }

    private fun userLogin() {
        val email = binding.emailEditText.editText?.text.toString().trim()
        val password = binding.passwordEditText.editText?.text.toString().trim()

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
        if (password.isEmpty()) {
            binding.passwordEditText.editText?.error = getString(R.string.empty_password_error)
            binding.passwordEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.passwordEditText.editText?.error = getString(R.string.short_password_error)
            binding.passwordEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_homePageFragment)
            } else {
                Toast.makeText(context, getString(R.string.login_fail_error), Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}