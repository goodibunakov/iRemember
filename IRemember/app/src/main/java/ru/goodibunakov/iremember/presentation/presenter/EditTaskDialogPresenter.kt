package ru.goodibunakov.iremember.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_ONLY
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_TIME_ONLY
import ru.goodibunakov.iremember.presentation.view.dialog.EditTaskDialogFragmentView
import java.util.*

@InjectViewState
class EditTaskDialogPresenter(
    private val repository: DatabaseRepository
) : MvpPresenter<EditTaskDialogFragmentView>() {

    lateinit var modelTask: ModelTask
    private lateinit var calendar: Calendar
    private var hasFocus = false

    private var titleIn = ""
    private var dateIn: Long = 0L
    private var priorityIn = 0
    private var timestampIn = 0L

    fun onCreateDialog(title: String, date: Long, priority: Int, timestamp: Long) {
        titleIn = title
        dateIn = date
        priorityIn = priority
        timestampIn = timestamp
        modelTask = ModelTask(title, date, priority, 0, timestamp)

        calendar = Calendar.getInstance()

        if (date == 0L) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1)
        } else {
            calendar.timeInMillis = modelTask.date
        }
    }

    fun setDateToUI() {
        viewState.setDateToUI(modelTask.date)
    }

    fun setTimeToUI() {
        viewState.setTimeToUI(modelTask.date)
    }

    fun itemSelected(position: Int) {
        modelTask.priority = position
    }

    fun nothingSelected() {
        modelTask.priority = 0
    }

    fun setEmptyDateToEditText() {
        viewState.setEmptyDateToEditText()
    }

    fun dateClicked() {
        viewState.showDatePickerDialog()
    }

    fun timeSelected(hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        viewState.setTimeToEditText(DateUtils.getDate(calendar.timeInMillis, FORMAT_TIME_ONLY))
    }

    fun timeClicked() {
        viewState.showTimePickerDialog()
    }

    fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        viewState.setDateToEditText(DateUtils.getDate(calendar.timeInMillis, FORMAT_DATE_ONLY))
    }

    fun setEmptyTimeToEditText() {
        viewState.setEmptyTimeToEditText()
    }

    fun okClicked(title: String) {
        modelTask.title = title
        modelTask.status = ModelTask.STATUS_CURRENT
    }

    fun setDateToModel() {
        modelTask.date = calendar.timeInMillis
    }

    fun dismissDialog() {
        viewState.dismissDialog()
    }

    fun cancelDialog() {
        viewState.cancelDialog()
    }

    fun titleEmpty() {
        viewState.setUIWhenTitleEmpty()
    }

    private fun titleNotEmpty() {
        viewState.setUIWhenTitleNotEmpty()
    }

    fun updateTask() {
        if (titleIn != modelTask.title || dateIn != modelTask.date || priorityIn != modelTask.priority || timestampIn != modelTask.timestamp) {
            repository.update(modelTask)
            if (modelTask.date != 0L) RememberApp.alarmHelper.setAlarm(modelTask)
        }
    }

    fun initTitle() {
        viewState.initTitle(modelTask.title)
    }

    fun setPriorityToUI() {
        viewState.setPriorityToUI(modelTask.priority)
    }

    fun onTextChanged(s: CharSequence) {
        if (s.isEmpty()) {
            titleEmpty()
        } else {
            titleNotEmpty()
        }
    }

    fun setTitleHasFocus(focus: Boolean) {
        hasFocus = focus
    }

    fun getTitleHasFocus() {
        viewState.setTitleFocus(hasFocus)
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
}