package com.yan.ahtpodcast002.utils

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun jsonDateToShortDate(jsonDate: String?): String {

        if (jsonDate == null) {
            return "-"
        }

        val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inFormat.parse(jsonDate) ?: return "-"
        val outputFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        return outputFormat.format(date)
    }

    fun xmlToDate(dateString:String?):Date{
        val date = dateString ?: return Date()
        val inFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        Log.d("DateUtils.kt","${Locale.getDefault()}")
        return inFormat.parse(date)?: Date()
    }

}