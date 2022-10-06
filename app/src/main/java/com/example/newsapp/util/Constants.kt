
package com.example.newsapp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val API_KEY = "20b03be6136a411fa7b33343dbb40ab1"
    const val BASE_URL = "http://newsapi.org"

    fun changeDateTimeFormat(dateTime: String): String? {
        val isDate: String?
        val dateFormat = SimpleDateFormat("dd-mm-yy", Locale.ENGLISH)
        isDate = try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(dateTime)
            dateFormat.format(date)
        } catch (e: ParseException) {
            dateTime
        }
        return isDate
    }

}