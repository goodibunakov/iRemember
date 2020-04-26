package ru.goodibunakov.iremember

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import ru.goodibunakov.iremember.data.DatabaseRepositoryImpl
import ru.goodibunakov.iremember.data.SharedPreferencesRepositoryImpl
import ru.goodibunakov.iremember.data.TasksDatabase
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository
import ru.goodibunakov.iremember.presentation.bus.EventRxBus
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper

class RememberApp : MultiDexApplication() {

    companion object {

        lateinit var databaseRepository: DatabaseRepository
        lateinit var sharedPreferencesRepository: SharedPreferencesRepository
        private lateinit var bus: EventRxBus
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

        fun getEventBus(): EventRxBus {
            return bus
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
        appContext = this
        bus = EventRxBus()
        databaseRepository = DatabaseRepositoryImpl(TasksDatabase.getDatabase(this).taskDao(), bus)
        sharedPreferencesRepository = SharedPreferencesRepositoryImpl(getSharedPreferences("preferences", Context.MODE_PRIVATE))
        alarmHelper = AlarmHelper.getInstance()
        alarmHelper.initAlarmManager()
    }
}