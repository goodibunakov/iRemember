package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.databinding.DialogTaskBinding
import ru.goodibunakov.iremember.presentation.presenter.AddingTaskDialogPresenter


class AddingTaskDialogFragment : MvpAppCompatDialogFragment(), AddingTaskDialogFragmentView {

    private var _binding: DialogTaskBinding? = null
    // This property is only valid between onCreateDialog and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var positive: Button
    private lateinit var timePickerDialogFragment: TimePickerDialogFragment
    private lateinit var datePickerDialogFragment: DatePickerDialogFragment

    companion object {
        const val REQUEST_CODE_TIME = 100
        const val REQUEST_CODE_DATE = 101

        const val TYPE = "type"
        const val KEY_POSITIVE = "positive"
        const val KEY_NEGATIVE = "negative"
        const val YEAR = "year"
        const val MONTH = "month"
        const val DAY = "day"
        const val HOUR = "hourOfDaySet"
        const val MINUTE = "minuteSet"
    }

    @InjectPresenter
    lateinit var addingTaskDialogPresenter: AddingTaskDialogPresenter

    @ProvidePresenter
    fun providePresenter(): AddingTaskDialogPresenter {
        return AddingTaskDialogPresenter(RememberApp.databaseRepository)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTaskBinding.inflate(LayoutInflater.from(context))
        Log.d("debug", "AddingTaskDialogFragment onCreateDialog")
        val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
        isCancelable = false

        builder.setTitle(R.string.dialog_title)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setView(binding.root)

        addingTaskDialogPresenter.dialogCreated()

        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            addingTaskDialogPresenter.okClicked(binding.etTitle.text.toString().trim())
            if (binding.etDate.text.isNotEmpty() || binding.etTime.text.isNotEmpty()) {
                addingTaskDialogPresenter.setDateToModel()
            }
            addingTaskDialogPresenter.saveTask()
            addingTaskDialogPresenter.dismissDialog()
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            addingTaskDialogPresenter.cancelDialog()
        }


        val priorityAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            requireActivity().resources.getStringArray(R.array.priority_array)
        )
        binding.spinnerPriority.adapter = priorityAdapter
        binding.spinnerPriority.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    addingTaskDialogPresenter.itemSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    addingTaskDialogPresenter.nothingSelected()
                }
            }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            addingTaskDialogPresenter.initPositiveButton()

            if (binding.etTitle.text.isBlank()) {
                addingTaskDialogPresenter.titleEmpty()
            }

            binding.etTitle.addTextChangedListener(object : MyTextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    addingTaskDialogPresenter.onTextChanged(s)
                }
            })

            binding.etTitle.isFocusableInTouchMode = true
            addingTaskDialogPresenter.getTitleHasFocus()
            binding.etTitle.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        addingTaskDialogPresenter.setTitleHasFocus(hasFocus)
                    }
                }

            binding.etDate.setOnClickListener {
                clearTitleFocus()
                if (binding.etDate.length() == 0) {
                    addingTaskDialogPresenter.dateIsEmpty()
                }
                addingTaskDialogPresenter.editTextDateClicked()
            }

            binding.etTime.setOnClickListener {
                clearTitleFocus()
                if (binding.etTime.length() == 0) {
                    addingTaskDialogPresenter.timeIsEmpty()
                }
                addingTaskDialogPresenter.editTextTimeClicked()
            }
        }
        return alertDialog
    }

    private fun clearTitleFocus() {
        if (binding.etTitle.hasFocus()) {
            binding.etTitle.clearFocus()
            hideSoftKeyboard()
            addingTaskDialogPresenter.setTitleHasFocus(false)
        }
    }

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun cancelDialog() {
        dialog?.cancel()
    }

    override fun setEmptyToDateEditText() {
        binding.etDate.setText("")
    }

    override fun setEmptyToTimeEditText() {
        binding.etTime.setText("")
    }

    override fun showDatePickerDialog() {
        datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.setTargetFragment(this@AddingTaskDialogFragment, REQUEST_CODE_DATE)
        datePickerDialogFragment.show(
            requireActivity().supportFragmentManager,
            "DatePickerDialogFragment"
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TIME) {
                when (data?.extras?.getString(TYPE)) {
                    KEY_POSITIVE -> {
                        getTimeFromIntent(data)
                        addingTaskDialogPresenter.positiveTimeClicked()
                    }
                    KEY_NEGATIVE -> addingTaskDialogPresenter.negativeTimeClicked()
                }
            } else if (requestCode == REQUEST_CODE_DATE) {
                when (data?.extras?.getString(TYPE)) {
                    KEY_POSITIVE -> {
                        getDateFromIntent(data)
                        addingTaskDialogPresenter.positiveDateClicked()
                    }
                    KEY_NEGATIVE -> addingTaskDialogPresenter.negativeDateClicked()
                }
            }
        }
    }

    private fun getTimeFromIntent(data: Intent?) {
        val hourOfDay = data?.extras?.getInt(HOUR)
        val minute = data?.extras?.getInt(MINUTE)
        if (hourOfDay != null && minute != null) {
            addingTaskDialogPresenter.timeSelected(hourOfDay, minute)
        }
    }

    private fun getDateFromIntent(data: Intent?) {
        val year = data?.extras?.getInt(YEAR)
        val month = data?.extras?.getInt(MONTH)
        val day = data?.extras?.getInt(DAY)
        if (year != null && month != null && day != null) {
            addingTaskDialogPresenter.dateSelected(year, month, day)
        }
    }

    override fun showTimePickerDialog() {
        timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.setTargetFragment(this@AddingTaskDialogFragment, REQUEST_CODE_TIME)
        timePickerDialogFragment.show(
            requireActivity().supportFragmentManager,
            "TimePickerDialogFragment"
        )
    }


    override fun setDate(date: String) {
        binding.etDate.setText(date)
    }

    override fun setTime(time: String) {
        binding.etTime.setText(time)
    }

    override fun setUIWhenTitleEmpty() {
        positive.isEnabled = false
        binding.dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
    }

    override fun setUIWhenTitleNotEmpty(s: String) {
        positive.isEnabled = true
        binding.dialogTaskTitle.isErrorEnabled = false
    }

    override fun initPositiveButton() {
        positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
    }

    override fun closeTimeDialogFragment() {
        if (::timePickerDialogFragment.isInitialized) {
            timePickerDialogFragment.dismiss()
        }
    }

    override fun closeDateDialogFragment() {
        if (::datePickerDialogFragment.isInitialized) {
            datePickerDialogFragment.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideSoftKeyboard()
        _binding = null
    }

    private fun hideSoftKeyboard() {
        (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.etTitle.windowToken,
            0
        )
    }

    override fun setTitleFocus(hasFocus: Boolean) {
        Log.d("debug", "устанавливаем фокус на титл в AddingTaskDialogFragment = $hasFocus")
        if (hasFocus) {
            binding.etTitle.requestFocus()
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }
}