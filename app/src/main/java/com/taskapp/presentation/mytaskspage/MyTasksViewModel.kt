package com.taskapp.presentation.mytaskspage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.taskapp.domain.Status
import com.taskapp.domain.Task

class MyTasksViewModel(app: Application) : AndroidViewModel(app) {

    var myCreatedTasks: ArrayList<Task> = arrayListOf()
    var archivedMyCreatedTasks: ArrayList<Task> = arrayListOf()

    var inProgressAssignedTasks: ArrayList<Task> = arrayListOf()
    var archivedAssignedTasks: ArrayList<Task> = arrayListOf()

    val isGetAllTasksSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

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

//        getMyCreatedTask
//            .addOnSuccessListener { documents ->
//                myCreatedTasks.clear()
//                archivedMyCreatedTasks.clear()
//                for (document in documents) {
//                    val currTask = document.toObject() as Task
//                    currTask.id = document.id
//                    if (currTask.status == Status.DONE) {
//                        archivedMyCreatedTasks.add(currTask)
//                    } else {
//                        myCreatedTasks.add(currTask)
//                    }
//                }
//                isGetAllTasksSuccessful.postValue(true)
//            }
//            .addOnFailureListener {
//                isGetAllTasksSuccessful.postValue(false)
//            }

//        getMyAssignedTask
//            .addOnSuccessListener { documents ->
//                inProgressAssignedTasks.clear()
//                archivedAssignedTasks.clear()
//                for (document in documents) {
//                    val currTask = document.toObject() as Task
//                    currTask.id = document.id
//                    if (currTask.status == Status.DONE) {
//                        archivedAssignedTasks.add(currTask)
//                    } else {
//                        inProgressAssignedTasks.add(currTask)
//                    }
//                }
//                isGetAllTasksSuccessful.postValue(true)
//            }
//            .addOnFailureListener {
//                isGetAllTasksSuccessful.postValue(false)
//            }

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
}