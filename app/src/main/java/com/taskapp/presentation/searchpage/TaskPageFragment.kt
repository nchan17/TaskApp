package com.taskapp.presentation.searchpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.R
import com.taskapp.core.domain.Task
import com.taskapp.core.domain.User
import com.taskapp.databinding.FragmentTaskPageBinding
import java.text.SimpleDateFormat

class TaskPageFragment : Fragment() {
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private val viewModel: SearchPageViewModel by viewModels()

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
        val employerId = viewModel.currentTask?.employer_id
        val taskId = arguments?.getString(TASK_ID)

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

        if (taskId != null) {
            val taskRef =
                mAuth.currentUser?.let {
                    FirebaseFirestore.getInstance().collection("tasks").document(taskId)
                }
            taskRef?.get()
                ?.addOnSuccessListener { data ->
                    if (data != null) {
                        data.toObject<Task>()?.let {
                            binding.titleTextView.text = viewModel.currentTask?.title
                            binding.descriptionTextView.text = viewModel.currentTask?.description
                            binding.priceTextView.text = viewModel.currentTask?.price.toString()
                        }
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

        val taskTime = viewModel.currentTask?.creation_data?.time
        if (taskTime != null) {
            val date =
                SimpleDateFormat("dd-mm-yyyy").format(taskTime)
            binding.dateTextView.text = date
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

        fun newInstance(taskId: String): TaskPageFragment {
            val myFragment = TaskPageFragment()
            val args = Bundle()
            args.putString(TASK_ID, taskId)
            myFragment.arguments = args
            return myFragment
        }
    }

}