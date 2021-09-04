package com.taskapp.presentation.mytaskspage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taskapp.R
import com.taskapp.domain.Task
import com.taskapp.databinding.FragmentCreateTaskBinding
import java.util.*

class CreateTaskFragment : Fragment() {
    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createTaskButton.setOnClickListener {
            createTask(view)
        }
        mAuth = FirebaseAuth.getInstance()
    }

    private fun createTask(view: View) {
        binding.progressBar.visibility = View.VISIBLE

        val title = binding.titleEditText.editText?.text.toString().trim()
        val description = binding.descriptionEditText.editText?.text.toString().trim()
        val price = binding.priceEditText.editText?.text.toString().trim().toDouble()

        val task = Task(
            title,
            description,
            price,
            mAuth.currentUser?.uid,
            Calendar.getInstance().time,
        )

        val ref =
            mAuth.currentUser?.let {
                FirebaseFirestore.getInstance().collection("tasks").document()
            }
        ref?.set(task)?.addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(getString(R.string.create_task_success_text))
                binding.progressBar.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
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