package com.medise.bashga.util

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.medise.bashga.MainActivity
import com.medise.bashga.R

fun showNotification(
    context: Context,
    channelId: String,
    channelName: String,
    notificationId: Int,
    contentTitle: List<String>? = emptyList(),
    personCount: Int
) {
    val startAppIntent = Intent(context, MainActivity::class.java)
    val startAppPendingIntent = PendingIntent.getActivity(
        context,
        0,
        startAppIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val style = NotificationCompat.InboxStyle()
        .setSummaryText("موعد شهریه")
        .setBigContentTitle("$personCount کاربرد نیاز به تمدید دارند")
    for (i in contentTitle!!){
        style.addLine(i)
    }

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.person)
        .setContentTitle(contentTitle[0])
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(NotificationCompat.CATEGORY_STATUS)
        .setStyle(
            style
        )
        .setFullScreenIntent(startAppPendingIntent, false)
        .setGroup(channelName)
        .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
        .setGroupSummary(true)
        .setAutoCancel(true)
    val notification = notificationBuilder.build()
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(channelId) == null
    ) {
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                channelId,
                channelName,
            )
        )
    }

    notificationManager.notify(notificationId, notification)
}

fun showPushNotification(
    context: Context,
    channelId:String,
    channelName:String,
    notificationId:Int,
    title:String,
    description: String
){
    val intent = Intent(context,MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        1,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificationBuilder = NotificationCompat.Builder(context , channelId)
        .setSmallIcon(R.drawable.gym)
        .setContentTitle(title)
        .setContentIntent(pendingIntent)
        .setContentText(description)
        .setAutoCancel(true)
    val notification = notificationBuilder.build()
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null){
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }
    notificationManager.notify(
        notificationId,notification
    )
}