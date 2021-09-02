package com.taskapp.presentation.searchpage

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.R
import com.taskapp.domain.User
import com.taskapp.databinding.FragmentTaskPageBinding
import java.io.File

class TaskPageFragment : Fragment() {
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        val employerId = arguments?.getString(TASK_EMPLOYER_ID)
//        val taskId = arguments?.getString(TASK_ID)
        val title = arguments?.getString(TASK_TITLE)
        val desc = arguments?.getString(TASK_DESC)
        val price = arguments?.getString(TASK_PRICE)
        val date = arguments?.getString(TASK_CREATION_DATA)

        binding.progressBar.visibility = View.VISIBLE

        if (employerId != null) {
            val userRef =
                mAuth.currentUser?.let {
                    FirebaseFirestore.getInstance().collection("users").document(employerId)
                }
            userRef?.get()
                ?.addOnSuccessListener { data ->
                    if (data != null) {
                        data.toObject<User>()?.let { showUserData(it) }
                    } else {
                        showToast("error no such user")
                    }
                    binding.progressBar.visibility = View.GONE
                }
                ?.addOnFailureListener {
                    showToast(getString(R.string.general_error))
                    binding.progressBar.visibility = View.GONE
                }
        }

        binding.titleTextView.text = title
        binding.descriptionTextView.text = desc
        binding.priceTextView.text = price
        binding.dateTextView.text = date
        getProfilePhoto(employerId)
    }

    private fun getProfilePhoto(employerId: String?) {
        if (employerId == null) return
        val localFile: File = File.createTempFile("profile", "jpeg")

        val storageRef = storage.reference.child(employerId)
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitMap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.profilePictureImageView.setImageBitmap(bitMap)
            }
    }

    private fun showUserData(user: User) {
        binding.employerNameTextView.text = user.fullName
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TASK_ID = "TASK_ID"
        private const val TASK_EMPLOYER_ID = "TASK_EMPLOYER_ID"
        private const val TASK_TITLE = "TASK_TITLE"
        private const val TASK_DESC = "TASK_DESC"
        private const val TASK_PRICE = "TASK_PRICE"
        private const val TASK_CREATION_DATA = "TASK_CREATION_DATA"

        fun newBundleInstance(
            taskId: String,
            employer_id: String?,
            title: String?,
            desc: String?,
            price: String,
            creation_data: String
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_ID, taskId)
            bundle.putString(TASK_EMPLOYER_ID, employer_id)
            bundle.putString(TASK_TITLE, title)
            bundle.putString(TASK_DESC, desc)
            bundle.putString(TASK_PRICE, price)
            bundle.putString(TASK_CREATION_DATA, creation_data)
            return bundle
        }
    }

}