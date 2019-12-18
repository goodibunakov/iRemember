package ru.goodibunakov.iremember.presentation.view.dialog

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface AddingTaskDialogFragmentView : MvpView {
    fun setEmptyToDateEditText()
    fun setEmptyToTimeEditText()
    @StateStrategyType(SkipStrategy::class)
    fun showDatePickerDialog()
    fun setDate(date: String)
    fun dismissDialog()
    fun cancelDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showTimePickerDialog()
    fun setTime(time: String)
    fun setUIWhenTitleEmpty()
    fun setUIWhenTitleNotEmpty(s: String)
    fun initPositiveButton()
    fun closeTimeDialogFragment()
    @StateStrategyType(SkipStrategy::class)
    fun setTitleFocus(hasFocus: Boolean)
    fun closeDateDialogFragment()
}