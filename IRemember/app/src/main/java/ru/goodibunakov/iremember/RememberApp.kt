package ru.goodibunakov.iremember

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import ru.goodibunakov.iremember.data.DatabaseRepositoryImpl
import ru.goodibunakov.iremember.data.SharedPreferencesRepositoryImpl
import ru.goodibunakov.iremember.data.TasksDatabase
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository

class RememberApp : MultiDexApplication() {

    companion object {

        lateinit var databaseRepository: DatabaseRepository
        lateinit var sharedPreferencesRepository: SharedPreferencesRepository

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
    }

    override fun onCreate() {
        super.onCreate()
//        Stetho.initializeWithDefaults(this)
        appContext = this
        databaseRepository = DatabaseRepositoryImpl(TasksDatabase.getDatabase(this).taskDao())
        sharedPreferencesRepository = SharedPreferencesRepositoryImpl(getSharedPreferences("preferences", Context.MODE_PRIVATE))
    }
}