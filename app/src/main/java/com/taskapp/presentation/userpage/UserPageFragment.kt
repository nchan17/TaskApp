package com.taskapp.presentation.userpage

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.domain.User
import com.taskapp.databinding.FragmentUserPageBinding
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.taskapp.R


class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var userId: String
    private val viewModel: UserPageViewModel by viewModels()

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
        binding.progressBar.visibility = VISIBLE
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        addListeners()
        binding.ratingBar.rating = 4.5F
        viewModel.getAllUserData(userId)
        addObservers()
    }

    private fun addListeners() {
        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result?.data?.data
                    binding.profilePictureImageView.setImageURI(imageUri)
                    viewModel.uploadImageToFirebase(imageUri, userId)
                }
            }

        binding.profilePictureImageView.setOnClickListener {
            val openGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            registerForActivityResult.launch(openGalleryIntent)
        }

        binding.logoutButton.setOnClickListener {
            logOut()
        }
    }


    private fun addObservers() {
        viewModel.getUserDataDone.observe(viewLifecycleOwner, { result ->
            if (result) {
                binding.profilePictureImageView.setImageBitmap(viewModel.profilePicLiveData.value)
                val data = viewModel.userLiveData.value
                if (data != null) {
                    showUserData(data)
                } else {
                    showToast("error no such user")
                }
            } else {
                showToast("error getting user data")
            }
            binding.progressBar.visibility = GONE
        })

        viewModel.setProfilePicDone.observe(viewLifecycleOwner, { result ->
            if (result) {
                showToast("Data was sent")
                binding.progressBar.visibility = GONE
            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = GONE
            }
        })
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

