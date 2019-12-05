package ru.goodibunakov.iremember.presentation.presenter

import android.annotation.SuppressLint
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

//    private var disposable: Disposable? = null
    private lateinit var disposableSearch: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        bus.post("")
    }

//    @SuppressLint("CheckResult")
//    override fun getTasksFromDb() {
//        viewState.checkAdapter()
//        viewState.removeAllItemsFromAdapter()
//
////        disposable = databaseRepository.getDoneTasks()
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe({
////                    sortTasks(it)
////                }, { error ->
////                    Log.e("debug", error.stackTrace.toString())
////                    viewState.showError(R.string.error_database_dowload)
////                })
//    }

    override fun searchSubscribe() {
        disposableSearch = bus.getEvent()
                .subscribe { it ->
                    databaseRepository.findDoneTasks(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                viewState.checkAdapter()
                                viewState.removeAllItemsFromAdapter()
                                for (element in it) {
                                    viewState.addTask(element)
                                    Log.d("debug", "donetaskFragmentPresenter $element")
                                }
                            }, { error ->
                                Log.d("debug", error!!.localizedMessage!!)
                                viewState.showError(R.string.error_database_download)
                            })
                }
    }

    fun onItemLongClick(location: Int){
        super.showRemoveTaskDialog(location)
    }

    fun updateTask(modelTask: ModelTask) {
        databaseRepository.update(modelTask)
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (disposable != null && !disposable!!.isDisposed) disposable?.dispose()
        if (::disposableSearch.isInitialized && !disposableSearch.isDisposed) disposableSearch.dispose()
    }
}