package com.taskapp.presentation.mytaskspage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentCreateTaskBinding
import com.taskapp.domain.Status
import com.taskapp.domain.Task
import java.util.*

class CreateTaskFragment : Fragment(R.layout.fragment_create_task) {

    private val binding by viewBinding(FragmentCreateTaskBinding::bind)

    private lateinit var mAuth: FirebaseAuth
    private val viewModel: MyTasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        addClickListeners()
        addObservers(view)
    }

    private fun addObservers(view: View) {
        viewModel.isCreateTaskSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast(getString(R.string.create_task_success_text))
                binding.progressBar.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun addClickListeners() {
        binding.createTaskButton.setOnClickListener {
            if (validateFields()) {
                createTask()
            }
        }
    }

    private fun createTask() {
        binding.progressBar.visibility = View.VISIBLE

        val title = binding.titleEditText.editText?.text.toString().trim()
        val description = binding.descriptionEditText.editText?.text.toString().trim()
        val price = binding.priceEditText.editText?.text.toString().trim().toDouble()
        val secret = binding.secretDataEditText.editText?.text.toString().trim()

        viewModel.createTask(
            Task(
                title,
                description,
                price,
                mAuth.currentUser?.uid,
                Calendar.getInstance().time,
                Status.TO_DO,
                secret
            )
        )
    }

    private fun validateFields(): Boolean {
        if (binding.titleEditText.editText?.text.isNullOrEmpty()) {
            binding.titleEditText.editText?.error = getString(R.string.empty_field_error)
            binding.titleEditText.requestFocus()
            return false
        }
        if (binding.descriptionEditText.editText?.text.isNullOrEmpty()) {
            binding.descriptionEditText.editText?.error = getString(R.string.empty_field_error)
            binding.descriptionEditText.requestFocus()
            return false
        }
        if (binding.priceEditText.editText?.text.isNullOrEmpty()) {
            binding.priceEditText.editText?.error = getString(R.string.empty_field_error)
            binding.priceEditText.requestFocus()
            return false
        }
        return true
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}