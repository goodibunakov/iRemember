package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
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
import ru.goodibunakov.iremember.utils.Utils

class EditTaskDialogFragment : MvpAppCompatDialogFragment(), EditTaskDialogFragmentView {

    lateinit var container: View
    lateinit var positive: Button

    @InjectPresenter
    lateinit var editTaskDialogPresenter: EditTaskDialogPresenter

    @ProvidePresenter
    fun providePresenter(): EditTaskDialogPresenter {
        return EditTaskDialogPresenter(RememberApp.databaseRepository)
    }

    private val timePickerController = object : TimePickerController() {
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            editTaskDialogPresenter.timeSelected(hourOfDay, minute)
        }
    }

    private val datePickerController = object : DatePickerController() {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            editTaskDialogPresenter.dateSelected(year, month, dayOfMonth)
        }
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

            val builder = AlertDialog.Builder(activity as Context)
            container = View.inflate(context, R.layout.dialog_task, null)

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

            val priorityAdapter = ArrayAdapter<String>(activity!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    activity!!.resources.getStringArray(R.array.priority_array))

            container.spinnerPriority.adapter = priorityAdapter
            editTaskDialogPresenter.setPriorityToUI()

            container.spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    editTaskDialogPresenter.itemSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    editTaskDialogPresenter.nothingSelected()
                }
            }

            container.etDate.setOnClickListener {
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

            builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                editTaskDialogPresenter.okClicked(container.etTitle.text.toString())

                if (container.etDate.length() != 0 || container.etTime.length() != 0) {
                    editTaskDialogPresenter.setDateToModel()
//                    val alarmHelper = AlarmHelper.getInstance()
//                    alarmHelper.setAlarm(modelTask)
                }
                editTaskDialogPresenter.updateTask()
                editTaskDialogPresenter.dismissDialog()

            }
            builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> editTaskDialogPresenter.cancelDialog() }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener { dialog ->
                positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                if (container.etTitle.length() == 0) {
                    editTaskDialogPresenter.titleEmpty()
                }
                container.etTitle.addTextChangedListener(object : MyTextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        if (s.isEmpty()) {
                            editTaskDialogPresenter.titleEmpty()
                        } else {
                            editTaskDialogPresenter.titleNotEmpty()
                        }
                    }
                })
            }

            return alertDialog
        } else {
            return super.onCreateDialog(savedInstanceState)
        }
    }

    override fun setDateToUI(date: Long) {
        container.etDate.setText(Utils.getDate(date))
        Log.d("debug", "setDateToUI = " + Utils.getDate(date))
    }

    override fun setTimeToUI(time: Long) {
        container.etTime.setText(Utils.getTime(time))
        Log.d("debug", "setTimeToUI = " + Utils.getTime(time))
    }

    override fun setEmptyDateToEditText() {
        container.etDate.setText("")
    }

    override fun showDateController() {
        datePickerController.onCreateDialog(activity as Context).show()
    }

    override fun setTimeToEditText(time: String) {
        container.etTime.setText(time)
    }

    override fun showTimeController() {
        timePickerController.onCreateDialog(activity as Context).show()
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

    override fun setPriorityToUI(priority: Int) {
        container.spinnerPriority.setSelection(priority)
    }
}