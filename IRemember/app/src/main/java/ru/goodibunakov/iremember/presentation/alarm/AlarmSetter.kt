package ru.goodibunakov.iremember.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.iremember.RememberApp

class AlarmSetter : BroadcastReceiver() {

    private lateinit var disposable: Disposable

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("debug", "AlarmSetter  onReceive")
        Log.d("debug", "AlarmSetter  intent?.action = ${intent?.action}")

        RememberApp.alarmHelper.initAlarmManager()
        disposable = RememberApp.databaseRepository.findCurrentTasks()
            .map {
                it.filter { modelTask -> modelTask.date != 0L }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                Log.d("debug", "AlarmSetter  List = ${list.size}")
                for (element in list) {
                    RememberApp.alarmHelper.setAlarm(element)
                    Log.d("debug", "AlarmSetter   |    $element")
                }
                dispose()
            }, { error ->
                Log.d("debug", "Ошибка в AlarmSetter ${error!!.localizedMessage!!}")
            })

    }

    private fun dispose() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }
}