package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_task.*
import kotlinx.android.synthetic.main.dialog_task.view.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.EditTaskDialogPresenter
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_ONLY
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_TIME_ONLY
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_NEGATIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_POSITIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_DATE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_TIME

class EditTaskDialogFragment : MvpAppCompatDialogFragment(), EditTaskDialogFragmentView {

    private lateinit var container: View
    private lateinit var positive: Button
    private lateinit var timePickerDialogFragment: TimePickerDialogFragment
    private lateinit var datePickerDialogFragment: DatePickerDialogFragment

    @InjectPresenter
    lateinit var editTaskDialogPresenter: EditTaskDialogPresenter

    @ProvidePresenter
    fun providePresenter(): EditTaskDialogPresenter {
        return EditTaskDialogPresenter(RememberApp.databaseRepository)
    }

    companion object {
        fun newInstance(task: ModelTask): EditTaskDialogFragment {
            val editTaskDialogFragment = EditTaskDialogFragment()

            val args = Bundle()
            args.putString("title", task.title)
            args.putLong("date", task.date)
            args.putInt("priority", task.priority)
            args.putLong("timestamp", task.timestamp)

            editTaskDialogFragment.arguments = args
            return editTaskDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments

        if (args != null) {
            val title = args.getString("title")!!
            val date = args.getLong("date")
            val priority = args.getInt("priority")
            val timestamp = args.getLong("timestamp")
            editTaskDialogPresenter.onCreateDialog(title, date, priority, timestamp)

            val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
            container = View.inflate(context, R.layout.dialog_task, null)
            isCancelable = false

            builder.setTitle(R.string.editing_title)
            builder.setIcon(R.mipmap.ic_launcher)
            editTaskDialogPresenter.initTitle()

            if (date != 0L) {
                editTaskDialogPresenter.setDateToUI()
                editTaskDialogPresenter.setTimeToUI()
            }

            container.dialogTaskTitle.hint = resources.getString(R.string.task_title)
            container.dialogTaskDate.hint = resources.getString(R.string.task_date)
            container.dialogTaskTime.hint = resources.getString(R.string.task_time)

            builder.setView(container)

            builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                editTaskDialogPresenter.okClicked(container.etTitle.text.toString().trim())

                if (container.etDate.length() != 0 || container.etTime.length() != 0) {
                    editTaskDialogPresenter.setDateToModel()
                }
                editTaskDialogPresenter.updateTask()
                editTaskDialogPresenter.dismissDialog()
            }
            builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> editTaskDialogPresenter.cancelDialog() }


            val priorityAdapter = ArrayAdapter<String>(activity!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    activity!!.resources.getStringArray(R.array.priority_array))

            container.spinnerPriority.adapter = priorityAdapter
            editTaskDialogPresenter.setPriorityToUI()

            container.spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    editTaskDialogPresenter.itemSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    editTaskDialogPresenter.nothingSelected()
                }
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener { dialog ->
                positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)

                if (container.etTitle.length() == 0) {
                    editTaskDialogPresenter.titleEmpty()
                }

