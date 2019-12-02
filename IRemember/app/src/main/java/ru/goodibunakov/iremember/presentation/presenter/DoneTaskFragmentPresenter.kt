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
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragmentView

@InjectViewState
class DoneTaskFragmentPresenter : TaskFragmentPresenter<DoneTaskFragmentView>() {

    private var disposable: Disposable? = null

    @SuppressLint("CheckResult")
    override fun getTasksFromDb() {
        viewState.checkAdapter()
        viewState.removeAllItemsFromAdapter()

//        disposable = databaseRepository.getDoneTasks()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    sortTasks(it)
//                }, { error ->
//                    Log.e("debug", error.stackTrace.toString())
//                    viewState.showError(R.string.error_database_dowload)
//                })
    }

    private fun sortTasks(tasks: List<ModelTask>) {
        for (i in tasks.indices) {
            viewState.addTask(tasks[i], false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}