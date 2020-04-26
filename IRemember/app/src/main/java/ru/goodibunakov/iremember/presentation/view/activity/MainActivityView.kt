package ru.goodibunakov.iremember.presentation.view.activity

import moxy.MvpView
import moxy.viewstate.strategy.*

@StateStrategyType(value = AddToEndStrategy::class)
interface MainActivityView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun onDestroyView()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun runSplash()

    fun setUI()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun itemSelected(id: Int)

    fun setSplashItemState(id: Int, isChecked: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteDoneTasksDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteAllTasksIcon(visible: Boolean)
}