package com.taskapp.presentation.searchpage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.taskapp.core.domain.Task

class SearchPageViewModel(app: Application) : AndroidViewModel(app) {
    var currentTask: Task? = null
    var currentTaskId: String? = null
}