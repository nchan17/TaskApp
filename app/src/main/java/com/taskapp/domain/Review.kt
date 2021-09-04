package com.taskapp.domain

import java.util.*

data class Review(
    val reviewer_id: String? = null,
    val reviewee_id: String? = null,
    val num_stars: Float? = null,
    val comment: String? = null,
    val creation_date: Date? = null,
)