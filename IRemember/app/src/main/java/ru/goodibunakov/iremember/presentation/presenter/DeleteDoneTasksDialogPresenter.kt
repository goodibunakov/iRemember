package ru.goodibunakov.iremember.presentation.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.bus.DeleteAllDoneTasksEvent
import ru.goodibunakov.iremember.presentation.bus.EventRxBus
import ru.goodibunakov.iremember.presentation.view.dialog.DeleteDoneTasksDialogView

@InjectViewState
class DeleteDoneTasksDialogPresenter(
    private val repository: DatabaseRepository,
    private val bus: EventRxBus
) : MvpPresenter<DeleteDoneTasksDialogView>() {

    private lateinit var disposable: Disposable

    fun cancelDialog() {
        viewState.cancelDialog()
    }

    fun okClicked() {
        disposable = repository.deleteAllDoneTasks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.showSuccess(R.string.removed_all_done_tasks)
                bus.post(DeleteAllDoneTasksEvent)
            }, {
                viewState.showError(R.string.error_delete_all_done_tasks)
            })
    }

    override fun onDestroy() {
        if (::disposable.isInitialized && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }
}