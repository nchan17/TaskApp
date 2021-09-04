package com.taskapp.presentation.searchpage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.R
import com.taskapp.domain.Review
import com.taskapp.domain.Status
import com.taskapp.domain.TaskOffer
import com.taskapp.domain.User
import java.io.File

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

    fun getAllUserData(userId: String) {
        val storageRef = storage.reference.child(userId)
        val localFile: File = File.createTempFile("profile", "jpeg")

        val profilePicTask = storageRef.getFile(localFile)
        val taskGetUserTask = FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(userId)
            .get()

        Tasks.whenAll(taskGetUserTask, profilePicTask).addOnCompleteListener {
            if (profilePicTask.isSuccessful) {
                profilePicLiveData.postValue(BitmapFactory.decodeFile(localFile.absolutePath))
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
            FirebaseFirestore.getInstance().collection("ratings").document(taskId + userId)
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
                mapOf(
                    "status" to Status.DONE
                )
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

}