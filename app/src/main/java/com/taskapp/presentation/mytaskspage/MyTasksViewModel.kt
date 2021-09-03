package com.taskapp.presentation.mytaskspage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.domain.Task

class MyTasksViewModel(app: Application) : AndroidViewModel(app) {

    var tasks: ArrayList<Task> = arrayListOf()
    private var taskLsId: ArrayList<String> = arrayListOf()

    val isGetAllTasksSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getAllMyTasks(userId: String) {
        val myCreatedTasks = FirebaseFirestore
            .getInstance()
            .collection("tasks")
            .whereEqualTo("employer_id", userId)
            .get()

        myCreatedTasks
            .addOnSuccessListener { documents ->
                taskLsId.clear()
                tasks.clear()
                for (document in documents) {
                    taskLsId.add(document.id)
                    tasks.add(document.toObject())
                }
                isGetAllTasksSuccessful.postValue(true)
            }
            .addOnFailureListener {
                isGetAllTasksSuccessful.postValue(false)
            }
    }
}