package ru.goodibunakov.iremember.utils

import ru.goodibunakov.iremember.data.Task
import ru.goodibunakov.iremember.presentation.model.ModelTask
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun getDate(date: Long): String {
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            return dateFormat.format(date)
        }

        fun getTime(time: Long): String {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return timeFormat.format(time)
        }

        fun getFullDate(date: Long): String {
            val fullDateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
            return fullDateFormat.format(date)
        }

        fun mapToModelTask(item: Task): ModelTask {
            return ModelTask(item.title, item.date, item.priority, item.status, item.timestamp)
        }

        fun mapToTask(modelTask: ModelTask) : Task{
            return Task(modelTask.title, modelTask.date, modelTask.priority, modelTask.status, modelTask.timestamp)
        }
    }
}