package com.taskapp.presentation.userpage

import android.app.Activity
import android.app.ProgressDialog
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
import com.taskapp.domain.User
import com.taskapp.databinding.FragmentUserPageBinding
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.R
import java.io.File


class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    private var currUser: User? = null
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var userId: String

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

        userId = FirebaseAuth.getInstance().currentUser!!.uid

        binding.profilePictureImageView.setOnClickListener {
            val openGalleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)
        }

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
        binding.ratingBar.rating = 4.5F
        getProfilePhoto()

    }

    private fun getProfilePhoto() {
        val storageRef = storage.reference.child(userId)

        val localFile: File = File.createTempFile("profile", "jpeg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.profilePictureImageView.setImageBitmap(bitMap)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data?.data
                binding.profilePictureImageView.setImageURI(imageUri)
                uploadImageToFirebase(imageUri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        binding.progressBar.visibility = VISIBLE
        if (imageUri != null) {
            val storageRef = storage.reference.child(userId)
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    showToast("Data was sent")
                    binding.progressBar.visibility = GONE
                }
                .addOnFailureListener {
                    showToast(getString(R.string.general_error))
                    binding.progressBar.visibility = GONE
                }
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
            currUser = user
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

