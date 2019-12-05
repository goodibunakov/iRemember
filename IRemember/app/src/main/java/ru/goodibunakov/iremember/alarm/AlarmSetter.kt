package ru.goodibunakov.iremember.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.iremember.RememberApp

class AlarmSetter : BroadcastReceiver() {

    lateinit var disposable: Disposable

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != "android.intent.action.BOOT_COMPLETED") return

        val alarmHelper = AlarmHelper.getInstance()
        alarmHelper.initAlarmManager()
        disposable = RememberApp.getBus().getEvent()
                .subscribe { it ->
                    RememberApp.databaseRepository.findCurrentTasks(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                for (element in it) {
                                    if (element.date != 0L) {
                                        alarmHelper.setAlarm(element)
                                    }
                                }
                                dispose()
                            }, { error ->
                                Log.d("debug", "Ошибка в AlarmSetter ${error!!.localizedMessage!!}")
                            })
                }

//        RememberApp.getBus().post("")

//        val tasks = ArrayList(dbHelper.query().getTasks(
//                DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
//                arrayOf(ModelTask.STATUS_CURRENT.toString(), ModelTask.STATUS_OVERDUE.toString()),
//                DbHelper.TASK_DATE_COLUMN)
//        )
//
//        for (task in tasks) {
//            if (task.date != 0L) {
//                alarmHelper.setAlarm(task)
//            }
//        }
    }

    private fun dispose() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }
}