package ru.goodibunakov.iremember

import android.content.Context
import androidx.multidex.MultiDexApplication

class RememberApp : MultiDexApplication() {

    companion object{
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
        appContext = this
    }
}