package ru.goodibunakov.iremember.presentation.view.activity

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface MainActivityView : MvpView {
    @Skip
    fun onDestroyView()

    @OneExecution
    fun runSplash()

    fun setUI()

    @OneExecution
    fun itemSelected(id: Int)

    fun setSplashItemState(id: Int, isChecked: Boolean)

    @OneExecution
    fun showDeleteDoneTasksDialog()

    @OneExecution
    fun showDeleteAllTasksIcon(visible: Boolean)
}