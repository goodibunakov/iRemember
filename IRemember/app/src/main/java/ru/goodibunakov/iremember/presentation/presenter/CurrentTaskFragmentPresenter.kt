package ru.goodibunakov.iremember.presentation.presenter

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.RxBus
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragmentView


@InjectViewState
class CurrentTaskFragmentPresenter(private val bus: RxBus) : TaskFragmentPresenter<CurrentTaskFragmentView>() {

    private lateinit var disposableSearch: Disposable
    private lateinit var disp: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        init()
    }

    private fun init() {
        disp = databaseRepository.findCurrentTasks(bus.getQueryString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.checkAdapter()
                    viewState.removeAllItemsFromAdapter()
                    for (element in it) {
                        viewState.addTask(element)
                    }
                    disposeThis()
                }, { error ->
                    disposeThis()
                    Log.d("debug", error!!.localizedMessage!!)
                    viewState.showError(R.string.error_database_download)
                })

    }

    private fun disposeThis() {
        if (::disp.isInitialized && !disp.isDisposed) disp.dispose()
        searchSubscribe()
    }

    fun showViewToAddTask() {
        viewState.showAddingTaskDialog()
    }

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
                .subscribe { it ->
                    databaseRepository.findCurrentTasks(bus.getQueryString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d("rx", "CurrentTaskFragmentPresenter search ${it.size}")
                                viewState.checkAdapter()
                                viewState.removeAllItemsFromAdapter()
                                for (element in it) {
                                    viewState.addTask(element)
                                }
                            }, { error ->
                                Log.d("debug", error!!.localizedMessage!!)
                                viewState.showError(R.string.error_database_download)
                            })
                }
    }

    fun hideFab() {
        viewState.hideFab()
    }

    fun showFab() {
        viewState.showFab()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposableSearch.isInitialized && !disposableSearch.isDisposed) disposableSearch.dispose()
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