package ru.goodibunakov.iremember.presentation.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragmentView

abstract class TaskFragmentPresenter<V : TaskFragmentView> : MvpPresenter<V>() {

    lateinit var disposable: Disposable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        searchSubscribe()
    }

    protected abstract fun searchSubscribe()

//    protected abstract fun getTasksFromDb()

    protected fun showRemoveTaskDialog(location: Int) {
        viewState.showRemoveTaskDialog(location)
    }

    fun doRemove(location: Int, timestamp: Long) {
        disposable = databaseRepository.delete(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.removeItemFromAdapter(location)
                    viewState.removeAlarm()
                    viewState.dismissRemoveDialog()
                }, {
                    viewState.showError(R.string.error_database_download)
                })
    }

    fun cancelDialog() {
        viewState.cancelRemoveDialog()
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }
}