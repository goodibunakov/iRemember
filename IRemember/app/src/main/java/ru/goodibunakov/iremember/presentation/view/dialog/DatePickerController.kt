package ru.goodibunakov.iremember.presentation.view.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import ru.goodibunakov.iremember.R
import java.util.*

open class DatePickerController : DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    internal fun onCreateDialog(context: Context): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, R.style.AppThemeDialog, this, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        return datePickerDialog
    }
}