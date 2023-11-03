package ru.goodibunakov.iremember.presentation.presenter

import moxy.MvpPresenter
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_ONLY
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_TIME_ONLY
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragmentView
import java.util.Calendar

class AddingTaskDialogPresenter(
    private val repository: DatabaseRepository
) : MvpPresenter<AddingTaskDialogFragmentView>() {

    lateinit var modelTask: ModelTask
    private val calendar: Calendar = Calendar.getInstance()
    private var hasFocus = false

    fun dialogCreated() {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1)
        modelTask = ModelTask()
    }

    fun itemSelected(position: Int) {
        modelTask.priority = position
    }

    fun nothingSelected() {
        modelTask.priority = 0
    }

    fun dateIsEmpty() {
        viewState.setEmptyToDateEditText()
    }

    fun timeIsEmpty() {
        viewState.setEmptyToTimeEditText()
    }

    fun editTextDateClicked() {
        viewState.showDatePickerDialog()
    }

    fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date = DateUtils.getDate(calendar.timeInMillis, FORMAT_DATE_ONLY)
        viewState.setDate(date)
    }

    fun dismissDialog() {
        viewState.dismissDialog()
    }

    fun cancelDialog() {
        viewState.cancelDialog()
    }

    fun okClicked(title: String) {
        modelTask.title = title
        modelTask.status = ModelTask.STATUS_CURRENT
    }

    fun setDateToModel() {
        modelTask.date = calendar.timeInMillis
    }

    fun saveTask() {
        repository.insert(modelTask)
        if (modelTask.date != 0L) RememberApp.alarmHelper.setAlarm(modelTask)
    }

    fun editTextTimeClicked() {
        viewState.showTimePickerDialog()
    }

    fun timeSelected(hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        val time = DateUtils.getDate(calendar.timeInMillis, FORMAT_TIME_ONLY)
        viewState.setTime(time)
    }

    fun titleEmpty() {
        viewState.setUIWhenTitleEmpty()
    }

    private fun titleNotEmpty(s: String) {
        viewState.setUIWhenTitleNotEmpty(s)
    }

    fun initPositiveButton() {
        viewState.initPositiveButton()
    }

    fun onTextChanged(s: CharSequence) {
        if (s.isBlank()) {
            titleEmpty()
        } else {
            titleNotEmpty(s.toString())
        }
    }

    fun positiveTimeClicked() {
        viewState.closeTimeDialogFragment()
    }

    fun negativeTimeClicked() {
        viewState.closeTimeDialogFragment()
    }

    fun positiveDateClicked() {
        viewState.closeDateDialogFragment()
    }

    fun negativeDateClicked() {
        viewState.closeDateDialogFragment()
    }

    fun setTitleHasFocus(focus: Boolean) {
        hasFocus = focus
    }

    fun getTitleHasFocus() {
        viewState.setTitleFocus(hasFocus)
    }
}