package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.utils.Utils
import java.util.*

class AddingTaskDialogFragment : DialogFragment() {

    private var addingTaskListener: AddingTaskListener? = null

    interface AddingTaskListener {
        fun onTaskAdded(newTask: ModelTask)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            addingTaskListener = context as AddingTaskListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AddingTaskListener")
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as Context)

        val container = View.inflate(activity, R.layout.dialog_task, null)

        builder.setTitle(R.string.dialog_title)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setView(container)

        val spinnerPriority: Spinner = container.findViewById(R.id.spinnerPriority)
        val etDate: EditText = container.findViewById(R.id.etDate)
        val etTime: EditText = container.findViewById(R.id.etTime)
        val etTitle: EditText = container.findViewById(R.id.etTitle)
        val dialogTaskTitle: TextInputLayout = container.findViewById(R.id.dialogTaskTitle)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1)
        Log.d("debug", "calendar = $calendar, Calendar.HOUR_OF_DAY = ${Calendar.HOUR_OF_DAY}, calendar.get(Calendar.HOUR_OF_DAY) + 1 = ${calendar.get(Calendar.HOUR_OF_DAY) + 1}")

        val modelTask = ModelTask()

        val priorityAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_spinner_dropdown_item,
                activity!!.resources.getStringArray(R.array.priority_array))
        spinnerPriority.adapter = priorityAdapter
        spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                modelTask.priority = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                modelTask.priority = 0
            }
        }

        etDate.setOnClickListener {
            if (etDate.length() == 0) {
                etDate.setText("")
            }

            val datePickerController = object : DatePickerController() {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    etDate.setText(Utils.getDate(calendar.timeInMillis))
                }
            }
            datePickerController.onCreateDialog(activity as Context).show()
        }

        etTime.setOnClickListener {
            if (etTime.length() == 0) {
                etTime.setText("")
            }

            val timePickerController = object : TimePickerController() {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    etTime.setText(Utils.getTime(calendar.timeInMillis))
                }
            }
            timePickerController.onCreateDialog(activity as Context).show()
        }

        builder.setPositiveButton(R.string.dialog_ok) { dialog, _ ->
            modelTask.title = etTitle.text.toString()
            if (etDate.length() != 0 || etTime.length() != 0) {
                modelTask.date = calendar.timeInMillis
                val alarmHelper = AlarmHelper.getInstance()
                alarmHelper.setAlarm(modelTask)
            }
            modelTask.status = ModelTask.STATUS_CURRENT
            addingTaskListener?.onTaskAdded(modelTask)
            dialog.dismiss()
            Log.d("debug", "model = " + modelTask.toString(modelTask))
        }
        builder.setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.cancel() }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener { dialog ->
            val positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
            if (etTitle.length() == 0) {
                positive.isEnabled = false
                dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
            }

            etTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isEmpty()) {
                        positive.isEnabled = false
                        dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
                    } else {
                        positive.isEnabled = true
                        dialogTaskTitle.isErrorEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })
        }
        return alertDialog
    }
}