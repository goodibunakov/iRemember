package ru.goodibunakov.iremember.presentation.presenter

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp.Companion.databaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragmentView

@InjectViewState
class CurrentTaskFragmentPresenter : TaskFragmentPresenter<CurrentTaskFragmentView>() {

    private var disposable: Disposable? = null

    fun showViewToAddTask() {
        viewState.showAddingTaskDialog()
    }

    @SuppressLint("CheckResult")
    override fun getTasksFromDb() {
        disposable = databaseRepository.getCurrentTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.checkAdapter()
                    viewState.removeAllItemsFromAdapter()
                    sortTasks(it)
                }, { error ->
                    Log.d("debug", error!!.localizedMessage!!)
                    viewState.showError(R.string.error_database_download)
                })
    }

    private fun sortTasks(tasks: List<ModelTask>) {
        for (i in tasks.indices) {
            viewState.addTask(tasks[i], false)
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
        disposable?.dispose()
    }
}