package ru.goodibunakov.iremember.domain

import io.reactivex.Observable
import ru.goodibunakov.iremember.presentation.model.ModelTask

interface DatabaseRepository {
    fun getCurrentTasks(): Observable<List<ModelTask>>
    fun getDoneTasks(): Observable<List<ModelTask>>
    fun insert(modelTask: ModelTask)
    fun update(modelTask: ModelTask)

    fun findCurrentTasks(title: String): Observable<List<ModelTask>>
    fun findDoneTasks(title: String): Observable<List<ModelTask>>
}