package ru.goodibunakov.iremember.presentation.view.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.TimePicker
import ru.goodibunakov.iremember.R
import java.util.*

open class TimePickerController : TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    }

    internal fun onCreateDialog(context: Context): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(context, R.style.AppThemeDialog, this, hour, minute, DateFormat.is24HourFormat(context))
    }
}