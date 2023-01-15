package com.medise.bashga.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.medise.bashga.data.repository.GymRepository
import com.medise.bashga.util.showNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NOTIFICATION_ID = 1002
private const val NOTIFICATION_CHANNEL_ID = "gym_alarm"
private const val NOTIFICATION_CHANNEL_NAME = "Gym Alarms"


@AndroidEntryPoint
class InexactAlarmBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: GymRepository

    override fun onReceive(context: Context, intent: Intent) {

        CoroutineScope(Dispatchers.IO).launch {
            repository.getAllPerson().onEach { personEntityList ->
                showNotification(
                    context,
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NOTIFICATION_ID,
                    personEntityList.map { it.name?:"" },
                    personEntityList.size
                )
            }.launchIn(this)
        }
    }
}