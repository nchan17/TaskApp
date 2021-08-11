package com.taskapp.presentation.searchpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.R
import com.taskapp.core.domain.Task
import com.taskapp.databinding.FragmentSearchPageBinding

class SearchPageFragment : Fragment() {
    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: SearchTaskRecyclerAdapter
    private var tasks: ArrayList<Task> = arrayListOf<Task>()

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
                for (document in documents) {
                    tasks.add(document.toObject<Task>())
                }
                adapter = SearchTaskRecyclerAdapter(tasks)
                binding.recyclerView.adapter = adapter
                binding.progressBar.visibility = View.GONE
            }
            ?.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                showToast(getString(R.string.general_error))
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
