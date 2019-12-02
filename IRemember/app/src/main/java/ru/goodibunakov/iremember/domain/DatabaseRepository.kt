package ru.goodibunakov.iremember.domain

import io.reactivex.Completable
import io.reactivex.Observable
import ru.goodibunakov.iremember.presentation.model.ModelTask

interface DatabaseRepository {
    fun getCurrentTasks(): Observable<List<ModelTask>>
    fun getDoneTasks(): Observable<List<ModelTask>>
    fun insert(modelTask: ModelTask)
//    fun insert(modelTask: ModelTask): Completable
    fun update(modelTask: ModelTask)
}