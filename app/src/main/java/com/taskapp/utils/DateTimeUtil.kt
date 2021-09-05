package com.taskapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun getDateToString(date: Date?): String {
        var result = ""
        date?.let {
            val mCalendar = Calendar.getInstance()
            mCalendar.time = it
            result =
                SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(mCalendar.time)
        }
        return result
    }
}