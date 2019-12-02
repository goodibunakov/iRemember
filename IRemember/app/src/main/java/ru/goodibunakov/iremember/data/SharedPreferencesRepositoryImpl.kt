package ru.goodibunakov.iremember.data

import android.content.SharedPreferences
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository

class SharedPreferencesRepositoryImpl(private val preferences: SharedPreferences) : SharedPreferencesRepository {

    companion object {
        const val SPLASH_IS_INVISIBLE = "splash_is_invisible"
    }

    override fun putBoolean(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }
}