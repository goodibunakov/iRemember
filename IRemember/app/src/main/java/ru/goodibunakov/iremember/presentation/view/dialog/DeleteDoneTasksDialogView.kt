package ru.goodibunakov.iremember.presentation.view.dialog

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface DeleteDoneTasksDialogView : MvpView {

    fun cancelDialog()

    fun showError(textId: Int)

    fun showSuccess(textId: Int)

    fun dismissRemoveDialog()
}