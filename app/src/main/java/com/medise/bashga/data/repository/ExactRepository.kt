package com.medise.bashga.data.repository

import androidx.compose.runtime.State
import com.medise.bashga.util.RepeatingAlarm

interface ExactRepository {

    fun getRepeatingAlarmState():State<RepeatingAlarm>

    fun rescheduleAlarms()

    fun scheduleRepeatingAlarm(repeatingAlarm: RepeatingAlarm)

    fun clearRepeatingAlarm()
}