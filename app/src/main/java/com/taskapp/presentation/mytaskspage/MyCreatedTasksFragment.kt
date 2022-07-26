package com.taskapp.presentation.mytaskspage

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentMyCreatedTasksBinding
import com.taskapp.domain.Status
import com.taskapp.presentation.userpage.UserPageFragment

class MyCreatedTasksFragment : Fragment(R.layout.fragment_my_created_tasks), TaskOffersAdapter.TaskOfferClickInterface {

    private val binding by viewBinding(FragmentMyCreatedTasksBinding::bind)

    private val viewModel: MyTasksViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var taskId: String

    private lateinit var adapter: TaskOffersAdapter
    private lateinit var listener: TaskOffersAdapter.TaskOfferClickInterface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        taskId = arguments?.getString(TASK_ID)!!
        if (arguments?.getString(TASK_STATUS) == Status.TO_DO.name) {
            viewModel.getTaskOffers(taskId)
        } else {
            binding.progressBar.visibility = View.GONE
        }

        setUpViews()
        setupClickListeners()
        setupObservers(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    private fun setupClickListeners() {
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.my_created_tasks_page_delete_confirmation_text))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.general_yes_text)) { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton(getString(R.string.general_no_text)) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun setupObservers(view: View) {
        viewModel.isGetOffersUserDataSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                adapter =
                    TaskOffersAdapter(viewModel.taskOfferPageDataLs, listener)
                binding.recyclerView.adapter = adapter
                binding.progressBar.visibility = View.GONE
            } else {
                showToast(getString(R.string.general_error))
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.isAcceptOfferSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Offer was accepted!")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        }

        viewModel.isDeleteTaskSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Task successfully deleted!")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        }
    }

    private fun setUpViews() {
        binding.titleTextView.text = arguments?.getString(TASK_TITLE)
        binding.descriptionTextView.text = arguments?.getString(TASK_DESC)
        binding.priceTextView.text = arguments?.getString(TASK_PRICE)
        binding.dateTextView.text = arguments?.getString(TASK_CREATION_DATA)
        binding.secretDataTextView.text = arguments?.getString(TASK_SECRET_DATA)
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TASK_ID = "TASK_ID"
        private const val TASK_TITLE = "TASK_TITLE"
        private const val TASK_DESC = "TASK_DESC"
        private const val TASK_PRICE = "TASK_PRICE"
        private const val TASK_STATUS = "TASK_STATUS"
        private const val TASK_CREATION_DATA = "TASK_CREATION_DATA"
        private const val TASK_SECRET_DATA = "TASK_SECRET_DATA"

        fun newBundleInstance(
            taskId: String,
            title: String?,
            desc: String?,
            price: String,
            creation_data: String,
            status: String,
            secretData: String?,
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_ID, taskId)
            bundle.putString(TASK_TITLE, title)
            bundle.putString(TASK_DESC, desc)
            bundle.putString(TASK_PRICE, price)
            bundle.putString(TASK_CREATION_DATA, creation_data)
            bundle.putString(TASK_STATUS, status)
            bundle.putString(TASK_SECRET_DATA, secretData)
            return bundle
        }
    }

    override fun onUserClick(index: Int) {
        val bundle = UserPageFragment.newBundleInstance(
            viewModel.taskOfferPageDataLs[index].userId
        )
        view?.findNavController()
            ?.navigate(R.id.action_myCreatedTasksFragment_to_userPageFragment2, bundle)
    }

    override fun onAcceptOfferClick(index: Int) {
        viewModel.acceptOffer(viewModel.taskOfferPageDataLs[index].userId, taskId)
    }
}