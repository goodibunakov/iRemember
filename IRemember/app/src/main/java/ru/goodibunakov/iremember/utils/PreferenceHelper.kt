package ru.goodibunakov.iremember.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {

    private var preferences: SharedPreferences? = null

    companion object {

        const val SPLASH_IS_INVISIBLE = "splash_is_invisible"

        private var instance: PreferenceHelper? = null

        fun getInstance(): PreferenceHelper {
            if (instance == null) {
                instance = PreferenceHelper()
            }
            return instance!!
        }
    }

    fun init(context: Context) {
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = preferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return preferences!!.getBoolean(key, false)
    }
}