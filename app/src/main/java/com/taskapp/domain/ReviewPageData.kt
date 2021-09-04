package com.taskapp.domain

import android.graphics.Bitmap

data class ReviewPageData(
    var userId: String,
    var userName: String,
    var rating: Float? = 0F,
    var comment: String? = null,
    var photo: Bitmap? = null,
)