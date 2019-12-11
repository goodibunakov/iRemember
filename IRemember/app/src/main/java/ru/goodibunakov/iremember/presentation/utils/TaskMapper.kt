package ru.goodibunakov.iremember.presentation.utils

import ru.goodibunakov.iremember.data.Task
import ru.goodibunakov.iremember.presentation.model.ModelTask

object TaskMapper {

    fun mapToModelTask(item: Task): ModelTask {
        return ModelTask(item.title, item.date, item.priority, item.status, item.timestamp)
    }

    fun mapToTask(modelTask: ModelTask): Task {
        return Task(modelTask.title, modelTask.date, modelTask.priority, modelTask.status, modelTask.timestamp)
    }
}