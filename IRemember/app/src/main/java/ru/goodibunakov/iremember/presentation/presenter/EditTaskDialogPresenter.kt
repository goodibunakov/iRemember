package ru.goodibunakov.iremember.presentation.presenter

import android.util.Log
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.dialog.EditTaskDialogFragmentView
import ru.goodibunakov.iremember.utils.Utils
import java.util.*

@InjectViewState
class EditTaskDialogPresenter(private val repository: DatabaseRepository) : MvpPresenter<EditTaskDialogFragmentView>() {

    lateinit var modelTask: ModelTask
    lateinit var calendar: Calendar

    var titleIn = ""
    var dateIn = 0L
    var priorityIn = 0
    var timestampIn = 0L

    fun onCreateDialog(title: String, date: Long, priority: Int, timestamp: Long) {
        titleIn = title
        dateIn = date
        priorityIn = priority
        timestampIn = timestamp

        modelTask = ModelTask(title, date, priority, 0, timestamp)
        Log.d("debug", "modelTask = " + modelTask.toString(modelTask))
        calendar = Calendar.getInstance()
        if (date == 0L) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1)
        } else {
            calendar.timeInMillis = modelTask.date
        }
        Log.d("debug", "calendar = ${calendar.timeInMillis}")
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
        viewState.showDateController()
    }

    fun timeSelected(hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        viewState.setTimeToEditText(Utils.getTime(calendar.timeInMillis))
    }

    fun timeClicked() {
        viewState.showTimeController()
    }

    fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        viewState.setDateToEditText(Utils.getDate(calendar.timeInMillis))
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

    fun titleNotEmpty() {
        viewState.setUIWhenTitleNotEmpty()
    }

    fun updateTask() {
        Log.d("debug", "modelTask = " + modelTask.toString(modelTask))
        if (titleIn != modelTask.title || dateIn != modelTask.date || priorityIn != modelTask.priority || timestampIn != modelTask.timestamp)
            repository.update(modelTask)
    }

    fun initTitle() {
        viewState.initTitle(modelTask.title)
    }

    fun setPriorityToUI() {
        viewState.setPriorityToUI(modelTask.priority)
    }
}