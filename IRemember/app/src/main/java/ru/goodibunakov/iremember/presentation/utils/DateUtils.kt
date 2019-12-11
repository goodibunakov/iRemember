package ru.goodibunakov.iremember.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    const val FORMAT_TIME_ONLY = "HH:mm"
    const val FORMAT_DATE_ONLY = "dd.MM.yy"
    const val FORMAT_DATE_FULL = "dd.MM.yy HH:mm"

    fun getDate(date: Long, type: String): String {
        return SimpleDateFormat(type, Locale.getDefault()).format(date)
    }
}