package ru.goodibunakov.iremember.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import java.util.*

class AlarmSetter : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != "android.intent.action.BOOT_COMPLETED") return

        val dbHelper = DbHelper(context)

        val alarmHelper = AlarmHelper.getInstance()
        alarmHelper.initAlarmManager()
        val tasks = ArrayList(dbHelper.query().getTasks(DbHelper.SELECTION_STATUS + " OR "
                + DbHelper.SELECTION_STATUS,
                arrayOf(ModelTask.STATUS_CURRENT.toString(), ModelTask.STATUS_OVERDUE.toString()),
                DbHelper.TASK_DATE_COLUMN))

        for (task in tasks) {
            if (task.date != 0L) {
                alarmHelper.setAlarm(task)
            }
        }
    }
}