package com.taskapp.presentation.userpage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentUserPageBinding
import com.taskapp.domain.User
import java.util.*

class UserPageFragment : Fragment(R.layout.fragment_user_page), UserPageReviewsAdapter.ReviewTaskClickInterface {

    private val binding by viewBinding(FragmentUserPageBinding::bind)

    private lateinit var userId: String
    private lateinit var mAuth: FirebaseAuth
    private val viewModel: UserPageViewModel by viewModels()

    private lateinit var adapter: UserPageReviewsAdapter
    private lateinit var listener: UserPageReviewsAdapter.ReviewTaskClickInterface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = VISIBLE
        binding.recyclerProgressBar.visibility = VISIBLE
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.uid!!
        arguments?.getString(USER_PAGE_USER_ID)?.let { userId = it }

        if (userId == mAuth.uid!!) {
            binding.logoutButton.visibility = VISIBLE
            binding.languageButton.visibility = VISIBLE
        } else {
            binding.logoutButton.visibility = GONE
            binding.languageButton.visibility = GONE
        }
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        addListeners(view)
        viewModel.getAllUserData(userId)
        viewModel.getUserReviews(userId)
        addObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    private fun addListeners(view: View) {
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

        binding.languageButton.setOnClickListener {
            switchLanguage()
            reloadFragment(view)
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

    private fun addObservers() {
        viewModel.getUserDataDone.observe(viewLifecycleOwner) { result ->
            if (result) {
                viewModel.profilePicLiveData.value?.let {
                    binding.profilePictureImageView.setImageBitmap(
                        it
                    )
                }
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
        }

        viewModel.setProfilePicDone.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Data was sent")
                binding.progressBar.visibility = GONE
            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = GONE
            }
        }

        viewModel.isGetReviewsSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                adapter = UserPageReviewsAdapter(viewModel.reviewPageDataLs, listener)
                binding.recyclerView.adapter = adapter
                binding.recyclerProgressBar.visibility = GONE
            } else {
                showToast(getString(R.string.general_error))
                binding.recyclerProgressBar.visibility = GONE
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
            binding.fullNameTextView.text = user.fullName
            binding.emailTextView.text = getString(R.string.user_page_email_text, user.email)
            binding.phoneNumberTextView.text = getString(R.string.user_page_phone_text, user.phone)
            binding.ratingBar.rating = viewModel.userRating
            binding.numReviewsTextView.text =
                getString(R.string.user_page_review_text, viewModel.numRaters)
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    override fun onUserClick(index: Int) {
//        TODO("Not yet implemented")
    }

    companion object {
        private const val USER_PAGE_USER_ID = "USER_PAGE_USER_ID"

        fun newBundleInstance(
            user_id: String
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(USER_PAGE_USER_ID, user_id)
            return bundle
        }
    }
}

