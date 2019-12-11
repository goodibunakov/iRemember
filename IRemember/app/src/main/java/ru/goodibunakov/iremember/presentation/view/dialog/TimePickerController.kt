package ru.goodibunakov.iremember.presentation.view.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.format.DateFormat
import android.util.Log
import android.view.KeyEvent
import android.widget.TimePicker
import ru.goodibunakov.iremember.R
import java.util.*

open class TimePickerController : TimePickerDialog.OnTimeSetListener {

    lateinit var dialog: TimePickerDialog

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    }

    internal fun onCreateDialog(context: Context): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(context, R.style.AppThemeDialog, this, hour, minute, DateFormat.is24HourFormat(context))
    }
}