package ru.goodibunakov.iremember.domain

import io.reactivex.Completable
import io.reactivex.Observable
import ru.goodibunakov.iremember.presentation.model.ModelTask

interface DatabaseRepository {
    fun insert(modelTask: ModelTask)
    fun update(modelTask: ModelTask)
    fun delete(timestamp: Long): Completable
    fun deleteAllDoneTasks(): Completable

    fun findCurrentTasks(): Observable<List<ModelTask>>
    fun findDoneTasks(): Observable<List<ModelTask>>
}