package com.taskapp.presentation.authorization

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.databinding.FragmentLoginBinding
import com.taskapp.R
import java.util.*


class LoginFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthorisationViewModel by viewModels()
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
        binding.languageButton.setOnClickListener {
            switchLanguage()
            reloadFragment(view)
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_textView -> {
                    v.findNavController()
                        .navigate(R.id.action_loginFragment_to_registerUserFragment)
                }
                R.id.log_in_button -> {
                    userLogin()
                }
                R.id.forgot_passw_textView -> {
                    v.findNavController()
                        .navigate(R.id.action_loginFragment_to_passwordRecoveryFragment)
                }
            }
        }
    }

    private fun switchLanguage() {
        if (resources.configuration.locale.language.toString() == "en") {
            setLocale("ka")
        } else {
            setLocale("en")
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        resources.configuration.setLocale(locale)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }

    private fun reloadFragment(view: View) {
        val navController = Navigation.findNavController(view)
        val id = navController.currentDestination?.id
        navController.popBackStack(id!!, true)
        navController.navigate(id)
    }

    private fun userLogin() {
        val email = binding.emailEditText.editText?.text.toString().trim()
        val password = binding.passwordEditText.editText?.text.toString().trim()

        viewModel.validateEmail(email)?.let { errorText ->
            binding.emailEditText.editText?.error = errorText
            binding.emailEditText.requestFocus()
            return
        }
        viewModel.validatePassword(password)?.let { errorText ->
            binding.passwordEditText.editText?.error = errorText
            binding.passwordEditText.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_homePageFragment)
            } else {
                showToast(getString(R.string.login_fail_error))
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}