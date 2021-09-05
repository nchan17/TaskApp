package com.taskapp.presentation.mytaskspage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.databinding.FragmentMyTasksBinding
import com.taskapp.domain.Status
import com.taskapp.domain.Task
import com.taskapp.presentation.searchpage.TaskPageFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyTasksFragment : Fragment(), MyTasksRecyclerAdapter.MyTasksClickInterface {
    private var _binding: FragmentMyTasksBinding? = null
    private val binding get() = _binding!!

    private var listChild = HashMap<String, ArrayList<Task>>()
    private var listGroup = arrayListOf<String>()

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
        listGroup = arrayListOf(
            getString(R.string.my_tasks_in_progress),
            getString(R.string.my_tasks_your_created),
            getString(R.string.my_tasks_archive),
            getString(R.string.my_tasks_archive_your_created)
        )

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
                binding.myTasksExpandable.expandGroup(0)
                binding.myTasksExpandable.expandGroup(1)
                binding.myTasksExpandable.expandGroup(2)
                binding.myTasksExpandable.expandGroup(3)
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

        var date = ""
        currTask.creation_data?.let {
            val mCalendar = Calendar.getInstance()
            mCalendar.time = it
            date =
                SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(mCalendar.time)
        }

        when (groupPosition) {
            0 -> {
                val bundle = TaskPageFragment.newBundleInstance(
                    TaskPageFragment.Companion.TYPE.MY_TASKS_IN_PROGRESS.name,
                    currTask.id!!,
                    currTask.employee_id,
                    currTask.title,
                    currTask.description,
                    currTask.price.toString() + " ₾",
                    date,
                    currTask.status.name,
                )
                view?.findNavController()
                    ?.navigate(R.id.action_myTasksFragment_to_taskPageFragment, bundle)
            }
            1 -> {
                if (currTask.status == Status.TO_DO) {
                    val bundle = MyCreatedTasksFragment.newBundleInstance(
                        currTask.id!!,
                        currTask.title,
                        currTask.description,
                        currTask.price.toString() + " ₾",
                        date,
                        currTask.status.name
                    )
                    view?.findNavController()
                        ?.navigate(R.id.action_myTasksFragment_to_myCreatedTasksFragment, bundle)
                } else if (currTask.status == Status.IN_PROGRESS) {
                    val bundle = TaskPageFragment.newBundleInstance(
                        TaskPageFragment.Companion.TYPE.MY_CREATED_IN_PROGRESS.name,
                        currTask.id!!,
                        currTask.employee_id,
                        currTask.title,
                        currTask.description,
                        currTask.price.toString() + " ₾",
                        date,
                        currTask.status.name
                    )
                    view?.findNavController()
                        ?.navigate(R.id.action_myTasksFragment_to_taskPageFragment, bundle)
                }
            }
            2 -> {
                val bundle = TaskPageFragment.newBundleInstance(
                    TaskPageFragment.Companion.TYPE.ARCHIVE.name,
                    currTask.id!!,
                    currTask.employer_id,
                    currTask.title,
                    currTask.description,
                    currTask.price.toString() + " ₾",
                    date,
                    currTask.status.name,
                )
                view?.findNavController()
                    ?.navigate(R.id.action_myTasksFragment_to_taskPageFragment, bundle)
            }
            else -> {
                val bundle = TaskPageFragment.newBundleInstance(
                    TaskPageFragment.Companion.TYPE.ARCHIVE_MY_CREATED.name,
                    currTask.id!!,
                    currTask.employee_id,
                    currTask.title,
                    currTask.description,
                    currTask.price.toString() + " ₾",
                    date,
                    currTask.status.name,
                )
                view?.findNavController()
                    ?.navigate(R.id.action_myTasksFragment_to_taskPageFragment, bundle)
            }
        }
    }

}