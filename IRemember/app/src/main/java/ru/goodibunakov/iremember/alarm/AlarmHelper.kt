package ru.goodibunakov.iremember.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.model.ModelTask

class AlarmHelper private constructor() {

    companion object {
        private var instance: AlarmHelper? = null

        fun getInstance(): AlarmHelper {
            if (instance == null) {
                instance = AlarmHelper()
            }
            return instance!!
        }
    }

    private var alarmManager: AlarmManager? = null


    fun initAlarmManager() {
        alarmManager = RememberApp.getAppContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setAlarm(task: ModelTask) {
        val intent = Intent(RememberApp.getAppContext(), AlarmBroadcastReceiver::class.java)
        intent.putExtra("title", task.title)
        intent.putExtra("timestamp", task.timestamp)
        intent.putExtra("color", task.getPriorityColor())

        val pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(),
                task.timestamp.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager!!.set(AlarmManager.RTC_WAKEUP, task.date, pendingIntent)
    }

    fun removeAlarm(timestamp: Long) {
        val intent = Intent(RememberApp.getAppContext(), AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(), timestamp.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager!!.cancel(pendingIntent)
    }
}