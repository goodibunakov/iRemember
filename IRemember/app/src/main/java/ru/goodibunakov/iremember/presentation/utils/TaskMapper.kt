package ru.goodibunakov.iremember.presentation.utils

import ru.goodibunakov.iremember.data.Task
import ru.goodibunakov.iremember.presentation.model.ModelTask

object TaskMapper {

    fun mapToModelTask(task: Task): ModelTask {
        return ModelTask(task.title, task.date, task.priority, task.status, task.timestamp)
    }

    fun mapToTask(modelTask: ModelTask): Task {
        return Task(
            modelTask.title,
            modelTask.date,
            modelTask.priority,
            modelTask.status,
            modelTask.timestamp
        )
    }
}