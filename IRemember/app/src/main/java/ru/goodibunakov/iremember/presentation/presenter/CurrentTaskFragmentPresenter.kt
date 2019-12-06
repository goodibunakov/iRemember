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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        bus.post("")
    }

    fun showViewToAddTask() {
        viewState.showAddingTaskDialog()
    }

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
                .subscribe { it ->
                    databaseRepository.findCurrentTasks(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
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