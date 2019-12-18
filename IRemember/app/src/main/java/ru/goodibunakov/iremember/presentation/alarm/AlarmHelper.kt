package ru.goodibunakov.iremember.presentation.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.model.ModelTask

class AlarmHelper private constructor() {

    companion object {

        const val ALARM_KEY_TITLE = "title"
        const val ALARM_KEY_TIMESTAMP = "timestamp"
        const val ALARM_KEY_DATE = "date"
        const val ALARM_KEY_COLOR = "color"

        private var instance: AlarmHelper? = null

        fun getInstance(): AlarmHelper {
            if (instance == null) {
                instance = AlarmHelper()
            }
            return instance!!
        }
    }

    private lateinit var alarmManager: AlarmManager


    fun initAlarmManager() {
        alarmManager = RememberApp.getAppContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setAlarm(task: ModelTask) {
        val intent = Intent(RememberApp.getAppContext(), AlarmBroadcastReceiver::class.java)
        intent.putExtra(ALARM_KEY_TITLE, task.title)
        intent.putExtra(ALARM_KEY_DATE, task.date)
        intent.putExtra(ALARM_KEY_TIMESTAMP, task.timestamp)
        intent.putExtra(ALARM_KEY_COLOR, task.getPriorityColor())

        val pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(),
                task.date.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, task.date, pendingIntent)
    }

    fun removeAlarm(timestamp: Long) {
        val intent = Intent(RememberApp.getAppContext(), AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(), timestamp.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
    }
}