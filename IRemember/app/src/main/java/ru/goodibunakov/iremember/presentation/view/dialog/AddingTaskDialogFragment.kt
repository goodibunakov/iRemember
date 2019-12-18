package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_task.view.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.presenter.AddingTaskDialogPresenter


class AddingTaskDialogFragment : MvpAppCompatDialogFragment(), AddingTaskDialogFragmentView {

    private lateinit var container: View
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

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("debug", "AddingTaskDialogFragment onCreateDialog")
        val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
        container = View.inflate(context, R.layout.dialog_task, null)
        isCancelable = false

        builder.setTitle(R.string.dialog_title)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setView(container)

        addingTaskDialogPresenter.dialogCreated()

        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            addingTaskDialogPresenter.okClicked(container.etTitle.text.toString().trim())
            if (container.etDate.text.isNotEmpty() || container.etTime.text.isNotEmpty()) {
                addingTaskDialogPresenter.setDateToModel()
            }
            addingTaskDialogPresenter.saveTask()
            addingTaskDialogPresenter.dismissDialog()
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            addingTaskDialogPresenter.cancelDialog()
        }


        val priorityAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_spinner_dropdown_item,
                activity!!.resources.getStringArray(R.array.priority_array))
        container.spinnerPriority.adapter = priorityAdapter
        container.spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                addingTaskDialogPresenter.itemSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                addingTaskDialogPresenter.nothingSelected()
            }
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            addingTaskDialogPresenter.initPositiveButton()

            if (container.etTitle.text.isBlank()) {
                addingTaskDialogPresenter.titleEmpty()
            }

            container.etTitle.addTextChangedListener(object : MyTextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    addingTaskDialogPresenter.onTextChanged(s)
                }
            })

            container.etTitle.isFocusableInTouchMode = true
            addingTaskDialogPresenter.getTitleHasFocus()
            container.etTitle.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            addingTaskDialogPresenter.setTitleHasFocus(hasFocus)
                        }
                    }

            container.etDate.setOnClickListener {
                clearTitleFocus()
                if (container.etDate.length() == 0) {
                    addingTaskDialogPresenter.dateIsEmpty()
                }
                addingTaskDialogPresenter.editTextDateClicked()
            }

            container.etTime.setOnClickListener {
                clearTitleFocus()
                if (container.etTime.length() == 0) {
                    addingTaskDialogPresenter.timeIsEmpty()
                }
                addingTaskDialogPresenter.editTextTimeClicked()
            }
        }
        return alertDialog
    }

    private fun clearTitleFocus() {
        if (container.etTitle.hasFocus()) {
            container.etTitle.clearFocus()
            hideSoftKeyboard()
            addingTaskDialogPresenter.setTitleHasFocus(false)
        }
    }

    override fun dismissDialog() {
        clearListeners()
        dialog?.dismiss()
    }

    override fun cancelDialog() {
        clearListeners()
        dialog?.cancel()
    }

    private fun clearListeners() {
        container.etDate.setOnClickListener(null)
        container.etTitle.setOnClickListener(null)
        container.etTitle.onFocusChangeListener = null
        container.etTitle.addTextChangedListener(null)
        container.etTime.setOnClickListener(null)
        container.spinnerPriority.onItemSelectedListener = null
    }

    override fun setEmptyToDateEditText() {
        container.etDate.setText("")
    }

    override fun setEmptyToTimeEditText() {
        container.etTime.setText("")
    }

    override fun showDatePickerDialog() {
        datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.setTargetFragment(this@AddingTaskDialogFragment, REQUEST_CODE_DATE)
        datePickerDialogFragment.show(activity!!.supportFragmentManager, "DatePickerDialogFragment")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TIME) {
                when (data?.extras?.get(TYPE)) {
                    KEY_POSITIVE -> {
                        getTimeFromIntent(data)
                        addingTaskDialogPresenter.positiveTimeClicked()
                    }
                    KEY_NEGATIVE -> addingTaskDialogPresenter.negativeTimeClicked()
                }
            } else if (requestCode == REQUEST_CODE_DATE) {
                when (data?.extras?.get(TYPE)) {
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
        val hourOfDay = data?.extras?.get(HOUR) as Int
        val minute = data.extras?.get(MINUTE) as Int
        addingTaskDialogPresenter.timeSelected(hourOfDay, minute)
    }

    private fun getDateFromIntent(data: Intent?) {
        val year = data?.extras?.get(YEAR) as Int
        val month = data.extras?.get(MONTH) as Int
        val day = data.extras?.get(DAY) as Int
        addingTaskDialogPresenter.dateSelected(year, month, day)
    }

    override fun showTimePickerDialog() {
        timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.setTargetFragment(this@AddingTaskDialogFragment, REQUEST_CODE_TIME)
        timePickerDialogFragment.show(activity!!.supportFragmentManager, "TimePickerDialogFragment")
    }


    override fun setDate(date: String) {
        container.etDate.setText(date)
    }

    override fun setTime(time: String) {
        container.etTime.setText(time)
    }

    override fun setUIWhenTitleEmpty() {
        positive.isEnabled = false
        container.dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
    }

    override fun setUIWhenTitleNotEmpty(s: String) {
        positive.isEnabled = true
        container.dialogTaskTitle.isErrorEnabled = false
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

    override fun onDestroy() {
        hideSoftKeyboard()
        super.onDestroy()
    }

    private fun hideSoftKeyboard() {
        (activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(container.etTitle.windowToken, 0)
    }

    override fun setTitleFocus(hasFocus: Boolean) {
        Log.d("debug", "устанавливаем фокус на титл в AddingTaskDialogFragment = $hasFocus")
        if (hasFocus) {
            container.etTitle.requestFocus()
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            (activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }
}