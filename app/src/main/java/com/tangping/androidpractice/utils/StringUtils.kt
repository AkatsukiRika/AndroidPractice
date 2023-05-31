package com.tangping.androidpractice.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StringUtils {
    fun convertTimestampToString(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}