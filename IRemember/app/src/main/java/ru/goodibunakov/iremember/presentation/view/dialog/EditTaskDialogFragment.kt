package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_task.*
import kotlinx.android.synthetic.main.dialog_task.view.*
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.utils.Utils
import java.util.*


class EditTaskDialogFragment : DialogFragment() {

    private var editingTaskListener: EditingTaskListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            editingTaskListener = context as EditingTaskListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implements EditingTaskListener")
        }
    }

    interface EditingTaskListener {
        fun onTaskEdited(updatedTask: ModelTask)
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

        val title: String?
        val date: Long
        val timestamp: Long
        val priority: Int
        val modelTask: ModelTask

        if (args != null) {
            title = args.getString("title")
            date = args.getLong("date")
            priority = args.getInt("priority")
            timestamp = args.getLong("timestamp")
            modelTask = ModelTask(title!!, date, priority, 0, timestamp)


            val builder = AlertDialog.Builder(activity as Context)

            val container = View.inflate(context, R.layout.dialog_task, null)

            builder.setTitle(R.string.editing_title)
            builder.setIcon(R.mipmap.ic_launcher)
            container.etTitle.setText(title)
            container.etTitle.setSelection(container.etTitle.length())
            if (date != 0L) {
                container.etDate.setText(Utils.getDate(date))
                container.etTime.setText(Utils.getTime(timestamp))
            }

            container.dialogTaskTitle.hint = resources.getString(R.string.task_title)
            container.dialogTaskDate.hint = resources.getString(R.string.task_date)
            container.dialogTaskTime.hint = resources.getString(R.string.task_time)

            builder.setView(container)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1)

            val priorityAdapter = ArrayAdapter<String>(activity!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    activity!!.resources.getStringArray(R.array.priority_array))

            container.spinnerPriority.adapter = priorityAdapter
            container.spinnerPriority.setSelection(priority)
            container.spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    modelTask.priority = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    modelTask.priority = 0
                }
            }

            if (container.etDate.length() != 0 || container.etTime.length() != 0) {
                calendar.timeInMillis = date
            }

            container.etDate.setOnClickListener {
                if (container.etDate.length() == 0) {
                    container.etDate.setText("")
                }

                val datePickerController = object : DatePickerController() {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        container.etDate.setText(Utils.getDate(calendar.timeInMillis))
                    }
                }
                datePickerController.onCreateDialog(activity as Context).show()
            }

            container.etTime.setOnClickListener {

                if (container.etTime.length() == 0) {
                    container.etTime.setText("")
                }

                val timePickerController = object : TimePickerController() {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        container.etTime.setText(Utils.getTime(calendar.timeInMillis))
                    }
                }
                timePickerController.onCreateDialog(activity as Context).show()
            }

            builder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                modelTask.title = container.etTitle.text.toString()
                if (container.etDate.length() != 0 || container.etTime.length() != 0) {
                    modelTask.date = calendar.timeInMillis
                    val alarmHelper = AlarmHelper.getInstance()
                    alarmHelper.setAlarm(modelTask)
                }
                modelTask.status = ModelTask.STATUS_CURRENT
                editingTaskListener?.onTaskEdited(modelTask)
                dialog.dismiss()
                Log.d("debug", "model = " + modelTask.toString(modelTask))
            }
            builder.setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.cancel() }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener { dialog ->
                val positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                if (container.etTitle.length() == 0) {
                    positive.isEnabled = false
                    dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
                }
                container.etTitle.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        if (s.isEmpty()) {
                            positive.isEnabled = false
                            container.dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
                        } else {
                            positive.isEnabled = true
                            container.dialogTaskTitle.isErrorEnabled = false
                        }
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })
            }

            return alertDialog
        } else {
            return super.onCreateDialog(savedInstanceState)
        }
    }
}