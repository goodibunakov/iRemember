package ru.goodibunakov.iremember.presentation.model

import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_FULL
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_TIME_ONLY
import java.util.*

class ModelTask : Item {

    companion object {
        private const val PRIORITY_LOW = 0
        private const val PRIORITY_NORMAL = 1
        private const val PRIORITY_HIGH = 2

        const val STATUS_OVERDUE = 0
        const val STATUS_CURRENT = 1
        const val STATUS_DONE = 2
    }

    var title: String = ""
    var date: Long = 0
    var priority: Int = 0
    var status: Int = 0
    val timestamp: Long
    var dateStatus: Int = 0

    constructor() {
        this.status = -1
        this.timestamp = Date().time
    }

    constructor(title: String, date: Long, priority: Int, status: Int, timestamp: Long) {
        this.title = title
        this.date = date
        this.priority = priority
        this.status = status
        this.timestamp = timestamp
    }

    fun getPriorityColor(): Int {
        when (priority) {
            PRIORITY_HIGH -> return if (status == STATUS_CURRENT || status == STATUS_OVERDUE) {
                R.color.priority_high
            } else {
                R.color.priority_high_selected
            }
            PRIORITY_NORMAL -> return if (status == STATUS_CURRENT || status == STATUS_OVERDUE) {
                R.color.priority_normal
            } else {
                R.color.priority_normal_selected
            }
            PRIORITY_LOW -> return if (status == STATUS_CURRENT || status == STATUS_OVERDUE) {
                R.color.priority_low
            } else {
                R.color.priority_low_selected
            }
            else -> return 0
        }
    }

    override fun isTask(): Boolean {
        return true
    }

    @Suppress("unused")
    fun toString(modelTask: ModelTask): String {
        return "title = " + modelTask.title + "  date = " + modelTask.date +
                "   date = " + DateUtils.getDate(modelTask.date, FORMAT_DATE_FULL) + " " + DateUtils.getDate(modelTask.date, FORMAT_TIME_ONLY) +
                "   priority = " + modelTask.priority +
                "   status = " + modelTask.status +
                "   priorityColor + " + modelTask.getPriorityColor()
    }
}