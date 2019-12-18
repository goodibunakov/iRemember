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
import ru.goodibunakov.iremember.presentation.bus.RxBus
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragmentView

@InjectViewState
class DoneTaskFragmentPresenter(private val bus: RxBus) : TaskFragmentPresenter<DoneTaskFragmentView>() {

    private lateinit var disposableSearch: Disposable
    private lateinit var disposable: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        searchSubscribe()
        bus.post(UpdateEvent())
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

    private fun disposeThis(){
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
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


    fun onItemLongClick(location: Int) {
        super.showRemoveTaskDialog(location)
    }

    fun updateTask(modelTask: ModelTask) {
        databaseRepository.update(modelTask)
    }

    override fun onDestroy() {
        if (::disposableSearch.isInitialized && !disposableSearch.isDisposed) disposableSearch.dispose()
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }
}