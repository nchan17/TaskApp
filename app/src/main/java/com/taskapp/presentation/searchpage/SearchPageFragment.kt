package com.taskapp.presentation.searchpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.R
import com.taskapp.core.domain.Task
import com.taskapp.databinding.FragmentSearchPageBinding

class SearchPageFragment : Fragment(), SearchTaskRecyclerAdapter.SearchTaskClickInterface {
    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: SearchTaskRecyclerAdapter
    private var tasks: ArrayList<Task> = arrayListOf<Task>()
    private var taskLsId: ArrayList<String> = arrayListOf<String>()
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
                FirebaseFirestore.getInstance().collection("tasks")
//                    .whereEqualTo("employer_id", it.uid)
            }
        ref?.get()
            ?.addOnSuccessListener { documents ->
                taskLsId.clear()
                tasks.clear()
                for (document in documents) {
                    taskLsId.add(document.id)
                    tasks.add(document.toObject<Task>())
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
        val bundle = Bundle()
        bundle.putString(TASK_ID, taskLsId[index])
        bundle.putString(TASK_EMPLOYER_ID, tasks[index].employer_id)
        bundle.putString(TASK_TITLE, tasks[index].title)
        bundle.putString(TASK_DESC, tasks[index].description)
        bundle.putString(TASK_PRICE, tasks[index].price.toString() + " ₾")

        val date =
            tasks[index].creation_data?.date.toString() + "-" + tasks[index].creation_data?.month.toString() + "-" + tasks[index].creation_data?.year?.plus(
                1900
            )
                .toString()
        bundle.putString(TASK_CREATION_DATA, date)

        view?.findNavController()
            ?.navigate(R.id.action_searchPageFragment_to_taskPageFragment, bundle)
    }

    companion object {
        private const val TASK_ID = "TASK_ID"
        private const val TASK_EMPLOYER_ID = "TASK_EMPLOYER_ID"
        private const val TASK_TITLE = "TASK_TITLE"
        private const val TASK_DESC = "TASK_DESC"
        private const val TASK_PRICE = "TASK_PRICE"
        private const val TASK_CREATION_DATA = "TASK_CREATION_DATA"
    }
}
