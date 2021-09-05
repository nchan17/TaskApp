package com.taskapp.presentation.searchpage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.domain.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SearchPageViewModel(app: Application) : AndroidViewModel(app) {
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    val userLiveData: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val profilePicLiveData: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    val getUserDataIsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val sendOfferIsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val offerAlreadySent: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val alreadyReviewed: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isFinishTaskSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSendReviewSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSearchTasksSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var userRating: Float = 0F

    var searchTasksLs: ArrayList<Task> = arrayListOf()

    val filteredTasksLs: ArrayList<Task> = arrayListOf()

    fun getAllUserData(userId: String) {
        val storageRef = storage.reference.child(userId)
        val localFile: File = File.createTempFile("profile", "jpeg")

        val profilePicTask = storageRef.getFile(localFile)
        val taskGetUserTask = FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(userId)
            .get()

        val taskGetUserRating = FirebaseFirestore
            .getInstance()
            .collection("ratings")
            .whereEqualTo("reviewee_id", userId)
            .get()

        Tasks.whenAll(taskGetUserTask, profilePicTask).addOnCompleteListener {
            if (profilePicTask.isSuccessful) {
                profilePicLiveData.postValue(BitmapFactory.decodeFile(localFile.absolutePath))
            }
            if (taskGetUserRating.isSuccessful) {
                val documents = taskGetUserRating.result
                if (documents != null) {
                    var size = 0
                    var sumOfRating = 0F
                    for (document in documents) {
                        val currReview = document.toObject() as Review
                        currReview.num_stars?.let {
                            sumOfRating += currReview.num_stars
                            size++
                        }
                    }
                    if (size != 0) {
                        userRating = sumOfRating / size
                    }
                }
            }
            if (taskGetUserTask.isSuccessful) {
                userLiveData.postValue(taskGetUserTask.result?.toObject<User>())
                getUserDataIsSuccessful.postValue(true)
            } else {
                getUserDataIsSuccessful.postValue(false)
            }
        }
    }

    fun sendOffer(userId: String, taskId: String) {
        val taskOffer = TaskOffer(userId, taskId)
        val ref =
            FirebaseFirestore.getInstance().collection("task_offers").document()
        ref.set(taskOffer).addOnCompleteListener {
            if (it.isSuccessful) {
                sendOfferIsSuccessful.postValue(true)
            } else {
                sendOfferIsSuccessful.postValue(false)
            }
        }
    }

    fun checkIfOfferAlreadySent(userId: String, taskId: String) {
        val ref =
            FirebaseFirestore.getInstance().collection("task_offers")
                .whereEqualTo("employeeId", userId)
                .whereEqualTo("taskId", taskId)
        ref.get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    offerAlreadySent.postValue(false)
                } else {
                    offerAlreadySent.postValue(true)
                }
            }.addOnFailureListener {
                offerAlreadySent.postValue(false)
            }
    }

    fun checkIfAlreadyReviewed(taskId: String, userId: String) {
        val ref =
            FirebaseFirestore.getInstance()
                .collection("ratings")
                .document(taskId + userId)
        ref.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    alreadyReviewed.postValue(true)
                } else {
                    alreadyReviewed.postValue(false)
                }
            }.addOnFailureListener {
                alreadyReviewed.postValue(false)
            }
    }

    fun sendFinished(taskId: String) {
        val setEmployeeTask =
            FirebaseFirestore.getInstance().collection("tasks").document(taskId).update(
                mapOf("status" to Status.DONE)
            )
        setEmployeeTask.addOnSuccessListener {
            isFinishTaskSuccessful.postValue(true)
        }.addOnFailureListener {
            isFinishTaskSuccessful.postValue(false)
        }
    }

    fun sendReview(review: Review, taskId: String) {
        val ref =
            FirebaseFirestore.getInstance().collection("ratings")
                .document(taskId + review.reviewee_id)
        ref.set(review).addOnCompleteListener {
            if (it.isSuccessful) {
                isSendReviewSuccessful.postValue(true)
            } else {
                isSendReviewSuccessful.postValue(false)
            }
        }
    }

    fun searchTasks(excludeUserId: String) {
        val ref =
            FirebaseFirestore.getInstance()
                .collection("tasks")
                .whereNotEqualTo("employer_id", excludeUserId)

        ref.get()
            .addOnSuccessListener { documents ->
                searchTasksLs.clear()
                documents.forEachIndexed { _, document ->
                    val currTask = document.toObject() as Task
                    if (currTask.status == Status.TO_DO) {
                        currTask.id = document.id
                        searchTasksLs.add(currTask)
                    }
                }
                searchTasksLs.sortByDescending { it.creation_data }
                filteredTasksLs.clear()
                filteredTasksLs.addAll(searchTasksLs)
                isSearchTasksSuccessful.postValue(true)
            }
            .addOnFailureListener {
                isSearchTasksSuccessful.postValue(false)
            }
    }

    fun filterTasksByString(searchStr: String): Boolean {
        searchStr.lowercase(Locale.getDefault())
        filteredTasksLs.clear()
        for (task in searchTasksLs) {
            if (task.title?.lowercase(Locale.getDefault())?.contains(searchStr) == true ||
                task.description?.lowercase(Locale.getDefault())?.contains(searchStr) == true
            ) {
                filteredTasksLs.add(task)
            }
        }
        return true
    }

}