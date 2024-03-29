package ru.goodibunakov.iremember.presentation.presenter

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.bus.DeleteAllDoneTasksEvent
import ru.goodibunakov.iremember.presentation.bus.EventRxBus
import ru.goodibunakov.iremember.presentation.bus.QueryEvent
import ru.goodibunakov.iremember.presentation.bus.UpdateEvent
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragmentView

class DoneTaskFragmentPresenter(
    private val bus: EventRxBus
) : TaskFragmentPresenter<DoneTaskFragmentView>() {

    private lateinit var disposable: Disposable
    private val disposables = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        searchSubscribe()
        deleteAllDoneTasksSubscribe()
        bus.post(UpdateEvent)
    }

    private fun deleteAllDoneTasksSubscribe() {
        val disposableDeleteAllDoneTasks = bus.getEvent()
            .subscribe { event ->
                if (event is DeleteAllDoneTasksEvent) {
                    viewState.removeAllItemsFromAdapter()
                }
            }
        disposables.add(disposableDeleteAllDoneTasks)
    }

    private fun getTasks(query: String = "") {
        disposable = databaseRepository.findDoneTasks()
            .map {
                it.filter { modelTask ->
                    modelTask.title.contains(query, true) || query.isEmpty()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                if (list.isEmpty()) {
                    viewState.showEmptyListText()
                } else {
                    viewState.hideEmptyListText()
                }

                viewState.checkAdapter()
                for (element in list) {
                    viewState.addTask(element)
                }
                disposeThis()
            }, { error ->
                Log.d("debug", error!!.localizedMessage!!)
                viewState.showError(R.string.error_database_download)
                disposeThis()
            })
    }

    private fun disposeThis() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
    }

    override fun searchSubscribe() {
        val disposableSearch = bus.getEvent()
            .subscribe { event ->
                if (event is QueryEvent) {
                    cachedQuery = event.query
                }
                getTasks(cachedQuery)
            }
        disposables.add(disposableSearch)
    }


    fun onItemLongClick(location: Int) {
        super.showRemoveTaskDialog(location)
    }

    fun updateTask(modelTask: ModelTask) {
        databaseRepository.update(modelTask)
    }

    override fun onDestroy() {
        if (!disposables.isDisposed) disposables.dispose()
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }
}