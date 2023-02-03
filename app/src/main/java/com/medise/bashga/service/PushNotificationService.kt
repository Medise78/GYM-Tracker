package com.medise.bashga.service

import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medise.bashga.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService:FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sharedPreferences.edit().putString(FB_TOKEN_NAME , p0).apply()
    }

    override fun onMessageReceived(remote: RemoteMessage) {
        super.onMessageReceived(remote)

        val title = remote.notification?.title?:""
        val desc = remote.notification?.body?:""

        showPushNotification(this , channelId , channelName , notificationId , title , desc)
    }
}