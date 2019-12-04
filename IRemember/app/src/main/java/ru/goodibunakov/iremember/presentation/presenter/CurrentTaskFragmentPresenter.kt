package ru.goodibunakov.iremember.presentation.presenter

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.RxBus
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragmentView

@InjectViewState
class CurrentTaskFragmentPresenter(private val bus: RxBus) : TaskFragmentPresenter<CurrentTaskFragmentView>() {

    private var disposable: Disposable? = null
    private var disposableSearch: Disposable? = null

    fun showViewToAddTask() {
        viewState.showAddingTaskDialog()
    }

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
//                .filter { s -> s.isNotEmpty()}
                .subscribe { it ->
                    databaseRepository.findCurrentTasks(it)
//                            .flatMap { Observable.fromIterable(it) }
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

    @SuppressLint("CheckResult")
    override fun getTasksFromDb() {
        disposable = databaseRepository.getCurrentTasks()
//                .flatMap { Observable.fromIterable(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.checkAdapter()
                    viewState.removeAllItemsFromAdapter()
                    Log.d("debug", "it = $it")
                    for (element in it) {
                        viewState.addTask(element)
                    }
                }, { error ->
                    Log.d("debug", error!!.localizedMessage!!)
                    viewState.showError(R.string.error_database_download)
                })
    }

    fun hideFab() {
        viewState.hideFab()
    }

    fun showFab() {
        viewState.showFab()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null && !disposable!!.isDisposed) disposable?.dispose()
        if (disposableSearch != null && !disposableSearch!!.isDisposed) disposableSearch?.dispose()
    }
}