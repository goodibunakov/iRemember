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
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragmentView

@InjectViewState
class DoneTaskFragmentPresenter(private val bus: RxBus) : TaskFragmentPresenter<DoneTaskFragmentView>() {

    private lateinit var disposableSearch: Disposable
    private lateinit var disp: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        init()
    }

    private fun init() {
        disp  = databaseRepository.findDoneTasks(bus.getQueryString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
//                    Log.d("rx", "DoneTaskFragmentPresenter search ${it.size}")
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

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
                .subscribe { it ->
                    databaseRepository.findDoneTasks(bus.getQueryString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d("rx", "DoneTaskFragmentPresenter search ${it.size}")
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

    fun onItemLongClick(location: Int) {
        super.showRemoveTaskDialog(location)
    }

    fun updateTask(modelTask: ModelTask) {
        Log.d("debug", "modelTask = $modelTask")
        databaseRepository.update(modelTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposableSearch.isInitialized && !disposableSearch.isDisposed) disposableSearch.dispose()
    }
}