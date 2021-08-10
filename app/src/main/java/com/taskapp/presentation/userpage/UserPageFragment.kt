package com.taskapp.presentation.userpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.core.domain.User
import com.taskapp.databinding.FragmentUserPageBinding
import android.content.Intent


class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        binding.progressBar.visibility = VISIBLE
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    showUserData(data.toObject<User>())
                } else {
                    showToast("error no such user")
                }
                binding.progressBar.visibility = GONE
            }
            .addOnFailureListener {
                showToast("error getting user data")
                binding.progressBar.visibility = GONE
            }
        binding.logoutButton.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val packName = context?.packageName
        if (packName != null) {
            val i = context?.packageManager?.getLaunchIntentForPackage(packName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
        activity?.finish()
    }

    private fun showUserData(user: User?) {
        if (user != null) {
            binding.fullNameTextView.text = user.fullName
            binding.emailTextView.text = user.email
            binding.phoneNumberTextView.text = user.phone
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

