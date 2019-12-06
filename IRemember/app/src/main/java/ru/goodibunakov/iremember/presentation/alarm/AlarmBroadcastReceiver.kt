package ru.goodibunakov.iremember.presentation.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper.Companion.ALARM_KEY_COLOR
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper.Companion.ALARM_KEY_TIMESTAMP
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper.Companion.ALARM_KEY_TITLE
import ru.goodibunakov.iremember.presentation.view.activity.MainActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("debug", "AlarmBroadcastReceiver : BroadcastReceiver()")
        Log.d("debug", "intent = $intent")
        val title = intent.getStringExtra(ALARM_KEY_TITLE)
        val timestamp = intent.getLongExtra(ALARM_KEY_TIMESTAMP, 0)
        val color = intent.getIntExtra(ALARM_KEY_COLOR, 0)

        var resultIntent = Intent(context, MainActivity::class.java)

        if (RememberApp.isActivityVisible()) {
            resultIntent = intent
        }

        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, timestamp.toInt(),
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(title)
        builder.color = ContextCompat.getColor(context, color)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setAutoCancel(true)
        builder.setLights(color, 5000, 5000)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(context.applicationContext.getString(R.string.default_notification_channel_id),
                    context.applicationContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            channel.description = title
            channel.enableLights(true)
            channel.enableVibration(true)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(timestamp.toInt(), notification) //Show notification for Oreo
            }
        } else {
            val manager = NotificationManagerCompat.from(context.applicationContext)
            manager.notify(timestamp.toInt(), notification) //Show notification for other version
        }
    }
}