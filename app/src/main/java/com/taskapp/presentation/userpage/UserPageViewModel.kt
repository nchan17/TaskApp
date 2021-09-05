package com.taskapp.presentation.userpage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.domain.Review
import com.taskapp.domain.ReviewPageData
import com.taskapp.domain.User
import java.io.File

class UserPageViewModel(app: Application) : AndroidViewModel(app) {
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    val userLiveData: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val profilePicLiveData: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    val getUserDataDone: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val setProfilePicDone: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var reviewPageDataLs: ArrayList<ReviewPageData> = arrayListOf()

    val isGetReviewsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var userRating: Float = 0F

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

        Tasks.whenAll(taskGetUserTask, profilePicTask, taskGetUserRating).addOnCompleteListener {
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
                getUserDataDone.postValue(true)
            } else {
                getUserDataDone.postValue(false)
            }

        }
    }

    fun getUserReviews(userId: String) {
        val reviewsList: ArrayList<Review> = arrayListOf()

        val getReviewersListTask = FirebaseFirestore
            .getInstance()
            .collection("ratings")
            .whereEqualTo("reviewee_id", userId)
            .get()

        getReviewersListTask.addOnSuccessListener { documents ->
            for (document in documents) {
                val currReview = document.toObject() as Review
                reviewsList.add(currReview)
            }
            getReviewersUserData(reviewsList)
        }.addOnFailureListener {
            isGetReviewsSuccessful.postValue(false)
        }
    }

    private fun getReviewersUserData(reviewersList: ArrayList<Review>) {
        val taskList: ArrayList<com.google.android.gms.tasks.Task<DocumentSnapshot>> = arrayListOf()
        for (review in reviewersList) {
            val taskGetUserTask = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(review.reviewer_id!!)
                .get()
            taskList.add(taskGetUserTask)
        }

        Tasks.whenAll(taskList)
            .addOnSuccessListener {
                taskList.forEachIndexed { index, task ->
                    val user = task.result?.toObject<User>()
                    if (user != null) {
                        reviewPageDataLs.add(
                            ReviewPageData(
                                reviewersList[index].reviewer_id!!,
                                user.fullName!!,
                                reviewersList[index].num_stars ?: 0F,
                                reviewersList[index].comment,
                            )
                        )
                    }
                }
                getAllReviewerPictures()
            }.addOnFailureListener {
                isGetReviewsSuccessful.postValue(false)
            }
    }

    private fun getAllReviewerPictures() {
        val localFileLs = MutableList(reviewPageDataLs.size) {
            File.createTempFile("profile", "jpeg")
        }
        val profileDownloadTaskLs: ArrayList<FileDownloadTask> = arrayListOf()
        reviewPageDataLs.forEachIndexed { index, reviewPageData ->
            val storageRef = storage.reference.child(reviewPageData.userId)
            profileDownloadTaskLs.add(storageRef.getFile(localFileLs[index]))
        }
        Tasks.whenAll(profileDownloadTaskLs)
            .addOnCompleteListener {
                profileDownloadTaskLs.forEachIndexed { index, fileDownloadTask ->
                    if (fileDownloadTask.isSuccessful) {
                        reviewPageDataLs[index].photo =
                            BitmapFactory.decodeFile(localFileLs[index].absolutePath)
                    } else {
                        reviewPageDataLs[index].photo = null
                    }
                }
                isGetReviewsSuccessful.postValue(true)
            }
    }

    fun uploadImageToFirebase(imageUri: Uri?, userId: String) {
        if (imageUri != null) {
            val storageRef = storage.reference.child(userId)
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    setProfilePicDone.postValue(true)
                }
                .addOnFailureListener {
                    setProfilePicDone.postValue(false)
                }
        }
    }


}