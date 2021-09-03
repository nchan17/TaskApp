package com.taskapp.presentation.mytaskspage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.databinding.FragmentMyTasksBinding
import com.taskapp.domain.Task

class MyTasksFragment : Fragment() {
    private var _binding: FragmentMyTasksBinding? = null
    private val binding get() = _binding!!

    private var listGroup =
        arrayListOf("In Progress", "Your Created", "Archive", "Archive Your Created")
    private var listChild = HashMap<String, ArrayList<Task>>()

    private val viewModel: MyTasksViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    private lateinit var adapter: MyTasksRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding.progressBar.visibility = VISIBLE

        binding.createTaskButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_myTasksFragment_to_createTaskFragment)
        }

        viewModel.getAllMyTasks(mAuth.uid!!)
        addObservers()

    }

    private fun addObservers() {
        viewModel.isGetAllTasksSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                listChild[listGroup[1]] = viewModel.tasks
                adapter = MyTasksRecyclerAdapter(listGroup, listChild)
                binding.myTasksExpandable.setAdapter(adapter)
                binding.progressBar.visibility = GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}