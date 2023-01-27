package ru.goodibunakov.iremember.presentation.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.databinding.DialogDatepickerBinding
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.DAY
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_NEGATIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_POSITIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.MONTH
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_DATE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.TYPE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.YEAR
import java.util.*

class DatePickerDialogFragment : DialogFragment() {

    private var _binding: DialogDatepickerBinding? = null
    // This property is only valid between onCreateDialog and onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDatepickerBinding.inflate(LayoutInflater.from(context))
        val intent = Intent()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
        binding.datePicker.minDate = System.currentTimeMillis() - 1000
        binding.datePicker.init(year, month, day, null)
        builder.setView(binding.root)
        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            val yearSet: Int = binding.datePicker.year
            val monthSet: Int = binding.datePicker.month
            val daySet: Int = binding.datePicker.dayOfMonth
            intent.putExtra(YEAR, yearSet)
            intent.putExtra(MONTH, monthSet)
            intent.putExtra(DAY, daySet)
            intent.putExtra(TYPE, KEY_POSITIVE)
            targetFragment?.onActivityResult(REQUEST_CODE_DATE, Activity.RESULT_OK, intent)
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            intent.putExtra(TYPE, KEY_NEGATIVE)
            targetFragment?.onActivityResult(REQUEST_CODE_DATE, Activity.RESULT_OK, intent)
        }
        isCancelable = false
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}