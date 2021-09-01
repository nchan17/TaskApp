package com.taskapp.presentation.searchpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.R
import com.taskapp.core.domain.User
import com.taskapp.databinding.FragmentTaskPageBinding

class TaskPageFragment : Fragment() {
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!

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

//        fun newInstance(taskId: String): TaskPageFragment {
//            val myFragment = TaskPageFragment()
//            val args = Bundle()
//            args.putString(TASK_ID, taskId)
//            myFragment.arguments = args
//            return myFragment
//        }
    }

}