package ru.goodibunakov.iremember.data

import android.util.Log
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.iremember.presentation.bus.UpdateEvent
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.bus.EventRxBus
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.utils.TaskMapper

class DatabaseRepositoryImpl(
    private val taskDao: TaskDao,
    private val bus: EventRxBus
) : DatabaseRepository {

    override fun insert(modelTask: ModelTask) {
        Completable
            .fromAction { taskDao.insert(TaskMapper.mapToTask(modelTask)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Log.d("debug", "Task saved to DB")
                    bus.post(UpdateEvent)
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    Log.d("debug", "Error!!! task NOT saved to DB")
                }
            })
    }

    override fun update(modelTask: ModelTask) {
        Completable
            .fromAction { taskDao.update(TaskMapper.mapToTask(modelTask)) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Log.d("debug", "Task updated to DB")
                    bus.post(UpdateEvent)
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    Log.d("debug", "Error!!! task NOT updated to DB")
                }
            })
    }

    override fun findCurrentTasks(): Observable<List<ModelTask>> {
        return taskDao.findCurrentTasks()
            .flatMap { list ->
                Observable.fromIterable(list)
                    .map { item -> TaskMapper.mapToModelTask(item) }
                    .toList()
                    .toObservable()
            }
    }

    override fun findDoneTasks(): Observable<List<ModelTask>> {
        return taskDao.findDoneTasks()
            .flatMap { list ->
                Observable.fromIterable(list)
                    .map { item -> TaskMapper.mapToModelTask(item) }
                    .toList()
                    .toObservable()
            }
    }

    override fun delete(timestamp: Long): Completable {
        return taskDao.delete(timestamp)
    }

    override fun deleteAllDoneTasks(): Completable {
        return taskDao.deleteAllDoneTasks()
    }
}