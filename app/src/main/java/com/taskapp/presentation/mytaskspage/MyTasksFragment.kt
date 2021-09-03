package com.taskapp.presentation.mytaskspage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.databinding.FragmentMyTasksBinding
import com.taskapp.domain.Task

class MyTasksFragment : Fragment(), MyTasksRecyclerAdapter.MyTasksClickInterface {
    private var _binding: FragmentMyTasksBinding? = null
    private val binding get() = _binding!!

    private var listGroup =
        arrayListOf("In Progress", "Your Created", "Archive", "Archive Your Created")
    private var listChild = HashMap<String, ArrayList<Task>>()

    private val viewModel: MyTasksViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    private lateinit var adapter: MyTasksRecyclerAdapter
    private lateinit var listener: MyTasksRecyclerAdapter.MyTasksClickInterface

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    private fun addObservers() {
        viewModel.isGetAllTasksSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                listChild[listGroup[0]] = viewModel.inProgressAssignedTasks
                listChild[listGroup[1]] = viewModel.myCreatedTasks
                listChild[listGroup[2]] = viewModel.archivedAssignedTasks
                listChild[listGroup[3]] = viewModel.archivedMyCreatedTasks
                adapter = MyTasksRecyclerAdapter(listGroup, listChild, listener)
                binding.myTasksExpandable.setAdapter(adapter)
                binding.progressBar.visibility = GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(groupPosition: Int, childPosition: Int) {
        val currTask = when (groupPosition) {
            0 -> {
                viewModel.inProgressAssignedTasks[childPosition]
            }
            1 -> {
                viewModel.myCreatedTasks[childPosition]
            }
            2 -> {
                viewModel.archivedAssignedTasks[childPosition]
            }
            else -> {
                viewModel.archivedMyCreatedTasks[childPosition]
            }
        }

        val date = currTask.creation_data?.date.toString() + "-" +
                currTask.creation_data?.month.toString() + "-" +
                currTask.creation_data?.year?.plus(1900).toString()
        val bundle = MyCreatedTasksFragment.newBundleInstance(
            currTask.id!!,
            currTask.title,
            currTask.description,
            currTask.price.toString() + " ₾",
            date
        )

        when (groupPosition) {
            0 -> {
//                viewModel.inProgressAssignedTasks[childPosition]
            }
            1 -> {
                view?.findNavController()
                    ?.navigate(R.id.action_myTasksFragment_to_myCreatedTasksFragment, bundle)
            }
            2 -> {
//                viewModel.archivedAssignedTasks[childPosition]
            }
            else -> {
//                viewModel.archivedMyCreatedTasks[childPosition]
            }
        }


    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }
}