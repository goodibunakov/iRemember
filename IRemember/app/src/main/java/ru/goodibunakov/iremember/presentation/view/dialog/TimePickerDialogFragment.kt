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
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.databinding.DialogTimepickerBinding
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.HOUR
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_NEGATIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_POSITIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.TYPE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.MINUTE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_TIME
import java.util.*

@Suppress("DEPRECATION")
class TimePickerDialogFragment : DialogFragment(R.layout.dialog_timepicker) {

    private val binding by viewBinding(DialogTimepickerBinding::bind)

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("debug", "timePickerDialogFragment onCreateDialog")

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
        builder.setView(binding.root)
        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            val intent = Intent()
            binding.timePicker.clearFocus()
            val hourSet: Int
            val minuteSet: Int
            if (Build.VERSION.SDK_INT > 23) {
                hourSet = binding.timePicker.hour
                minuteSet = binding.timePicker.minute
            } else {
                hourSet = binding.timePicker.currentHour
                minuteSet = binding.timePicker.currentMinute
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
            binding.timePicker.clearFocus()
        }
        binding.timePicker.setIs24HourView(true)
        if (Build.VERSION.SDK_INT > 23) {
            binding.timePicker.hour = hour
            binding.timePicker.minute = minute
        } else {
            binding.timePicker.currentHour = hour
            binding.timePicker.currentMinute = minute
        }

        hideKeyboardInputInTimePicker(this.resources.configuration.orientation, binding.timePicker)
        isCancelable = false
        return builder.create()
    }

    //hack from StackOverflow
    private fun hideKeyboardInputInTimePicker(orientation: Int, timePicker: TimePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ((timePicker.getChildAt(0) as LinearLayout).getChildAt(4) as LinearLayout).getChildAt(
                        0
                    )?.visibility = View.GONE
                } else {
                    (((timePicker.getChildAt(0) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(
                        2
                    ) as LinearLayout).getChildAt(0)?.visibility = View.GONE
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}