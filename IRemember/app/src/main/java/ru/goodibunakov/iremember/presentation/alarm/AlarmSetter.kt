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
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED
                || intent.action != "android.intent.action.RECEIVE_BOOT_COMPLETED"
                || intent.action != Intent.ACTION_REBOOT
                || intent.action != "android.intent.action.QUICKBOOT_POWERON"
                || intent.action != "com.htc.intent.action.QUICKBOOT_POWERON")
            return


        RememberApp.alarmHelper.initAlarmManager()
        disposable = RememberApp.getBus().getEvent()
                .subscribe { it ->
                    RememberApp.databaseRepository.findCurrentTasks(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                for (element in it) {
                                    if (element.date != 0L) {
                                        RememberApp.alarmHelper.setAlarm(element)
                                    }
                                }
                                dispose()
                            }, { error ->
                                Log.d("debug", "Ошибка в AlarmSetter ${error!!.localizedMessage!!}")
                            })
                }
    }

    private fun dispose() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }
}