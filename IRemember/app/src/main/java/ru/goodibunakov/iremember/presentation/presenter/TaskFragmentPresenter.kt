package ru.goodibunakov.iremember.presentation.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragmentView

abstract class TaskFragmentPresenter<V : TaskFragmentView> : MvpPresenter<V>() {

    private lateinit var disposableRemove: Disposable

    companion object {
        var cachedQuery = ""
    }

    protected abstract fun searchSubscribe()

    protected fun showRemoveTaskDialog(location: Int) {
        viewState.showRemoveTaskDialog(location)
    }

    fun doRemove(location: Int, timestamp: Long) {
        disposableRemove = databaseRepository.delete(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.removeItemFromAdapter(location)
                    removeAlarm(timestamp)
                    viewState.dismissRemoveDialog()
                    viewState.showSuccess(R.string.removed)
                }, {
                    viewState.showError(R.string.error_database_download)
                })
    }

    fun setAlarm(modelTask: ModelTask) {
        RememberApp.alarmHelper.setAlarm(modelTask)
    }

    fun removeAlarm(timestamp: Long) {
        RememberApp.alarmHelper.removeAlarm(timestamp)
    }

    fun cancelDialog() {
        viewState.cancelRemoveDialog()
    }

    override fun onDestroy() {
        if (::disposableRemove.isInitialized && !disposableRemove.isDisposed) disposableRemove.dispose()
        super.onDestroy()
    }
}