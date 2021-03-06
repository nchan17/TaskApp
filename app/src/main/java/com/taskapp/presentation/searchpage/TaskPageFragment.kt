package com.taskapp.presentation.searchpage

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.common.viewBinding
import com.taskapp.R
import com.taskapp.databinding.FragmentTaskPageBinding
import com.taskapp.domain.Review
import com.taskapp.domain.User
import com.taskapp.presentation.userpage.UserPageFragment
import java.util.*


class TaskPageFragment : Fragment(R.layout.fragment_task_page) {

    private val binding by viewBinding(FragmentTaskPageBinding::bind)

    private val viewModel: SearchPageViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var taskId: String
    private lateinit var fragmentType: String
    private lateinit var status: String
    private lateinit var userId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = VISIBLE
        mAuth = FirebaseAuth.getInstance()

        userId = arguments?.getString(TASK_EMPLOYER_ID)!!
        fragmentType = arguments?.getString(TASK_PAGE_FRAGMENT_TYPE)!!
        taskId = arguments?.getString(TASK_ID)!!
        status = arguments?.getString(TASK_STATUS)!!

        when (fragmentType) {
            TYPE.MY_TASKS_IN_PROGRESS.name -> {
                binding.doneButton.visibility = VISIBLE
            }
            TYPE.SEARCH_TO_DO.name -> {
                mAuth.uid?.let { viewModel.checkIfOfferAlreadySent(it, taskId) }
            }
            TYPE.MY_CREATED_IN_PROGRESS.name -> {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = getString(R.string.task_page_user_is_working)
            }
            TYPE.ARCHIVE.name -> {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = getString(R.string.task_page_you_finished_task)
                viewModel.checkIfAlreadyReviewed(taskId, userId)
            }
            TYPE.ARCHIVE_MY_CREATED.name -> {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = getString(R.string.task_page_user_finished_task)
                viewModel.checkIfAlreadyReviewed(taskId, userId)
            }
        }

        setUpViews()
        setupClickListeners()
        userId.let { viewModel.getAllUserData(it) }
        addObservers(view)
    }

    private fun showReviewDialog() {
        val inputLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        inputLp.setMargins(50, 20, 50, 20)

        val input = EditText(context)
        input.hint = "Enter Comment"
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.layoutParams = inputLp

        val ratingLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ratingLp.setMargins(50, 20, 50, 20)

        val ratingBar = RatingBar(context)
        ratingBar.numStars = 5
        ratingBar.stepSize = 0.1F
        ratingBar.layoutParams = ratingLp
        ratingBar.isClickable = true

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(ratingBar)
        linearLayout.addView(input)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle("Review Tasker!")
            .setView(linearLayout)
            .setPositiveButton("Review") { _, _ ->
                val mComment = input.text.toString()
                val mRating = ratingBar.rating
                viewModel.sendReview(
                    Review(
                        mAuth.uid!!,
                        userId,
                        mRating,
                        mComment,
                        Calendar.getInstance().time
                    ),
                    taskId
                )
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        builder.show()
    }

    private fun setupClickListeners() {
        binding.offerButton.setOnClickListener {
            viewModel.sendOffer(mAuth.uid!!, taskId)
        }
        binding.withdrawOfferButton.setOnClickListener {
            viewModel.withdrawOffer(mAuth.uid!!, taskId)
        }
        binding.doneButton.setOnClickListener {
            viewModel.sendFinished(taskId)
        }
        binding.reviewButton.setOnClickListener {
            showReviewDialog()
        }
        binding.userConstraintLayout.setOnClickListener {
            val bundle = UserPageFragment.newBundleInstance(userId)
            view?.findNavController()
                ?.navigate(R.id.action_taskPageFragment_to_userPageFragment2, bundle)
        }
    }

    private fun setUpViews() {
        binding.titleTextView.text = arguments?.getString(TASK_TITLE)
        binding.descriptionTextView.text = arguments?.getString(TASK_DESC)
        binding.priceTextView.text = arguments?.getString(TASK_PRICE)
        binding.dateTextView.text = arguments?.getString(TASK_CREATION_DATA)
        binding.secretDataTextView.text = arguments?.getString(TASK_SECRET_DATA)
    }

    private fun addObservers(view: View) {
        viewModel.getUserDataIsSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                viewModel.profilePicLiveData.value?.let {
                    binding.profilePictureImageView.setImageBitmap(it)
                }
                binding.ratingBar.rating = viewModel.userRating
                val data = viewModel.userLiveData.value
                if (data != null) {
                    showUserData(data)
                } else {
                    showToast(getString(R.string.general_error))
                }
            } else {
                showToast(getString(R.string.general_error))
            }
            binding.progressBar.visibility = GONE
        }

        viewModel.sendOfferIsSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Offer was sent")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        }

        viewModel.withdrawOfferIsSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Offer was withdrawn")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        }

        viewModel.offerAlreadySent.observe(viewLifecycleOwner) { result ->
            if (!result) {
                binding.offerButton.visibility = VISIBLE
            } else {
                binding.withdrawOfferButton.visibility = VISIBLE
            }
        }

        viewModel.isFinishTaskSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Congrats! Task is DONE!")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        }

        viewModel.alreadyReviewed.observe(viewLifecycleOwner) { result ->
            if (!result) {
                binding.reviewButton.visibility = VISIBLE
            }
        }

        viewModel.isSendReviewSuccessful.observe(viewLifecycleOwner) { result ->
            if (result) {
                showToast("Your review was sent!")
                binding.reviewButton.visibility = GONE
            } else {
                showToast(getString(R.string.general_error))
            }
        }
    }


    private fun showUserData(user: User) {
        binding.employerNameTextView.text = user.fullName
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TASK_ID = "TASK_ID"
        private const val TASK_EMPLOYER_ID = "TASK_EMPLOYER_ID"
        private const val TASK_TITLE = "TASK_TITLE"
        private const val TASK_DESC = "TASK_DESC"
        private const val TASK_PRICE = "TASK_PRICE"
        private const val TASK_CREATION_DATA = "TASK_CREATION_DATA"
        private const val TASK_PAGE_FRAGMENT_TYPE = "TASK_PAGE_FRAGMENT_TYPE"
        private const val TASK_STATUS = "TASK_STATUS"
        private const val TASK_SECRET_DATA = "TASK_SECRET_DATA"

        enum class TYPE {
            SEARCH_TO_DO,
            MY_TASKS_IN_PROGRESS,
            MY_CREATED_IN_PROGRESS,
            ARCHIVE,
            ARCHIVE_MY_CREATED
        }

        fun newBundleInstance(
            fragmentType: String,
            taskId: String,
            employer_id: String?,
            title: String?,
            desc: String?,
            price: String,
            creation_data: String,
            status: String,
            secretData: String?,
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(TASK_PAGE_FRAGMENT_TYPE, fragmentType)
            bundle.putString(TASK_ID, taskId)
            bundle.putString(TASK_EMPLOYER_ID, employer_id)
            bundle.putString(TASK_TITLE, title)
            bundle.putString(TASK_DESC, desc)
            bundle.putString(TASK_PRICE, price)
            bundle.putString(TASK_CREATION_DATA, creation_data)
            bundle.putString(TASK_STATUS, status)
            bundle.putString(TASK_SECRET_DATA, secretData)
            return bundle
        }
    }
}