                container.etTitle.addTextChangedListener(object : MyTextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        editTaskDialogPresenter.onTextChanged(s)
                    }
                })

                container.etTitle.isFocusableInTouchMode = true
                editTaskDialogPresenter.getTitleHasFocus()
                container.etTitle.onFocusChangeListener =
                        View.OnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                editTaskDialogPresenter.setTitleHasFocus(hasFocus)
                            }
                        }

                container.etDate.setOnClickListener {
                    clearTitleFocus()
                    if (container.etDate.length() == 0) {
                        editTaskDialogPresenter.setEmptyDateToEditText()
                    }
                    editTaskDialogPresenter.dateClicked()
                }

                container.etTime.setOnClickListener {
                    if (container.etTime.length() == 0) {
                        editTaskDialogPresenter.setEmptyTimeToEditText()
                    }
                    editTaskDialogPresenter.timeClicked()
                }
            }

            return alertDialog
        } else {
            return super.onCreateDialog(savedInstanceState)
        }
    }

    override fun setDateToUI(date: Long) {
        container.etDate.setText(DateUtils.getDate(date, FORMAT_DATE_ONLY))
    }

    override fun setTimeToUI(time: Long) {
        container.etTime.setText(DateUtils.getDate(time, FORMAT_TIME_ONLY))
    }

    override fun setEmptyDateToEditText() {
        container.etDate.setText("")
    }

    override fun showDatePickerDialog() {
        datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.setTargetFragment(this@EditTaskDialogFragment, REQUEST_CODE_DATE)
        datePickerDialogFragment.show(activity!!.supportFragmentManager, "DatePickerDialogFragment")
    }

    override fun showTimePickerDialog() {
        timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.setTargetFragment(this@EditTaskDialogFragment, REQUEST_CODE_TIME)
        timePickerDialogFragment.show(activity!!.supportFragmentManager, "TimePickerDialogFragment")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TIME) {
                when (data?.extras?.get(AddingTaskDialogFragment.TYPE)) {
                    KEY_POSITIVE -> {
                        getTimeFromIntent(data)
                        editTaskDialogPresenter.positiveTimeClicked()
                    }
                    KEY_NEGATIVE -> editTaskDialogPresenter.negativeTimeClicked()
                }
            } else if (requestCode == REQUEST_CODE_DATE) {
                when (data?.extras?.get(AddingTaskDialogFragment.TYPE)) {
                    KEY_POSITIVE -> {
                        getDateFromIntent(data)
                        editTaskDialogPresenter.positiveDateClicked()
                    }
                    KEY_NEGATIVE -> editTaskDialogPresenter.negativeDateClicked()
                }
            }
        }
    }

    private fun getTimeFromIntent(data: Intent?) {
        val hourOfDay = data?.extras?.get(AddingTaskDialogFragment.HOUR) as Int
        val minute = data.extras?.get(AddingTaskDialogFragment.MINUTE) as Int
        editTaskDialogPresenter.timeSelected(hourOfDay, minute)
    }

    private fun getDateFromIntent(data: Intent?) {
        val year = data?.extras?.get(AddingTaskDialogFragment.YEAR) as Int
        val month = data.extras?.get(AddingTaskDialogFragment.MONTH) as Int
        val day = data.extras?.get(AddingTaskDialogFragment.DAY) as Int
        editTaskDialogPresenter.dateSelected(year, month, day)
    }

    override fun setTimeToEditText(time: String) {
        container.etTime.setText(time)
    }

    override fun setDateToEditText(date: String) {
        container.etDate.setText(date)
    }

    override fun setEmptyTimeToEditText() {
        container.etTime.setText("")
    }

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun cancelDialog() {
        dialog?.cancel()
    }

    override fun setUIWhenTitleEmpty() {
        positive.isEnabled = false
        dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
    }

    override fun setUIWhenTitleNotEmpty() {
        positive.isEnabled = true
        container.dialogTaskTitle.isErrorEnabled = false
    }

    override fun initTitle(title: String) {
        container.etTitle.setText(title)
        container.etTitle.setSelection(container.etTitle.length())
    }

    override fun setTitleFocus(hasFocus: Boolean) {
        if (hasFocus) {
            container.etTitle.requestFocus()
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    override fun setPriorityToUI(priority: Int) {
        container.spinnerPriority.setSelection(priority)
    }

    override fun onDestroy() {
        hideSoftKeyboard()
        super.onDestroy()
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

    private fun clearTitleFocus() {
        if (container.etTitle.hasFocus()) {
            container.etTitle.clearFocus()
            hideSoftKeyboard()
            editTaskDialogPresenter.setTitleHasFocus(false)
        }
    }

    private fun hideSoftKeyboard() {
        (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(container.etTitle.windowToken, 0)
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }
}