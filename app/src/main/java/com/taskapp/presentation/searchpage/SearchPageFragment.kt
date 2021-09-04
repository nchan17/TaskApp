package com.taskapp.presentation.searchpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.R
import com.taskapp.domain.Task
import com.taskapp.databinding.FragmentSearchPageBinding
import com.taskapp.domain.Status

class SearchPageFragment : Fragment(), SearchTaskRecyclerAdapter.SearchTaskClickInterface {
    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: SearchTaskRecyclerAdapter
    private var tasks: ArrayList<Task> = arrayListOf()
    private var taskLsId: ArrayList<String> = arrayListOf()
    private lateinit var listener: SearchTaskRecyclerAdapter.SearchTaskClickInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        binding.progressBar.visibility = View.VISIBLE
        val ref =
            mAuth.currentUser?.let {
                FirebaseFirestore.getInstance()
                    .collection("tasks")
                    .whereNotEqualTo("employer_id", it.uid)
            }
        ref?.get()
            ?.addOnSuccessListener { documents ->
                taskLsId.clear()
                tasks.clear()
                for (document in documents) {
                    val currTask = document.toObject() as Task
                    if (currTask.status == Status.TO_DO) {
                        taskLsId.add(document.id)
                        tasks.add(document.toObject())
                    }
                }
                adapter = SearchTaskRecyclerAdapter(
                    tasks,
                    listener
                )
                binding.recyclerView.adapter = adapter
                binding.progressBar.visibility = View.GONE
            }
            ?.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showToast(getString(R.string.general_error))
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(index: Int) {
        val date =
            tasks[index].creation_data?.date.toString() + "-" + tasks[index].creation_data?.month.toString() + "-" + tasks[index].creation_data?.year?.plus(
                1900
            )
                .toString()
        val bundle = TaskPageFragment.newBundleInstance(
            TaskPageFragment.Companion.TYPE.SEARCH_TO_DO.name,
            taskLsId[index],
            tasks[index].employer_id,
            tasks[index].title,
            tasks[index].description,
            tasks[index].price.toString() + " ₾",
            date,
            tasks[index].status.name
        )

        view?.findNavController()
            ?.navigate(R.id.action_searchPageFragment_to_taskPageFragment, bundle)
    }

}
