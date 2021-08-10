package com.taskapp.core.domain

import java.util.*

data class Task(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val employer_id: String? = null,
    val creation_data: Date? = null,
    val status: Status = Status.TO_DO
)

enum class Status {
    TO_DO,
    IN_PROGRESS,
    DONE
}