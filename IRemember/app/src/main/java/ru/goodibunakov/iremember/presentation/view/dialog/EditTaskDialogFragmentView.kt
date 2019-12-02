package ru.goodibunakov.iremember.presentation.view.dialog

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface EditTaskDialogFragmentView : MvpView {
    fun setDateToUI(date: Long)
    fun setTimeToUI(time: Long)
    fun setEmptyDateToEditText()
    fun showDateController()
    fun setTimeToEditText(time: String)
    fun showTimeController()
    fun setDateToEditText(date: String)
    fun setEmptyTimeToEditText()
    fun dismissDialog()
    fun cancelDialog()
    fun setUIWhenTitleEmpty()
    fun setUIWhenTitleNotEmpty()
    fun initTitle(title: String)
    fun setPriorityToUI(priority: Int)
}