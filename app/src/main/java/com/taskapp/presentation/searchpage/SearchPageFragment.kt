package com.taskapp.presentation.searchpage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentSearchPageBinding
import com.taskapp.utils.DateTimeUtil
import com.taskapp.utils.PriceUtil

class SearchPageFragment : Fragment(R.layout.fragment_search_page), SearchTaskRecyclerAdapter.SearchTaskClickInterface {

    private val binding by viewBinding(FragmentSearchPageBinding::bind)

    private val viewModel: SearchPageViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: SearchTaskRecyclerAdapter

    private lateinit var listener: SearchTaskRecyclerAdapter.SearchTaskClickInterface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.progressBar.visibility = View.VISIBLE

        setListeners()
        viewModel.searchTasks(mAuth.uid!!)
        addObservers()
    }

    private fun setListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                if (viewModel.filterTasksByString(qString)) {
                    adapter.setNewTaskList(viewModel.filteredTasksLs)
                }
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                return true
            }
        })
    }


    private fun addObservers() {
        viewModel.isSearchTasksSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                adapter = SearchTaskRecyclerAdapter(
                    viewModel.searchTasksLs,
                    listener
                )
                binding.recyclerView.adapter = adapter
            } else {
                showToast(getString(R.string.general_error))
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(index: Int) {
        val tasks = viewModel.filteredTasksLs[index]

        val bundle = TaskPageFragment.newBundleInstance(
            TaskPageFragment.Companion.TYPE.SEARCH_TO_DO.name,
            tasks.id!!,
            tasks.employer_id,
            tasks.title,
            tasks.description,
            PriceUtil.getPrice(tasks.price),
            DateTimeUtil.getDateToString(tasks.creation_data),
            tasks.status.name,
            null
        )

        view?.findNavController()
            ?.navigate(R.id.action_searchPageFragment_to_taskPageFragment, bundle)
    }
}
