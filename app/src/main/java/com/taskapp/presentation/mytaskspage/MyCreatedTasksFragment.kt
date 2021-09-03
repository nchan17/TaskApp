package com.taskapp.presentation.mytaskspage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.databinding.FragmentMyCreatedTasksBinding

class MyCreatedTasksFragment : Fragment() {
    private var _binding: FragmentMyCreatedTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyTasksViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var taskId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCreatedTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()

        taskId = arguments?.getString(TASK_ID)!!
        setUpViews()
        setupClickListeners()
    }

    private fun setupClickListeners() {
//        TODO("Not yet implemented")
    }

    private fun setUpViews() {
        binding.titleTextView.text = arguments?.getString(TASK_TITLE)
        binding.descriptionTextView.text = arguments?.getString(TASK_DESC)
        binding.priceTextView.text = arguments?.getString(TASK_PRICE)
        binding.dateTextView.text = arguments?.getString(TASK_CREATION_DATA)
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
        private const val TASK_TITLE = "TASK_TITLE"
        private const val TASK_DESC = "TASK_DESC"
        private const val TASK_PRICE = "TASK_PRICE"
        private const val TASK_CREATION_DATA = "TASK_CREATION_DATA"

        fun newBundleInstance(
            taskId: String,
            title: String?,
            desc: String?,
            price: String,
            creation_data: String
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_ID, taskId)
            bundle.putString(TASK_TITLE, title)
            bundle.putString(TASK_DESC, desc)
            bundle.putString(TASK_PRICE, price)
            bundle.putString(TASK_CREATION_DATA, creation_data)
            return bundle
        }
    }
}