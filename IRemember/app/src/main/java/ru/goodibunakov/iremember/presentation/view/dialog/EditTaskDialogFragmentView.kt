package ru.goodibunakov.iremember.presentation.view.dialog

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface EditTaskDialogFragmentView : MvpView {
    fun setDateToUI(date: Long)
    fun setTimeToUI(time: Long)
    fun setEmptyDateToEditText()
    @StateStrategyType(SkipStrategy::class)
    fun showDatePickerDialog()
    fun setTimeToEditText(time: String)
    @StateStrategyType(SkipStrategy::class)
    fun showTimePickerDialog()
    fun setDateToEditText(date: String)
    fun setEmptyTimeToEditText()
    fun dismissDialog()
    fun cancelDialog()
    fun setUIWhenTitleEmpty()
    fun setUIWhenTitleNotEmpty()
    fun initTitle(title: String)
    @StateStrategyType(SkipStrategy::class)
    fun setTitleFocus(hasFocus: Boolean)
    fun setPriorityToUI(priority: Int)
    fun closeTimeDialogFragment()
    fun closeDateDialogFragment()
}