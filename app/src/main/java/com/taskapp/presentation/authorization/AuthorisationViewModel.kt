package com.taskapp.presentation.authorization

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.taskapp.R

class AuthorisationViewModel(app: Application) : AndroidViewModel(app) {


    fun validateFullName(name: String): String?{
        if (name.isEmpty()) {
            return getString(R.string.empty_full_name_error)
        }
        return null
    }

    fun validatePhoneNumber(phone: String): String?{
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
}