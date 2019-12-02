package ru.goodibunakov.iremember.presentation.view.dialog

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface AddingTaskDialogFragmentView : MvpView {
    fun setEmptyToDateEditText()
    fun setEmptyToTimeEditText()
    fun showDatePickerController()
    fun setDate(date: String)
    fun dismissDialog()
    fun cancelDialog()
    fun showTimePickerController()
    fun setTime(time: String)
    fun setUIWhenTitleEmpty()
    fun setUIWhenTitleNotEmpty()
}