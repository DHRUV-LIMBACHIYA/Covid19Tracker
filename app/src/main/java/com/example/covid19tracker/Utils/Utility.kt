package com.example.covid19tracker.Utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Dhruv Limbachiya on 25-09-2021.
 */

fun getCurrentDate() : String {
    val date = Calendar.getInstance().time
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun getDayAgo(days: Int) : String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    val date = calendar.time
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
    Log.i("UTIL", "get7dayAgo: ${simpleDateFormat.format(date)}")
    return simpleDateFormat.format(date)
}

fun convertDateStringIntoString(dateString: String) : String {
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val dateTime = LocalDateTime.parse(dateString, formatter)
        val formatter2  = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return dateTime.format(formatter2)
    }
    return ""
}