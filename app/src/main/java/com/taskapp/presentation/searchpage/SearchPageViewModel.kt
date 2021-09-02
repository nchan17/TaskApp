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

    val getUserDataDone: MutableLiveData<Boolean> by lazy {
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
                getUserDataDone.postValue(true)
            } else {
                getUserDataDone.postValue(false)
            }
        }
    }

}