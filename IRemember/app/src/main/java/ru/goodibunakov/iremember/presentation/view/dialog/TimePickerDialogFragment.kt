package ru.goodibunakov.iremember.presentation.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_timepicker.view.*
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.HOUR
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_NEGATIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_POSITIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.TYPE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.MINUTE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_TIME
import java.util.*

class TimePickerDialogFragment : DialogFragment() {

    interface TimePickerDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("debug", "timePickerDialogFragment onCreateDialog")

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_timepicker, null)
        builder.setView(view)
        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            val intent = Intent()
            view.timePicker.clearFocus()
            val hourSet: Int
            val minuteSet: Int
            if (Build.VERSION.SDK_INT > 23) {
                hourSet = view.timePicker.hour
                minuteSet = view.timePicker.minute
            } else {
                hourSet = view.timePicker.currentHour
                minuteSet = view.timePicker.currentMinute
            }
            intent.putExtra(HOUR, hourSet)
            intent.putExtra(MINUTE, minuteSet)
            intent.putExtra(TYPE, KEY_POSITIVE)
            targetFragment?.onActivityResult(REQUEST_CODE_TIME, Activity.RESULT_OK, intent)
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            val intent = Intent()
            intent.putExtra(TYPE, KEY_NEGATIVE)
            targetFragment?.onActivityResult(REQUEST_CODE_TIME, Activity.RESULT_OK, intent)
            view.timePicker.clearFocus()
        }
        view.timePicker.setIs24HourView(true)
        if (Build.VERSION.SDK_INT > 23) {
            view.timePicker.hour = hour
            view.timePicker.minute = minute
        } else {
            view.timePicker.currentHour = hour
            view.timePicker.currentMinute = minute
        }

        hideKeyboardInputInTimePicker(this.resources.configuration.orientation, view.timePicker)
        return builder.create()
    }

    //hack from StackOverflow
    private fun hideKeyboardInputInTimePicker(orientation: Int, timePicker: TimePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ((timePicker.getChildAt(0) as LinearLayout).getChildAt(4) as LinearLayout).getChildAt(0)?.visibility = View.GONE
                } else {
                    (((timePicker.getChildAt(0) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(0)?.visibility = View.GONE
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}