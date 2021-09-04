package com.taskapp.presentation.searchpage

import android.R.attr.*
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.taskapp.R
import com.taskapp.domain.User
import com.taskapp.databinding.FragmentTaskPageBinding
import android.widget.LinearLayout
import com.taskapp.domain.Review
import java.util.*


class TaskPageFragment : Fragment() {
    private var _binding: FragmentTaskPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchPageViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var taskId: String
    private lateinit var fragmentType: String
    private lateinit var status: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskPageBinding.inflate(inflater, container, false)
        return binding.root
    }

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
                binding.infoTextView.text = "This user is working on your Task!"
            }
            TYPE.ARCHIVE.name -> {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = "You finished this user's Task!"
                viewModel.checkIfAlreadyReviewed(taskId, userId)
            }
            TYPE.ARCHIVE_MY_CREATED.name -> {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = "This user finished your Task!"
                viewModel.checkIfAlreadyReviewed(taskId, userId)
            }
        }

        setUpViews()
        setupClickListeners()
        userId?.let { viewModel.getAllUserData(it) }
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
        binding.doneButton.setOnClickListener {
            viewModel.sendFinished(taskId)
        }
        binding.reviewButton.setOnClickListener {
            showReviewDialog()
        }
    }

    private fun setUpViews() {
        binding.titleTextView.text = arguments?.getString(TASK_TITLE)
        binding.descriptionTextView.text = arguments?.getString(TASK_DESC)
        binding.priceTextView.text = arguments?.getString(TASK_PRICE)
        binding.dateTextView.text = arguments?.getString(TASK_CREATION_DATA)
    }

    private fun addObservers(view: View) {
        viewModel.getUserDataIsSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                viewModel.profilePicLiveData.value?.let {
                    binding.profilePictureImageView.setImageBitmap(
                        it
                    )
                }
                val data = viewModel.userLiveData.value
                if (data != null) {
                    showUserData(data)
                } else {
                    showToast("error no such user")
                }
            } else {
                showToast("error getting user data")
            }
            binding.progressBar.visibility = GONE
        })

        viewModel.sendOfferIsSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                showToast("Offer was sent")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        })

        viewModel.offerAlreadySent.observe(viewLifecycleOwner, { result ->
            if (!result) {
                binding.offerButton.visibility = VISIBLE
            } else {
                binding.infoTextView.visibility = VISIBLE
                binding.infoTextView.text = "You've already sent an offer!"
            }
        })

        viewModel.isFinishTaskSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                showToast("Congrats! Task is DONE!")
                Navigation.findNavController(view).popBackStack()
            } else {
                showToast(getString(R.string.general_error))
            }
        })

        viewModel.alreadyReviewed.observe(viewLifecycleOwner, { result ->
            if (!result) {
                binding.reviewButton.visibility = VISIBLE
            }
        })

        viewModel.isSendReviewSuccessful.observe(viewLifecycleOwner, { result ->
            if (result) {
                showToast("Your review was sent!")
                binding.reviewButton.visibility = GONE
            } else {
                showToast(getString(R.string.general_error))
            }
        })
    }


    private fun showUserData(user: User) {
        binding.employerNameTextView.text = user.fullName
    }

    private fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            status: String
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
            return bundle
        }
    }

}