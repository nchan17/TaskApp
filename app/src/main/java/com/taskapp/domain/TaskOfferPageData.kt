package com.taskapp.domain

import android.graphics.Bitmap

data class TaskOfferPageData(
    var userId: String,
    var userName: String,
    var rating: Float = 0F,
    var photo: Bitmap? = null,
)