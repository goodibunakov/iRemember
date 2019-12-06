package ru.goodibunakov.iremember.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragmentView
import ru.goodibunakov.iremember.presentation.utils.Utils
import java.util.*

@InjectViewState
class AddingTaskDialogPresenter(private val repository: DatabaseRepository) : MvpPresenter<AddingTaskDialogFragmentView>() {

    lateinit var modelTask: ModelTask
    private val calendar: Calendar = Calendar.getInstance()

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
        viewState.showDatePickerController()
    }

    fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date = Utils.getDate(calendar.timeInMillis)
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
    }

    fun editTextTimeClicked() {
        viewState.showTimePickerController()
    }

    fun timeSelected(hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        val time = Utils.getTime(calendar.timeInMillis)
        viewState.setTime(time)
    }

    fun titleEmpty() {
        viewState.setUIWhenTitleEmpty()
    }

    fun titleNotEmpty() {
        viewState.setUIWhenTitleNotEmpty()
    }

    fun setAlarm() {
        RememberApp.alarmHelper.setAlarm(modelTask)
    }
}