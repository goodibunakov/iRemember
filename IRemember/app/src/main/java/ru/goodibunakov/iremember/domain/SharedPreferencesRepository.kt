package ru.goodibunakov.iremember.domain

interface SharedPreferencesRepository {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean
}