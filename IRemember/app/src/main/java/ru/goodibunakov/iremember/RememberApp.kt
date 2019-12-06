package ru.goodibunakov.iremember

import android.content.Context
import androidx.multidex.MultiDexApplication
import ru.goodibunakov.iremember.data.DatabaseRepositoryImpl
import ru.goodibunakov.iremember.data.SharedPreferencesRepositoryImpl
import ru.goodibunakov.iremember.data.TasksDatabase
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository
import ru.goodibunakov.iremember.presentation.RxBus
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper

class RememberApp : MultiDexApplication() {

    companion object {

        lateinit var databaseRepository: DatabaseRepository
        lateinit var sharedPreferencesRepository: SharedPreferencesRepository
        private lateinit var bus: RxBus
        lateinit var alarmHelper: AlarmHelper

        private var activityVisible: Boolean = false
        private lateinit var appContext: Context

        fun getAppContext(): Context {
            return appContext
        }

        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }

        fun getBus(): RxBus {
            return bus
        }
    }

    override fun onCreate() {
        super.onCreate()
//        Stetho.initializeWithDefaults(this)
        appContext = this
        databaseRepository = DatabaseRepositoryImpl(TasksDatabase.getDatabase(this).taskDao())
        sharedPreferencesRepository = SharedPreferencesRepositoryImpl(getSharedPreferences("preferences", Context.MODE_PRIVATE))
        bus = RxBus()
        alarmHelper = AlarmHelper.getInstance()
        alarmHelper.initAlarmManager()
    }
}