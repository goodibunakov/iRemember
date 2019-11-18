package ru.goodibunakov.iremember.presentation.model

import ru.goodibunakov.iremember.R

class ModelSeparator(private var type: Int) : Item {

    companion object{
        const val TYPE_OVERDUE = R.string.separator_overdue
        const val TYPE_TODAY = R.string.separator_today
        const val TYPE_TOMORROW = R.string.separator_tomorrow
        const val TYPE_FUTURE = R.string.separator_future
    }

    override fun isTask(): Boolean {
        return false
    }

    fun getType(): Int {
        return type
    }
}