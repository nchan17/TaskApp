package com.taskapp.presentation.mytaskspage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.taskapp.domain.Status
import com.taskapp.domain.Task
import com.taskapp.domain.TaskOffer
import com.taskapp.domain.User
import java.io.File

class MyTasksViewModel(app: Application) : AndroidViewModel(app) {
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    var myCreatedTasks: ArrayList<Task> = arrayListOf()
    var archivedMyCreatedTasks: ArrayList<Task> = arrayListOf()

    var inProgressAssignedTasks: ArrayList<Task> = arrayListOf()
    var archivedAssignedTasks: ArrayList<Task> = arrayListOf()

    var offersUserList: ArrayList<User> = arrayListOf()

    val isGetAllTasksSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isGetOffersUserDataSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val isGetOffersUserPicsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isAcceptOfferSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val profilePicList: ArrayList<Bitmap> = arrayListOf()

    fun getAllMyTasks(userId: String) {
        val getMyCreatedTask = FirebaseFirestore
            .getInstance()
            .collection("tasks")
            .whereEqualTo("employer_id", userId)
            .get()

        val getMyAssignedTask = FirebaseFirestore
            .getInstance()
            .collection("tasks")
            .whereEqualTo("employee_id", userId)
            .get()

        Tasks.whenAll(getMyCreatedTask, getMyAssignedTask).addOnCompleteListener {
            if (getMyCreatedTask.isSuccessful) {
                myCreatedTasks.clear()
                archivedMyCreatedTasks.clear()
                val documents = getMyCreatedTask.result
                if (documents != null) {
                    for (document in documents) {
                        val currTask = document.toObject() as Task
                        currTask.id = document.id
                        if (currTask.status == Status.DONE) {
                            archivedMyCreatedTasks.add(currTask)
                        } else {
                            myCreatedTasks.add(currTask)
                        }
                    }
                }
            }
            if (getMyAssignedTask.isSuccessful) {
                inProgressAssignedTasks.clear()
                archivedAssignedTasks.clear()
                val documents = getMyAssignedTask.result
                if (documents != null) {
                    for (document in documents) {
                        val currTask = document.toObject() as Task
                        currTask.id = document.id
                        if (currTask.status == Status.DONE) {
                            archivedAssignedTasks.add(currTask)
                        } else {
                            inProgressAssignedTasks.add(currTask)
                        }
                    }
                }
            }
            if (!getMyAssignedTask.isSuccessful && !getMyCreatedTask.isSuccessful) {
                isGetAllTasksSuccessful.postValue(false)
            } else {
                isGetAllTasksSuccessful.postValue(true)
            }
        }
    }

    fun getTaskOffers(taskId: String) {
        val getTaskOffers = FirebaseFirestore
            .getInstance()
            .collection("task_offers")
            .whereEqualTo("taskId", taskId)
            .get()

        getTaskOffers
            .addOnSuccessListener { documents ->
                val offersList: ArrayList<String> = arrayListOf()
                for (document in documents) {
                    val currTaskOffer = document.toObject() as TaskOffer
                    offersList.add(currTaskOffer.employeeId!!)
                }
                getTaskOffers(offersList)
            }.addOnFailureListener {
                isGetOffersUserDataSuccessful.postValue(false)
            }
    }

    private fun getTaskOffers(offersList: ArrayList<String>) {
        val taskList: ArrayList<com.google.android.gms.tasks.Task<DocumentSnapshot>> = arrayListOf()
        for (userId in offersList) {
            val taskGetUserTask = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(userId)
                .get()
            taskList.add(taskGetUserTask)
        }

        Tasks.whenAll(taskList)
            .addOnSuccessListener {
                taskList.forEachIndexed { index, task ->
                    val user = task.result?.toObject<User>()
                    if (user != null) {
                        user.id = offersList[index]
                        offersUserList.add(user)
                    }
                }
                getAllProfilePictures(offersList)
            }.addOnFailureListener {
                isGetOffersUserDataSuccessful.postValue(false)
            }
    }

    private fun getAllProfilePictures(usersList: ArrayList<String>) {
        val localFileLs = MutableList(usersList.size) { File.createTempFile("profile", "jpeg") }
        val profileDownloadTaskLs: ArrayList<FileDownloadTask> = arrayListOf()
        usersList.forEachIndexed { index, userId ->
            val storageRef = storage.reference.child(userId)
            profileDownloadTaskLs.add(storageRef.getFile(localFileLs[index]))
        }
        Tasks.whenAll(profileDownloadTaskLs)
            .addOnSuccessListener {
                profileDownloadTaskLs.forEachIndexed { index, _ ->
                    profilePicList.add(
                        BitmapFactory.decodeFile(localFileLs[index].absolutePath)
                    )
                }
                isGetOffersUserPicsSuccessful.postValue(true)
                isGetOffersUserDataSuccessful.postValue(true)
            }.addOnFailureListener {
                isGetOffersUserPicsSuccessful.postValue(false)
                isGetOffersUserDataSuccessful.postValue(true)
            }
    }


    fun acceptOffer(employeeId: String, taskId: String) {
        val setEmployeeTask =
            FirebaseFirestore.getInstance().collection("tasks").document(taskId).update(
                mapOf(
                    "employee_id" to employeeId,
                    "status" to Status.IN_PROGRESS
                )
            )
        setEmployeeTask.addOnSuccessListener {
            isAcceptOfferSuccessful.postValue(true)
        }.addOnFailureListener {
            isAcceptOfferSuccessful.postValue(false)
        }

    }
}
