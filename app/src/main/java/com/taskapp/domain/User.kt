package com.taskapp.domain

data class User(
    val fullName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    var id: String? = null
)