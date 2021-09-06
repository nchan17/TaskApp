package com.taskapp.presentation.authorization

import android.app.Application
import android.util.Patterns
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taskapp.R
import com.taskapp.domain.User

class AuthorisationViewModel(app: Application) : AndroidViewModel(app) {

    val isCreateUserSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun validateFullName(name: String): String? {
        if (name.isEmpty()) {
            return getString(R.string.empty_full_name_error)
        }
        return null
    }

    fun validatePhoneNumber(phone: String): String? {
        if (phone.isEmpty()) {
            return getString(R.string.empty_phone_number_error)
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            return getString(R.string.invalid_phone_number_error)
        }
        return null
    }

    fun validateEmail(email: String): String? {
        if (email.isEmpty()) {
            return getString(R.string.empty_email_error)
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return getString(R.string.invalid_email_error)
        }
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isEmpty()) {
            return getString(R.string.empty_password_error)
        }
        if (password.length < 6) {
            return getString(R.string.short_password_error)
        }
        return null
    }


    private fun getString(stringId: Int): String {
        return getApplication<Application>().resources.getString(stringId)
    }


    fun registerUser(user: User, password: String) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(user.email!!, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val createUserTask =
                    mAuth.currentUser?.let {
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(it.uid)
                    }
                createUserTask?.set(user)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        isCreateUserSuccessful.postValue(true)
                    } else {
                        isCreateUserSuccessful.postValue(false)
                    }
                }
            } else {
                isCreateUserSuccessful.postValue(false)
            }
        }
    }
}