package com.medise.bashga.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.medise.bashga.domain.model.PersonEntity
import java.text.DecimalFormat
import java.util.*


fun String.isValidHour(is24HourFormat: Boolean = true): Boolean = this.toIntOrNull().let {
    if (is24HourFormat) {
        it != null && it >= 0 && it <= 23
    } else {
        it != null && it > 0 && it <= 12
    }
}

fun String.isValidMinute(): Boolean = this.toIntOrNull().let {
    it != null && it >= 0 && it <= 59
}

fun String.isValidWindowLength(): Boolean = this.toIntOrNull().let {
    it != null && it >= 10 && it <= 60
}

fun String.isNotZero(): Boolean = this.toIntOrNull().let {
    it != null && it > 0
}

fun Calendar.setHourAndMinute(hour: Int, minute: Int): Calendar {
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)

    return this
}

fun Long.plusOneDay(): Long = this + 24 * 60 * 60 * 1000

fun Int.toMillis(): Long = this * 60 * 1000L

fun toUserFriendlyText(millis: Long, is24HourFormat: Boolean = true): String {
    val decimalFormat = DecimalFormat("00")
    val calender = Calendar.getInstance()

    calender.timeInMillis = millis

    if (is24HourFormat) {
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        return "${decimalFormat.format(hour)}:${decimalFormat.format(minute)}"
    } else {
        val amPm = calender.get(Calendar.AM_PM)
        val calenderHour = calender.get(Calendar.HOUR)
        val hour: Int = if (calenderHour == 0) {
            12
        } else {
            calenderHour
        }
        val minute = calender.get(Calendar.MINUTE)
        val amPmString = if (amPm == Calendar.AM) "AM" else "PM"
        return "${decimalFormat.format(hour)}:${decimalFormat.format(minute)} ($amPmString)"
    }
}

fun toUserFriendlyText(millis: Long, intervalMillis: Long, is24HourFormat: Boolean = true): String {
    val decimalFormat = DecimalFormat("00")
    val calender = Calendar.getInstance()
    calender.timeInMillis = millis

    val intervalMinute: Int = (intervalMillis / 1000 / 60).toInt()

    if (is24HourFormat) {
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)

        return "${decimalFormat.format(hour)}:${decimalFormat.format(minute)}" +
                "(${decimalFormat.format(intervalMinute)})"
    } else {
        val amPm = calender.get(Calendar.AM_PM)
        val calendarHour = calender.get(Calendar.HOUR)
        val hour: Int = if (calendarHour == 0) {
            12
        } else {
            calendarHour
        }
        val minute = calender.get(Calendar.MINUTE)
        val amPmString = if (amPm == Calendar.AM) "AM" else "PM"
        return "${decimalFormat.format(hour)}:${decimalFormat.format(minute)}" +
                "(${decimalFormat.format(intervalMinute)}) ($amPmString)"
    }
}

fun currentTimeMillis(): Long = Calendar.getInstance().timeInMillis

fun Int.toHour24Format(isAm: Boolean): Int {
    return if (isAm) {
        if (this == 12) {
            0
        } else {
            this
        }
    } else {
        if (this == 12) {
            12
        } else {
            this + 12
        }
    }
}

object TimeFormat {
    var is24HourFormat by mutableStateOf(false)
}