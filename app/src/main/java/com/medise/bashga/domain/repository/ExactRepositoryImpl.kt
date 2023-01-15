package com.medise.bashga.domain.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.medise.bashga.data.repository.ExactRepository
import com.medise.bashga.receiver.InexactAlarmBroadcastReceiver
import com.medise.bashga.util.RepeatingAlarm
import com.medise.bashga.util.clearRepeatingAlarm
import com.medise.bashga.util.getRepeatingAlarm
import com.medise.bashga.util.putRepeatingAlarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


const val INEXACT_REPEATING_ALARM_REQUEST_CODE = 1004

const val ALARM_REQUEST_CODE_EXTRA = "alarm_request_code_extra"

class ExactRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ExactRepository {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private var repeatingAlarmState = mutableStateOf(RepeatingAlarm.NOT_SET)

    override fun getRepeatingAlarmState(): State<RepeatingAlarm> = repeatingAlarmState

    override fun rescheduleAlarms() {
        val repeatingAlarm = sharedPreferences.getRepeatingAlarm()
        if (repeatingAlarm.isSet()) {
            scheduleRepeatingAlarm(repeatingAlarm)
        } else {
            clearRepeatingAlarm()
        }
    }

    override fun scheduleRepeatingAlarm(repeatingAlarm: RepeatingAlarm) {
        val pendingIntent = createInexactAlarmIntent(INEXACT_REPEATING_ALARM_REQUEST_CODE)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            repeatingAlarm.triggerAtMillis,
            repeatingAlarm.intervalMillis,
            pendingIntent
        )

        sharedPreferences.putRepeatingAlarm(repeatingAlarm)
        repeatingAlarmState.value = repeatingAlarm
    }

    override fun clearRepeatingAlarm() {
        val pendingIntent = createInexactAlarmIntent(INEXACT_REPEATING_ALARM_REQUEST_CODE)
        alarmManager.cancel(pendingIntent)

        sharedPreferences.clearRepeatingAlarm()
        repeatingAlarmState.value = RepeatingAlarm.NOT_SET
    }


    private fun createInexactAlarmIntent(alarmRequestCode: Int): PendingIntent {
        val intent = Intent(context, InexactAlarmBroadcastReceiver::class.java).apply {
            putExtra(ALARM_REQUEST_CODE_EXTRA, alarmRequestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}