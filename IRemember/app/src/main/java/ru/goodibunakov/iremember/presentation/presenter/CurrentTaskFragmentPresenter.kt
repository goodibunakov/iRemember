package ru.goodibunakov.iremember.presentation.presenter

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.goodibunakov.iremember.presentation.bus.QueryEvent
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.bus.UpdateEvent
import ru.goodibunakov.iremember.presentation.bus.EventRxBus
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragmentView


@InjectViewState
class CurrentTaskFragmentPresenter(
    private val bus: EventRxBus
) : TaskFragmentPresenter<CurrentTaskFragmentView>() {

    private lateinit var disposableSearch: Disposable
    private lateinit var disposable: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        searchSubscribe()
        bus.post(UpdateEvent)
    }

    private fun getTasks(query: String = "") {
        disposable = databaseRepository.findCurrentTasks()
            .map {
                it.filter { modelTask ->
                    modelTask.title.contains(query, true) || query.isEmpty()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                Log.d("debug", "getTasks Single")
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

    fun showViewToAddTask() {
        viewState.showAddingTaskDialog()
    }

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
            .subscribe { event ->
                if (event is QueryEvent) {
                    cachedQuery = event.query
                }
                getTasks(cachedQuery)
            }
    }

    fun hideFab() {
        viewState.hideFab()
    }

    fun showFab() {
        viewState.showFab()
    }

    override fun onDestroy() {
        if (::disposableSearch.isInitialized && !disposableSearch.isDisposed) disposableSearch.dispose()
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }

    fun onItemClick(task: ModelTask) {
        viewState.showEditTaskDialog(task)
    }

    fun onItemLongClick(location: Int) {
        viewState.showRemoveTaskDialog(location)
    }

    fun updateTask(modelTask: ModelTask) {
        databaseRepository.update(modelTask)
    }
}