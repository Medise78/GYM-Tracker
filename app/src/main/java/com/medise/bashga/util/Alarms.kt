package com.medise.bashga.util

import android.content.SharedPreferences
import java.util.Calendar

private const val INEXACT_REPEATING_ALARM_TRIGGER_PREFERENCES_KEY =
    "inexact_repeating_alarm_trigger"
private const val INEXACT_REPEATING_ALARM_INTERVAL_PREFERENCES_KEY =
    "inexact_repeating_alarm_window"
private const val REPEATING_ALARM_STATE = "repeating_alarm_state"

private const val ALARM_NOT_SET = -1L

fun SharedPreferences.getRepeatingAlarm(): RepeatingAlarm {
    val triggerAtMillis = getLong(INEXACT_REPEATING_ALARM_TRIGGER_PREFERENCES_KEY, ALARM_NOT_SET)
    val intervalMillis = getLong(INEXACT_REPEATING_ALARM_INTERVAL_PREFERENCES_KEY, ALARM_NOT_SET)

    return RepeatingAlarm(triggerAtMillis, intervalMillis)
}

fun SharedPreferences.putRepeatingAlarm(repeatingAlarm: RepeatingAlarm) {
    edit().putLong(INEXACT_REPEATING_ALARM_TRIGGER_PREFERENCES_KEY, repeatingAlarm.triggerAtMillis)
        .apply()
    edit().putLong(INEXACT_REPEATING_ALARM_INTERVAL_PREFERENCES_KEY, repeatingAlarm.intervalMillis)
        .apply()
}

fun SharedPreferences.clearRepeatingAlarm() {
    edit().putLong(INEXACT_REPEATING_ALARM_INTERVAL_PREFERENCES_KEY, ALARM_NOT_SET).apply()
    edit().putLong(INEXACT_REPEATING_ALARM_TRIGGER_PREFERENCES_KEY, ALARM_NOT_SET).apply()
}

fun SharedPreferences.putAlarmEnabled(isEnabled:Boolean):Boolean{
    return edit().putBoolean(REPEATING_ALARM_STATE , isEnabled).commit()
}

fun SharedPreferences.isAlarmEnabled():Boolean{
    return getBoolean(REPEATING_ALARM_STATE , false)
}

fun convertToAlarmTimeMillis(hour: Int, minute: Int): Long {
    val calender = Calendar.getInstance()
    val currentTimeMillis = calender.timeInMillis
    val proposedTimeMillis = calender.setHourAndMinute(
        hour, minute
    ).timeInMillis

    val alarmTimeMillis: Long = if (proposedTimeMillis > currentTimeMillis) {
        proposedTimeMillis
    } else {
        proposedTimeMillis.plusOneDay()
    }
    return alarmTimeMillis
}

data class RepeatingAlarm(
    val triggerAtMillis: Long,
    val intervalMillis: Long
) {
    companion object {
        val NOT_SET = RepeatingAlarm(
            ALARM_NOT_SET, ALARM_NOT_SET
        )
    }

    fun isSet(): Boolean = triggerAtMillis != ALARM_NOT_SET && intervalMillis != ALARM_NOT_SET
}